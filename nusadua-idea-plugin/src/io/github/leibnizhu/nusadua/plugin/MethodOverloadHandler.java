package io.github.leibnizhu.nusadua.plugin;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leibniz on 2020/06/25 12:20 AM
 */
public class MethodOverloadHandler {
    private static final String METHOD_OVERLOAD_ANNOTATION_NAME = "MethodOverload";

    public static MethodOverloadHandler getInstance() {
        return ServiceManager.getService(MethodOverloadHandler.class);
    }

    public void handle(PsiClass psiClass) {
        for (PsiMethod method : psiClass.getMethods()) {
            List<PsiAnnotation> annotationList = Arrays.stream(method.getAnnotations())
                    .filter(annotation -> METHOD_OVERLOAD_ANNOTATION_NAME.equals(annotation.getQualifiedName()))
                    .collect(Collectors.toList());
            System.out.println(method.getParameterList());
        }
    }

    public boolean acceptable(PsiMethod psiMethod) {
        return psiMethod.getAnnotation(METHOD_OVERLOAD_ANNOTATION_NAME)!= null;
    }
}
