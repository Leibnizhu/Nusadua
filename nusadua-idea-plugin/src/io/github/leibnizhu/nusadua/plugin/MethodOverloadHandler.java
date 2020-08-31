package io.github.leibnizhu.nusadua.plugin;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import io.github.leibnizhu.nusadua.plugin.psi.NusaduaLightMethodBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.sun.tools.javac.util.List.nil;

/**
 * @author Leibniz on 2020/06/25 12:20 AM
 */
public class MethodOverloadHandler {
    private static final Logger log = Logger.getInstance(MethodOverloadHandler.class);
    private static final String METHOD_OVERLOAD_ANNOTATION_NAME = "MethodOverload";
    private static final String METHOD_OVERLOAD_ANNOTATION_FULL_CLASS_NAME = "io.github.leibnizhu.nusadua.annotation.MethodOverload";

    public static MethodOverloadHandler getInstance() {
        return ServiceManager.getService(MethodOverloadHandler.class);
    }

    public void handle(PsiClass psiClass) {
        Queue<PsiMethod> newMethods = new LinkedList<>(); //all new methods
        Map<String, Set<String>> methodSignMap = new HashMap<>(); //Map<MethodName, Set<ParameterTypeList>>
        for (PsiMethod method : psiClass.getMethods()) {
            String methodName = method.getName();
            Set<String> curMethodSignSet = methodSignMap.computeIfAbsent(methodName, name -> new HashSet<>());
            String originSign = calMethodSign(method.getParameterList().getParameters());
            curMethodSignSet.add(originSign);
            List<PsiAnnotation> methodOverloadAnnotationList = Arrays.stream(method.getAnnotations())
                    .filter(annotation -> {
                        String annotationName = annotation.getQualifiedName();
                        return METHOD_OVERLOAD_ANNOTATION_NAME.equals(annotationName) || METHOD_OVERLOAD_ANNOTATION_FULL_CLASS_NAME.equals(annotationName);
                    })
                    .collect(Collectors.toList());
            if (!methodOverloadAnnotationList.isEmpty()) {
                Map<String, String> methodParamTypeMap = parseParamTypeMap(method);
                Map<String, Object> defaultValueMap = validateAndParseAnnotation(method, methodOverloadAnnotationList, methodParamTypeMap);
                System.out.println(methodName + ":" + defaultValueMap.toString());
                genNewMethods(defaultValueMap, psiClass, method, newMethods, curMethodSignSet);
            }
        }
    }

