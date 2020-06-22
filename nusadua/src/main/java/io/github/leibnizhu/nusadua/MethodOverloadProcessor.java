package io.github.leibnizhu.nusadua;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import io.github.leibnizhu.nusadua.annotation.MethodOverload;
import io.github.leibnizhu.nusadua.annotation.MethodOverloads;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
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
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(this.processingEnv);
        Context context = ((JavacProcessingEnvironment) this.processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        messager = processingEnv.getMessager();
        this.names = Names.instance(context);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        MethodOverloadTranslator visitor = new MethodOverloadTranslator(treeMaker, messager, names);
        roundEnv.getRootElements().forEach(element -> trees.getTree(element).accept(visitor));


//        Set<? extends Element> multiAnnotation = roundEnv.getElementsAnnotatedWith(MethodOverloads.class);
//        messager.printMessage(Diagnostic.Kind.NOTE, "Has MethodOverloads annotation:" + multiAnnotation.toString()+","+multiAnnotation.getClass());
//        multiAnnotation.stream()
//                .map(element -> trees.getTree(element))
//                .forEach(tree -> tree.accept(visitor));

//        Set<? extends Element> annotation = roundEnv.getElementsAnnotatedWith(MethodOverload.class);
//        messager.printMessage(Diagnostic.Kind.NOTE, "Has MethodOverload annotation:" + annotation.toString()+","+annotation.getClass());
//        annotation.stream()
//                .map(element -> trees.getTree(element))
//                .forEach(tree -> tree.accept(visitor));
        return true;
    }
}