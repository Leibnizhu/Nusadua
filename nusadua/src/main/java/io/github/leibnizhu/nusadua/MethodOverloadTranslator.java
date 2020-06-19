package io.github.leibnizhu.nusadua;

import com.google.gson.Gson;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leibniz on 2020/6/19 3:59 PM
 */
class MethodOverloadTranslator extends TreeTranslator {
    private final TreeMaker treeMaker;
    private final Messager messager;
    private final Names names;

    public MethodOverloadTranslator(TreeMaker treeMaker, Messager messager, Names names) {
        this.treeMaker = treeMaker;
        this.messager = messager;
        this.names = names;
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethod) {
        super.visitMethodDef(jcMethod);
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClass) {
        Queue<JCTree.JCMethodDecl> newMethods = new LinkedList<>();
        jcClass.defs.stream()
                .filter(k -> k.getKind().equals(Tree.Kind.METHOD))
                .map(tree -> (JCTree.JCMethodDecl) tree)
                .forEach(jcMethod -> {
                    JCTree.JCModifiers modifiers = jcMethod.getModifiers();
                    //打印所有方法
                    messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "current Method:" + jcMethod.name + "(" + jcMethod.params + ")");
                    for (JCTree.JCAnnotation annotation : modifiers.annotations) {
                        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "current Annotation:" + annotation.toString() + "," + annotation.getArguments());
                        if ("MethodOverload".equals(annotation.getAnnotationType().toString())) {
                            JCTree.JCModifiers flagsOnly = treeMaker.Modifiers(modifiers.flags);
                            //TODO 校验注解
                            Pair<String, Object> defaultFieldPair = validateAnnotation(jcMethod.params, annotation);
                            List<JCTree.JCVariableDecl> newParams = jcMethod.getParameters(); //TODO 剪掉当前参数后的参数列表
                            JCTree.JCBlock codeBlock = treeMaker.Block(jcMethod.body.flags, jcMethod.body.stats); //TODO 新建一句，调用原方法；返回空则return
                            newMethods.add(treeMaker.MethodDef(flagsOnly, getNameFromString(jcMethod.name + "_" + new Random().nextInt(100000)), jcMethod.restype,
                                    jcMethod.getTypeParameters(), newParams, jcMethod.getThrows(),
                                    codeBlock, jcMethod.defaultValue));
                        }
                    }
                });
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "new Methods:" + newMethods);
        for (JCTree.JCMethodDecl method : newMethods) {
            jcClass.defs = jcClass.defs.append(method);
        }
        super.visitClassDef(jcClass);
    }

    /**
     * Validate the annotation,find field to overload, and its default value
     *
     * @param params     parameters of method
     * @param annotation annotation
     * @return Pair<fieldName, defaultValue>
     */
    private Pair<String, Object> validateAnnotation(List<JCTree.JCVariableDecl> params, JCTree.JCAnnotation annotation) {
        Map<String, Object> annotationValueMap = annotation.args.stream()
                .filter(a -> a instanceof JCTree.JCAssign)
                .map(a -> (JCTree.JCAssign) a)
                .filter(a -> a.lhs instanceof JCTree.JCIdent && a.rhs instanceof JCTree.JCLiteral)
                .collect(Collectors.toMap(a -> ((JCTree.JCIdent) a.lhs).getName().toString(), a -> ((JCTree.JCLiteral) a.rhs).getValue()));
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "parsed Annotation:" + annotationValueMap);
        return null;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }
}