    private Map<String, String> parseParamTypeMap(PsiMethod method) {
        Map<String, String> paramTypeMap = new HashMap<>();
        PsiParameterList parameterList = method.getParameterList();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter psiParameter = parameterList.getParameter(i);
            String paramName = psiParameter.getName();
            String paramType = psiParameter.getType().getCanonicalText();
            paramTypeMap.put(paramName, paramType.substring(paramType.lastIndexOf(".") + 1));
        }
        return paramTypeMap;
    }

    private void genNewMethods(Map<String, Object> defaultValueMap, PsiClass psiClass, PsiMethod method, Queue<PsiMethod> newMethods, Set<String> curMethodSignSet) {
        //TODO
        //All parameters to calculate
        List<String> allElement = new ArrayList<>(defaultValueMap.keySet());
        Map<Integer, List<List<String>>> allCombinationMap = allElement.isEmpty() ? new HashMap<>() :
                calCombinations(0, nil(), new LinkedList<>(), allElement) //calculate Cartesian Product
                        .stream()
                        .filter(combination -> !combination.isEmpty()) //drop empty default value method(equals to original method)
                        .collect(Collectors.toMap(List::size, combination -> {
                            List<List<String>> newSize = new LinkedList<>();
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
        Map<String, PsiType> fieldTypeMap = Arrays.stream(method.getParameterList().getParameters())
                .collect(Collectors.toMap(PsiParameter::getName, PsiParameter::getType));
        for (Integer size : sizeList) {
            boolean errorWhenMethodSignConflict = size == 1;
            for (List<String> fields : allCombinationMap.get(size)) {
                //TODO use NusaduaLightMethodBuilder, to:
                // generate new parameter list √
                // generate method access flag √
                // generate code block
                // generate new method, and add to newMethods √
                NusaduaLightMethodBuilder methodBuilder = new NusaduaLightMethodBuilder(method.getManager(), method.getName())
                        .withMethodReturnType(method.getReturnType())
                        .withContainingClass(psiClass)
                        .withModifiers(method.getModifierList())
                        .withNavigationElement(method);
                for (String field : fields) {
                    methodBuilder.withParameter(field, fieldTypeMap.get(field));
                }

                newMethods.add(methodBuilder);
            }
        }
    }


    /**
     * calculate all combinations of MethodOverload fields
     *
     * @param n          current process level
     * @param curElements     current fields
     * @param curResult  current set of combinations
     * @param allElement all MethodOverload fields
     * @return List<List < fieldName>>
     * @author Leibniz
     */
    private List<List<String>> calCombinations(int n, com.sun.tools.javac.util.List<String> curElements, List<List<String>> curResult, List<String> allElement) {
        if (allElement.size() == n) {
            curResult.add(curElements);
        } else {
            //
            List<List<String>> tmpRes = calCombinations(n + 1, curElements.append(allElement.get(n)), curResult, allElement); //当前数字选择加入子集
            calCombinations(n + 1, curElements, tmpRes, allElement); //当前数字选择不加入子集
        }
        return curResult;
    }

    private Map<String, Object> validateAndParseAnnotation(PsiMethod method, List<PsiAnnotation> methodOverloadAnnotationList, Map<String, String> methodParamTypeMap) {
        Map<String, Object> defaultValueMap = new HashMap<>();
        for (PsiAnnotation psiAnnotation : methodOverloadAnnotationList) {
            validateAnnotation(psiAnnotation, defaultValueMap, methodParamTypeMap);
        }
        return defaultValueMap;
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

    private void validateAnnotation(PsiAnnotation annotation, Map<String, Object> defaultValueMap, Map<String, String> methodParamTypeMap) {
        JvmAnnotationAttribute fieldAttr = annotation.findAttribute("field");
        if (fieldAttr == null) {
            log.warn(annotation + "has no 'field' property! It will be ignore...");
            return;
        }
        String fieldName = (String) PsiAnnotationUtil.getAnnotationValue(fieldAttr.getAttributeValue());
        String paramType = methodParamTypeMap.get(fieldName);
        if (paramType == null) {
            log.warn("field '" + fieldName + "' is not presented in method parameters! it will be ignore...");
            return;
        }
        String defaultValueAnnoAttrName = TYPE_TO_ANNOTATION_PROPERTY.getOrDefault(paramType.toLowerCase(),
                "default" + paramType.substring(0, 1).toUpperCase() + paramType.substring(1).replace("[]", "Arr"));
        JvmAnnotationAttribute defaultValueAttr = annotation.findAttribute(defaultValueAnnoAttrName);
        if (defaultValueAttr == null) {
            JvmAnnotationAttribute defaultNullAttr = annotation.findAttribute("defaultNull");
            if (defaultNullAttr != null) {
                Object defaultNullValue = PsiAnnotationUtil.getAnnotationValue(defaultNullAttr.getAttributeValue());
                if (defaultNullValue instanceof Boolean) {
                    defaultValueMap.put(fieldName, null);
                }
            }
            log.warn("field '" + fieldName + "''s default value(property name=" + defaultValueAnnoAttrName + ") doesn't set! It will be ignore...");
        } else {
            Object defaultValue = PsiAnnotationUtil.getAnnotationValue(defaultValueAttr.getAttributeValue());
            defaultValueMap.put(fieldName, defaultValue);
        }
    }

    public boolean acceptable(PsiMethod psiMethod) {
        return psiMethod.getAnnotation(METHOD_OVERLOAD_ANNOTATION_NAME) != null;
    }

    /**
     * calculate method signature by method parameters, join by '_'
     *
     * @param parameterArr method parameter Array
     * @return method signature
     * @author Leibniz
     */
    private String calMethodSign(PsiParameter[] parameterArr) {
        StringJoiner sj = new StringJoiner("_");
        for (PsiParameter psiParameter : parameterArr) {
            sj.add(psiParameter.getType().getCanonicalText());
        }
        return sj.toString();
    }
}
