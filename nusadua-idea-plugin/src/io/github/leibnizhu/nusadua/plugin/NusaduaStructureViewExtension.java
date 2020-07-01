package io.github.leibnizhu.nusadua.plugin;

import com.intellij.ide.structureView.StructureViewExtension;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author Leibniz on 2020/7/1 5:15 PM
 */
public class NusaduaStructureViewExtension implements StructureViewExtension {
        @Override
        public Class<? extends PsiElement> getType() {
            return PsiClass.class;
        }

        @Override
        public StructureViewTreeElement[] getChildren(PsiElement parent) {
            final PsiClass parentClass = (PsiClass) parent;

//            final Stream<PsiFieldTreeElement> lombokFields = Arrays.stream(parentClass.getFields())
//                    .filter(LombokLightFieldBuilder.class::isInstance)
//                    .map(psiField -> new PsiFieldTreeElement(psiField, false));
//
//            final Stream<PsiMethodTreeElement> lombokMethods = Arrays.stream(parentClass.getMethods())
//                    .filter(LombokLightMethodBuilder.class::isInstance)
//                    .map(psiMethod -> new PsiMethodTreeElement(psiMethod, false));
//
//            final Stream<JavaClassTreeElement> lombokInnerClasses = Arrays.stream(parentClass.getInnerClasses())
//                    .filter(LombokLightClassBuilder.class::isInstance)
//                    .map(psiClass -> new JavaClassTreeElement(psiClass, false));
//
//            return Stream.concat(Stream.concat(lombokFields, lombokMethods), lombokInnerClasses)
//                    .toArray(StructureViewTreeElement[]::new);
            return null;
        }

        @Nullable
        @Override
        public Object getCurrentEditorElement(Editor editor, PsiElement parent) {
            return null;
        }
    }
