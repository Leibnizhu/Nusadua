package io.github.leibnizhu.nusadua;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import io.github.leibnizhu.nusadua.annotation.MethodOverload;

import javax.annotation.processing.Messager;
import java.util.*;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.*;

/**
 * @author Leibniz on 2020/6/19 3:59 PM
 */
class MethodOverloadTranslator extends TreeTranslator {
    public static final String METHOD_OVERLOAD_CLASS_NAME = MethodOverload.class.getSimpleName();
    private final TreeMaker treeMaker;
    private final Messager messager;
    private final Names names;

    public MethodOverloadTranslator(TreeMaker treeMaker, Messager messager, Names names) {
        this.treeMaker = treeMaker;
        this.messager = messager;
        this.names = names;
    }

    /**
     * just override visitClassDef(), no need to override visitMethodDef().
     * Because when we visitMethodDef, we can read annotation of method, but we cannot get it's class and add method for class.
     * So we just override visitClassDef(), traverse its methods(defs), and add new overload method to its defs.
     * When visitClassDef(), just keep default implement and it will visit sub-definition recursively.
     *
     * @author Leibniz
     */
    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClass) {
        String classFullName = Optional.ofNullable(jcClass.sym).map(s -> s.fullname).map(Object::toString).orElse("");
        Queue<JCTree.JCMethodDecl> newMethods = new LinkedList<>();
        Map<String, Set<String>> methodSignMap = new HashMap<>(); //Map<MethodName, Set<ParameterTypeList>>
        jcClass.defs.stream()
                .filter(k -> k.getKind().equals(Tree.Kind.METHOD))
                .map(tree -> (JCTree.JCMethodDecl) tree)
                .forEach(jcMethod -> {
                    JCTree.JCModifiers modifiers = jcMethod.getModifiers();
                    String methodName = jcMethod.name.toString();
                    String curMethodSign = calMethodSign(jcMethod.params);
                    Set<String> curMethodSignSet = methodSignMap.computeIfAbsent(methodName, name -> new HashSet<>());
                    curMethodSignSet.add(curMethodSign);
                    messager.printMessage(NOTE, "current Method:" + jcMethod.name + "(" + jcMethod.params + ")");
                    List<JCTree.JCAnnotation> methodOverloadAnnotationList = List.from(modifiers.annotations.stream()
                            .filter(annotation -> METHOD_OVERLOAD_CLASS_NAME.equals(annotation.getAnnotationType().toString()))
                            .collect(Collectors.toList()));
                    String errMsgTmpl = String.format("%s.%s(%s) method has a MethodOverload annotation's ERROR,Annotation definition: ",
                            classFullName, methodName, jcMethod.params);
                    Map<String, Object> defaultValueMap = new HashMap<>();
                    methodOverloadAnnotationList = validateAndParseAnnotation(jcMethod, methodOverloadAnnotationList, defaultValueMap, errMsgTmpl);
                    messager.printMessage(NOTE, "All available MethodOverload annotations:" + methodOverloadAnnotationList);
                    genNewMethods(defaultValueMap, jcMethod, newMethods, curMethodSignSet, errMsgTmpl);
                });
        messager.printMessage(NOTE, "new Methods:" + newMethods);
        for (JCTree.JCMethodDecl method : newMethods) {
            jcClass.defs = jcClass.defs.append(method);
        }
        super.visitClassDef(jcClass);
    }

    /**
     * validate all MethodOverload annotations, and parse their field name and default value into defaultValueMap
     *
     * @param jcMethod                     method to process
     * @param methodOverloadAnnotationList all MethodOverload annotations
     * @param defaultValueMap              to store Map<fieldName, defaultValue>
     * @param errMsgTmpl                   error message template
     * @return all available MethodOverload annotations
     * @author Leibniz
     */
    private List<JCTree.JCAnnotation> validateAndParseAnnotation(JCTree.JCMethodDecl jcMethod, List<JCTree.JCAnnotation> methodOverloadAnnotationList,
                                                                 Map<String, Object> defaultValueMap, String errMsgTmpl) {
        List<JCTree.JCAnnotation> result = List.nil();
        for (JCTree.JCAnnotation annotation : methodOverloadAnnotationList) {
            String errMsg = errMsgTmpl + annotation + ", ERROR: ";
            messager.printMessage(NOTE, "current Annotation:" + annotation.toString() + "," + annotation.getArguments());
            Pair<String, Object> defaultFieldPair = validateAnnotation(jcMethod.params, annotation, errMsg);
            if (defaultFieldPair != null) {
                defaultValueMap.put(defaultFieldPair.fst, defaultFieldPair.snd);
                result = result.append(annotation);
            }
        }
        return result;
    }

    private static final Map<String, String> TYPE_TO_ANNOTATION_PROPERTY = new HashMap<>();

    static {
        TYPE_TO_ANNOTATION_PROPERTY.put("integer", "defaultInt");
        TYPE_TO_ANNOTATION_PROPERTY.put("integer[]", "defaultIntArr");
        TYPE_TO_ANNOTATION_PROPERTY.put("boolean", "defaultBool");
        TYPE_TO_ANNOTATION_PROPERTY.put("boolean[]", "defaultBoolArr");
        TYPE_TO_ANNOTATION_PROPERTY.put("character", "defaultChar");
        TYPE_TO_ANNOTATION_PROPERTY.put("character[]", "defaultCharArr");
    }

    /**
     * Validate the annotation,find field to overload, and its default value
     *
     * @param params     parameters of method
     * @param annotation annotation
     * @param errMsg     error message
     * @return Pair<fieldName, defaultValue>
     * @author Leibniz
     */
    private Pair<String, Object> validateAnnotation(List<JCTree.JCVariableDecl> params, JCTree.JCAnnotation annotation, String errMsg) {
        Map<String, Object> annotationValueMap = annotation.args.stream()
                .filter(arg -> arg instanceof JCTree.JCAssign)
                .map(arg -> (JCTree.JCAssign) arg)
                .filter(assign -> assign.lhs instanceof JCTree.JCIdent &&
                        (assign.rhs instanceof JCTree.JCLiteral || assign.rhs instanceof JCTree.JCNewArray || assign.rhs instanceof JCTree.JCTypeCast))
                .collect(Collectors.toMap(
                        assign -> ((JCTree.JCIdent) assign.lhs).getName().toString(),
                        assign -> {
                            if (assign.rhs instanceof JCTree.JCLiteral) {
                                return ((JCTree.JCLiteral) assign.rhs).getValue();
                            } else if (assign.rhs instanceof JCTree.JCNewArray) {
//                                return ((JCTree.JCNewArray) assign.rhs).elems.stream().map(element -> ((JCTree.JCLiteral) element).value).toArray();
                                return ((JCTree.JCNewArray) assign.rhs).elems;
                            } else if (assign.rhs instanceof JCTree.JCTypeCast) {
                                return ((JCTree.JCLiteral) ((JCTree.JCTypeCast) assign.rhs).expr).value;
                            } else {
                                //impossible
                                return null;
                            }
                        }));
        messager.printMessage(NOTE, "parsed Annotation:" + annotationValueMap);
        Object fieldNameObj = annotationValueMap.get("field");
        if (!(fieldNameObj instanceof String)) {
            messager.printMessage(MANDATORY_WARNING, errMsg + "has no 'field' property! It will be ignore...");
            return null;
        }
        String fieldName = (String) fieldNameObj;
        Optional<JCTree.JCVariableDecl> fieldParamOpt = params.stream()
                .filter(p -> fieldName.equals(p.name.toString())).findFirst();
        if (!fieldParamOpt.isPresent()) {
            messager.printMessage(MANDATORY_WARNING,
                    String.format(errMsg + "field '%s' is not presented in method parameters! it will be ignore...", fieldName));
            return null;
        }
        JCTree.JCVariableDecl fieldParam = fieldParamOpt.get();
        String fieldType = fieldParam.vartype.toString();
        String defaultValueKey = TYPE_TO_ANNOTATION_PROPERTY.getOrDefault(fieldType.toLowerCase(),
                "default" + fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1).replace("[]", "Arr"));
        Object defaultValueObj = annotationValueMap.get(defaultValueKey);
        if (defaultValueObj == null) {
            Object defaultNullObj = annotationValueMap.get("defaultNull");
            if (defaultNullObj instanceof Boolean) {
                defaultValueObj = null;
            } else {
                messager.printMessage(MANDATORY_WARNING, String.format(
                        errMsg + "field '%s''s default value(property name=%s) doesn't set! It will be ignore...",
                        fieldName, defaultValueKey));
                return null;
            }
        }

        return new Pair<>(fieldName, defaultValueObj);
    }

    /**
     * generate all new methods by MethodOverload annotations
     *
     * @param defaultValueMap  Map<fieldName, defaultValue>
     * @param jcMethod         method to process
     * @param newMethods       store all new generated methods
     * @param curMethodSignSet store all method signatures
     * @param errMsgTmpl       error message template
     * @author Leibniz
     */
    private void genNewMethods(Map<String, Object> defaultValueMap, JCTree.JCMethodDecl jcMethod,
                               Queue<JCTree.JCMethodDecl> newMethods, Set<String> curMethodSignSet, String errMsgTmpl) {
        List<String> allElement = List.from(defaultValueMap.keySet());
        Map<Integer, java.util.List<List<String>>> allCombinationMap = allElement.isEmpty() ? new HashMap<>() :
                calCombinations(0, List.nil(), new java.util.LinkedList<>(), allElement)
                        .stream()
                        .filter(combination -> !combination.isEmpty()) //drop empty default value method(equals to original method)
                        .collect(Collectors.toMap(List::size, combination -> {
                            java.util.List<List<String>> newSize = new LinkedList<>();
                            newSize.add(combination);
                            return newSize;
                        }, (sizeList1, sizeList2) -> {
                            sizeList1.addAll(sizeList2);
                            return sizeList1;
                        }));
        if (allCombinationMap.isEmpty()) {
            return;
        }
        LinkedList<Integer> sizeList = new LinkedList<>(allCombinationMap.keySet());
        Collections.sort(sizeList); //keep process size==1's method first, must not method sign conflict
        for (Integer size : sizeList) {
            boolean errorWhenMethodSignConflict = size == 1;
            for (List<String> fields : allCombinationMap.get(size)) {
                List<JCTree.JCVariableDecl> newParams = genNewParamList(jcMethod.getParameters(), fields, curMethodSignSet, errMsgTmpl, errorWhenMethodSignConflict);
                if (newParams == null) {
                    continue;
                }
                List<JCTree.JCStatement> newStatements = genBodyStats(jcMethod, defaultValueMap, fields);
                JCTree.JCModifiers flagsOnly = treeMaker.Modifiers(jcMethod.mods.flags);
                JCTree.JCBlock codeBlock = treeMaker.Block(jcMethod.body.flags, newStatements);
                newMethods.add(treeMaker.MethodDef(flagsOnly, jcMethod.name, jcMethod.restype, jcMethod.getTypeParameters(),
                        newParams, jcMethod.getThrows(), codeBlock, jcMethod.defaultValue));
            }
        }
    }

    /**
     * calculate all combinations of MethodOverload fields
     *
     * @param n          current process level
     * @param curSet     current fields
     * @param curResult  current set of combinations
     * @param allElement all MethodOverload fields
     * @return List<List < fieldName>>
     * @author Leibniz
     */
    private java.util.List<List<String>> calCombinations(int n, List<String> curSet, java.util.List<List<String>> curResult, List<String> allElement) {
        if (allElement.size() == n) {
            curResult.add(curSet);
        } else {
            java.util.List<List<String>> tmpRes = calCombinations(n + 1, curSet.append(allElement.get(n)), curResult, allElement); //当前数字选择加入子集
            calCombinations(n + 1, curSet, tmpRes, allElement); //当前数字选择不加入子集
        }
        return curResult;
    }

    /**
     * calculate method signature by method parameters, join by '_'
     *
     * @param parameterList method parameter list
     * @return method signature
     * @author Leibniz
     */
    private String calMethodSign(List<JCTree.JCVariableDecl> parameterList) {
        return parameterList.stream()
                .map(variable -> variable.vartype.toString())
                .collect(Collectors.joining("_"));
    }

    /**
     * @param originParams                parameter list of method which to be overloaded
     * @param fieldNames                  all fields with default value
     * @param methodSignSet               store all method signatures
     * @param errMsg                      error message template
     * @param errorWhenMethodSignConflict when method signature, errorWhenMethodSignConflict determine whether throw compile error
     * @return new method parameter list, excluded fields with default value
     * @author Leibniz
     */
    private List<JCTree.JCVariableDecl> genNewParamList(List<JCTree.JCVariableDecl> originParams, List<String> fieldNames,
                                                        Set<String> methodSignSet, String errMsg, boolean errorWhenMethodSignConflict) {
        List<JCTree.JCVariableDecl> newParamList = List.from(originParams.stream()
                .filter(variable -> !fieldNames.contains(variable.name.toString()))
                .collect(Collectors.toList()));
        String newMethodSign = calMethodSign(newParamList);
        if (methodSignSet.contains(newMethodSign)) {
            messager.printMessage(errorWhenMethodSignConflict ? ERROR : MANDATORY_WARNING,
                    String.format(errMsg + "method with same signature (%s) already existed! Can not continue!", newParamList));
            return null;
        }
        methodSignSet.add(newMethodSign);
        return newParamList;
    }

    /**
     * @param jcMethod        method to process
     * @param defaultValueMap Map<fieldName, defaultValue>
     * @param fields          all fields with default value
     * @return new overload method's body, just one line of return (or even no return, just call original method)
     * @author Leibniz
     */
    private List<JCTree.JCStatement> genBodyStats(JCTree.JCMethodDecl jcMethod, Map<String, Object> defaultValueMap, List<String> fields) {
        List<JCTree.JCVariableDecl> originParams = jcMethod.params;
        Name methodName = jcMethod.name;
        JCTree.JCExpression returnType = jcMethod.restype;

        List<JCTree.JCExpression> argList = List.nil();
        for (JCTree.JCVariableDecl originParam : originParams) {
            String argName = originParam.name.toString();
            if (fields.contains(argName)) { //argument with default value
                Object defaultValue = defaultValueMap.get(argName);
                JCTree.JCExpression defaultValueArg;
                boolean needCast = true;
                if (defaultValue == null) {
                    defaultValueArg = treeMaker.Literal(TypeTag.BOT, null);
                    needCast = false;
                } else if (defaultValue.getClass().isArray()) {
                    defaultValueArg = treeMaker.Literal(TypeTag.ARRAY, defaultValue); //FIXME
                } else if (defaultValue instanceof List) {
                    JCTree.JCExpression arrayType = originParam.vartype instanceof JCTree.JCArrayTypeTree ?
                            ((JCTree.JCArrayTypeTree) originParam.vartype).elemtype : null;
                    defaultValueArg = treeMaker.NewArray(arrayType, List.nil(), (List<JCTree.JCExpression>) defaultValue);
                } else if (defaultValue instanceof Character) {
                    int charIntValue = (Character) defaultValue;
                    defaultValueArg = treeMaker.Literal(charIntValue);
                } else {
                    defaultValueArg = treeMaker.Literal(defaultValue);
                }
                Type castType = originParam.vartype.type;
                if (Short.class.getName().equals(castType.toString())) {
                    castType = new Type.JCPrimitiveType(TypeTag.SHORT, null).constType(0);
                } else if (Character.class.getName().equals(castType.toString())) {
                    castType = new Type.JCPrimitiveType(TypeTag.CHAR, null).constType(0);
                } else if (Byte.class.getName().equals(castType.toString())) {
                    castType = new Type.JCPrimitiveType(TypeTag.BYTE, null).constType(0);
                }
                if (needCast) {
                    argList = argList.append(treeMaker.TypeCast(castType, defaultValueArg));
                } else {
                    argList = argList.append(defaultValueArg);
                }
            } else {
                argList = argList.append(memberAccess(argName));
            }
        }
        JCTree.JCExpression callOriginMethod = treeMaker.Apply(List.nil(), memberAccess(methodName.toString()), argList);
        if ("void".equals(returnType.type.toString())) {
            return List.of(treeMaker.Exec(callOriginMethod));
        } else {
            return List.of(treeMaker.Return(callOriginMethod));
        }
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }
}
