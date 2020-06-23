package io.github.leibnizhu.nusadua;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author Leibniz on 2020/06/18 11:50 PM
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"io.github.leibnizhu.nusadua.annotation.MethodOverload",
        "io.github.leibnizhu.nusadua.annotation.MethodOverloads"})
public class MethodOverloadProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private TreeMaker treeMaker; //AST
    private Names names;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(this.processingEnv);
        Context context = ((JavacProcessingEnvironment) this.processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        messager = processingEnv.getMessager();
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        MethodOverloadTranslator visitor = new MethodOverloadTranslator(treeMaker, messager, names);
        roundEnv.getRootElements().forEach(element -> trees.getTree(element).accept(visitor));
        return true;
    }
}