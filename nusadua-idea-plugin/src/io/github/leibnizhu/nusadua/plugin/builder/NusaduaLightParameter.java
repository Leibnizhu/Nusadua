package io.github.leibnizhu.nusadua.plugin.builder;

import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightModifierList;
import com.intellij.psi.impl.light.LightParameter;
import com.intellij.psi.impl.light.LightVariableBuilder;
import io.github.leibnizhu.nusadua.plugin.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * @author Leibniz on 2020/08/30 7:29 PM
 */
public class NusaduaLightParameter extends LightParameter implements SyntheticElement {
    private String myName;
    private final NusaduaLightIdentifier myNameIdentifier;

    public NusaduaLightParameter(@NotNull String name, @NotNull PsiType type, PsiElement declarationScope, Language language) {
        super(name, type, declarationScope, language);
        myName = name;
        PsiManager manager = declarationScope.getManager();
        myNameIdentifier = new NusaduaLightIdentifier(manager, name);
        ReflectionUtil.setFinalFieldPerReflection(LightVariableBuilder.class, this, LightModifierList.class,
                new NusaduaLightModifierList(manager, language, Collections.emptySet()));
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public PsiElement setName(@NotNull String name) {
        myName = name;
        myNameIdentifier.setText(name);
        return this;
    }

    @Override
    public PsiIdentifier getNameIdentifier() {
        return myNameIdentifier;
    }

    @Override
    public TextRange getTextRange() {
        TextRange r = super.getTextRange();
        return r == null ? TextRange.EMPTY_RANGE : r;
    }

    public NusaduaLightParameter setModifiers(String... modifiers) {
        NusaduaLightModifierList modifierList = new NusaduaLightModifierList(getManager(), getLanguage(), Collections.emptySet(), modifiers);
        ReflectionUtil.setFinalFieldPerReflection(LightVariableBuilder.class, this, LightModifierList.class, modifierList);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NusaduaLightParameter that = (NusaduaLightParameter) o;

        final PsiType thisType = getType();
        final PsiType thatType = that.getType();
        if (thisType.isValid() != thatType.isValid()) {
            return false;
        }

        return thisType.getCanonicalText().equals(thatType.getCanonicalText());
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }
}
