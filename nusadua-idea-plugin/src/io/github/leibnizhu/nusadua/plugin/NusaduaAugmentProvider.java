package io.github.leibnizhu.nusadua.plugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.augment.PsiAugmentProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Leibniz on 2020/7/1 5:16 PM
 */
public class NusaduaAugmentProvider extends PsiAugmentProvider {
    @NotNull
    @Override
    protected <Psi extends PsiElement> List<Psi> getAugments(@NotNull PsiElement element, @NotNull Class<Psi> type) {
        return super.getAugments(element, type);
    }
}
