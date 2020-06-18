package io.github.leibnizhu.nusadua;

import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leibniz on 2020/06/18 11:50 PM
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"io.github.leibnizhu.nusadua.MethodOverload"})
public class MethodOverloadProcessor extends AbstractProcessor {
    private JavacTrees trees; //抽象语法树
    private TreeMaker treeMaker; //AST
    private Names names; //标识符
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
        Set<? extends Element> annotation = roundEnv.getElementsAnnotatedWith(MethodOverload.class);
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "hahah===="+annotation.toString());
        annotation.stream()
                .map(element -> trees.getTree(element))
                .forEach(tree -> tree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClass) {
                        //过滤属性
                        Map<Name, JCTree.JCVariableDecl> treeMap =
                                jcClass.defs.stream().filter(k -> k.getKind().equals(Tree.Kind.VARIABLE))
                                        .map(tree -> (JCTree.JCVariableDecl) tree)
                                        .collect(Collectors.toMap(JCTree.JCVariableDecl::getName, Function.identity()));
                        //处理变量
                        treeMap.forEach((k, jcVariable) -> {
                            messager.printMessage(Diagnostic.Kind.NOTE, String.format("fields:%s", k));
                            try {
                                //增加get方法
                                jcClass.defs = jcClass.defs.prepend(null);
                                //增加set方法
                                jcClass.defs = jcClass.defs.prepend(null);
                            } catch (Exception e) {
                                messager.printMessage(Diagnostic.Kind.ERROR, Throwables.getStackTraceAsString(e));
                            }
                        });
                        //增加toString方法
                        jcClass.defs = jcClass.defs.prepend(null);
                        super.visitClassDef(jcClass);
                    }

                    @Override
                    public void visitMethodDef(JCTree.JCMethodDecl jcMethod) {
                        //打印所有方法
                        messager.printMessage(Diagnostic.Kind.NOTE, jcMethod.toString());
                        //修改方法
                        if ("getTest".equals(jcMethod.getName().toString())) {
                            result = treeMaker
                                    .MethodDef(jcMethod.getModifiers(), getNameFromString("testMethod"), jcMethod.restype,
                                            jcMethod.getTypeParameters(), jcMethod.getParameters(), jcMethod.getThrows(),
                                            jcMethod.getBody(), jcMethod.defaultValue);
                        }
                        super.visitMethodDef(jcMethod);
                    }
                }));
        return true;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }
}