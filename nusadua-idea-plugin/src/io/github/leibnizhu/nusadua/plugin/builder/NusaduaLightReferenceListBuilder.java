package io.github.leibnizhu.nusadua.plugin.builder;

import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.impl.light.LightReferenceListBuilder;

/**
 * @author Leibniz on 2020/08/30 7:28 PM
 */
public class NusaduaLightReferenceListBuilder extends LightReferenceListBuilder implements SyntheticElement {

    public NusaduaLightReferenceListBuilder(PsiManager manager, Language language, Role role) {
        super(manager, language, role);
    }

    @Override
    public TextRange getTextRange() {
        TextRange r = super.getTextRange();
        return r == null ? TextRange.EMPTY_RANGE : r;
    }
}
