package io.github.leibnizhu.nusadua.plugin.builder;

import com.intellij.lang.Language;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.impl.light.LightParameterListBuilder;

import java.util.Arrays;

/**
 * @author Leibniz on 2020/08/30 7:25 PM
 */
public class NusaduaLightParameterListBuilder extends LightParameterListBuilder implements SyntheticElement {

    public NusaduaLightParameterListBuilder(PsiManager manager, Language language) {
        super(manager, language);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NusaduaLightParameterListBuilder that = (NusaduaLightParameterListBuilder) o;

        if (getParametersCount() != that.getParametersCount()) {
            return false;
        }

        return Arrays.equals(getParameters(), that.getParameters());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getParameters());
    }
}
