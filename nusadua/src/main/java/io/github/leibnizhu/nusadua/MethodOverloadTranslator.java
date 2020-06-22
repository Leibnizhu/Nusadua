package io.github.leibnizhu.nusadua;

import com.sun.source.tree.Tree;
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

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethod) {
        super.visitMethodDef(jcMethod);
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClass) {
        String classFullName = jcClass.sym.fullname.toString();
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
                    for (JCTree.JCAnnotation annotation : modifiers.annotations) {
                        if (!METHOD_OVERLOAD_CLASS_NAME.equals(annotation.getAnnotationType().toString())) {
                            continue;
                        }
                        String errMsg = String.format("%s.%s(%s) method has a MethodOverload annotation's ERROR,Annotation definition: %s: ",
                                classFullName, methodName, jcMethod.params, annotation);
                        messager.printMessage(NOTE, "current Annotation:" + annotation.toString() + "," + annotation.getArguments());
                        JCTree.JCModifiers flagsOnly = treeMaker.Modifiers(modifiers.flags);
                        Pair<String, Object> defaultFieldPair = validateAnnotation(jcMethod.params, annotation, errMsg);
                        if (defaultFieldPair == null) {
                            continue;
                        }
                        List<JCTree.JCVariableDecl> newParams = genNewParamList(jcMethod.getParameters(), defaultFieldPair.fst, curMethodSignSet, errMsg);
                        if (newParams == null) {
                            continue;
                        }
                        List<JCTree.JCStatement> newStatements = genBodyStats(jcMethod.params, jcMethod.name, defaultFieldPair);
                        if (newStatements == null) {
                            continue;
                        }
                        JCTree.JCBlock codeBlock = treeMaker.Block(jcMethod.body.flags, newStatements); //TODO 新建一句，调用原方法；返回空则return
                        newMethods.add(treeMaker.MethodDef(flagsOnly, jcMethod.name, jcMethod.restype, jcMethod.getTypeParameters(),
                                newParams, jcMethod.getThrows(), codeBlock, jcMethod.defaultValue));
                    }
                });
        messager.printMessage(NOTE, "new Methods:" + newMethods);
        for (JCTree.JCMethodDecl method : newMethods) {
            jcClass.defs = jcClass.defs.append(method);
        }
        super.visitClassDef(jcClass);
    }

    private String calMethodSign(List<JCTree.JCVariableDecl> parameterList) {
        return parameterList.stream()
                .map(variable -> variable.vartype.toString())
                .collect(Collectors.joining("_"));
    }

    /**
     * Validate the annotation,find field to overload, and its default value
     *
     * @param params     parameters of method
     * @param annotation annotation
     * @param errMsg     error message
     * @return Pair<fieldName, defaultValue>
     */
    private Pair<String, Object> validateAnnotation(List<JCTree.JCVariableDecl> params, JCTree.JCAnnotation annotation, String errMsg) {
        Map<String, Object> annotationValueMap = annotation.args.stream()
                .filter(arg -> arg instanceof JCTree.JCAssign)
                .map(arg -> (JCTree.JCAssign) arg)
                .filter(assign -> assign.lhs instanceof JCTree.JCIdent && assign.rhs instanceof JCTree.JCLiteral)
                .collect(Collectors.toMap(
                        assign -> ((JCTree.JCIdent) assign.lhs).getName().toString(),
                        assign -> ((JCTree.JCLiteral) assign.rhs).getValue()));
        messager.printMessage(NOTE, "parsed Annotation:" + annotationValueMap);
        Object fieldNameObj = annotationValueMap.get("field");
        if (!(fieldNameObj instanceof String)) {
            messager.printMessage(MANDATORY_WARNING, errMsg + " has no 'field' property! It will be ignore...");
            return null;
        }
        String fieldName = (String) fieldNameObj;
        Optional<JCTree.JCVariableDecl> fieldParamOpt = params.stream()
                .filter(p -> fieldName.equals(p.name.toString())).findFirst();
        if (!fieldParamOpt.isPresent()) {
            messager.printMessage(MANDATORY_WARNING,
                    String.format(errMsg + " field '%s' is not presented in method parameters! it will be ignore...", fieldName));
            return null;
        }
        JCTree.JCVariableDecl fieldParam = fieldParamOpt.get();
        String fieldType = fieldParam.vartype.toString();
        String defaultValueKey = "default" + fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1);
        Object defaultValueObj = annotationValueMap.get(defaultValueKey);
        if (defaultValueObj == null) {
            messager.printMessage(MANDATORY_WARNING, String.format(
                    errMsg + " field '%s''s default value(property name=%s) doesn't set! It will be ignore...",
                    fieldName, defaultValueKey));
            return null;
        }

        return new Pair<>(fieldName, defaultValueObj);
    }

    private List<JCTree.JCVariableDecl> genNewParamList(List<JCTree.JCVariableDecl> originParams,
                                                        String fieldName, Set<String> methodSignSet, String errMsg) {
        List<JCTree.JCVariableDecl> newParamList = List.from(originParams.stream()
                .filter(variable -> !fieldName.equals(variable.name.toString()))
                .collect(Collectors.toList()));
        String newMethodSign = calMethodSign(newParamList);
        if (methodSignSet.contains(newMethodSign)) {
            messager.printMessage(ERROR, String.format(
                    errMsg + "method with same signature (%s) already existed! Can not continue!",
                    newParamList));
            return null;
        }
        methodSignSet.add(newMethodSign);
        return newParamList;
    }

    private List<JCTree.JCStatement> genBodyStats(List<JCTree.JCVariableDecl> originParams, Name methodName, Pair<String, Object> defaultFieldPair) {
        List<JCTree.JCExpression> argTypeList = List.from(originParams.stream().map(v -> v.vartype).collect(Collectors.toList()));
        List<JCTree.JCExpression> argList = List.nil();
        JCTree.JCExpression callOriginMethod = treeMaker.Apply(argTypeList, memberAccess(methodName.toString()), argList);
        return List.of(treeMaker.Return(callOriginMethod));
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }
}
