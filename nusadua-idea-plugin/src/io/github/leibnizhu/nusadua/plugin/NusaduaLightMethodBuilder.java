package io.github.leibnizhu.nusadua.plugin;

import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.light.LightMethodBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author Leibniz on 2020/7/1 5:55 PM
 */
public class NusaduaLightMethodBuilder extends LightMethodBuilder {
    public NusaduaLightMethodBuilder(@NotNull PsiManager manager, @NotNull String name) {
        super(manager, name);
    }
}
