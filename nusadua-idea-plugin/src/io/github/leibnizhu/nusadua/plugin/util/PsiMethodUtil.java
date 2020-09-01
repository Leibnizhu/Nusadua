package io.github.leibnizhu.nusadua.plugin.util;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Leibniz on 2020/09/1 1:24 PM
 */
public class PsiMethodUtil {
    @NotNull
    public static PsiCodeBlock createCodeBlockFromText(@NotNull String blockText, @NotNull PsiElement psiElement) {
        final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiElement.getProject());
        return elementFactory.createCodeBlockFromText("{" + blockText + "}", psiElement);
    }

    public static boolean hasMethodByName(@NotNull Collection<PsiMethod> classMethods, @NotNull String methodName) {
        return classMethods.stream().map(PsiMethod::getName).anyMatch(methodName::equals);
    }

    public static boolean hasMethodByName(@NotNull Collection<PsiMethod> classMethods, String... methodNames) {
        final List<String> searchedMethodNames = Arrays.asList(methodNames);
        return classMethods.stream().map(PsiMethod::getName).anyMatch(searchedMethodNames::contains);
    }

    public static boolean hasSimilarMethod(@NotNull Collection<PsiMethod> classMethods, @NotNull String methodName, int methodArgCount) {
        for (PsiMethod classMethod : classMethods) {
            if (isSimilarMethod(classMethod, methodName, methodArgCount)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSimilarMethod(@NotNull PsiMethod classMethod, @NotNull String methodName, int methodArgCount) {
        boolean equalNames = methodName.equalsIgnoreCase(classMethod.getName());
        if (equalNames) {
            int minArgs = classMethod.getParameterList().getParametersCount();
            int maxArgs = minArgs;
            if (classMethod.isVarArgs()) {
                minArgs--;
                maxArgs = Integer.MAX_VALUE;
            }
            return !(methodArgCount < minArgs || methodArgCount > maxArgs);
        }
        return false;
    }
}
