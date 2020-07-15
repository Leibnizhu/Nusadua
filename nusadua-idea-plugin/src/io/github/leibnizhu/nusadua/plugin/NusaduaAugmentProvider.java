package io.github.leibnizhu.nusadua.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author Leibniz on 2020/7/1 5:16 PM
 */
public class NusaduaAugmentProvider extends PsiAugmentProvider {
    private static final Logger log = Logger.getInstance(NusaduaAugmentProvider.class);

    public NusaduaAugmentProvider() {
        log.info("===========NusaduaAugmentProvider");
    }

    @NotNull
    @Override
    protected <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
        MethodOverloadHandler methodOverloadHandler = MethodOverloadHandler.getInstance();
        if (type == PsiClass.class && element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            boolean hasMethodOverload = Arrays.stream(psiClass.getMethods()).anyMatch(methodOverloadHandler::acceptable);
            if (hasMethodOverload) {
                methodOverloadHandler.handle(psiClass);
            }
        }
        return super.getAugments(element, type);
    }
}
