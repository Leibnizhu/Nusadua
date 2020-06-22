package io.github.leibnizhu.nusadua;


import io.github.leibnizhu.nusadua.annotation.MethodOverload;
import org.junit.Test;

import javax.annotation.Generated;

/**
 * @author Leibniz on 2020/06/19 12:15 AM
 */
public class NusaduaTest {

    @Test
    public void primitiveTypesTest() {
        printPrimitiveTypes("a", (byte) 0, (short) 0, 0, 0, 0, 0, true, 'd');
        //omit str
        printPrimitiveTypes((byte) 0, (short) 0, 0, 0, 0, 0, true, 'd');
        //omit i
        printPrimitiveTypes("a", (byte) 0, (short) 0, 0, 0, 0, true, 'd');
        //omit str and i
//        nusaduaTest.printPrimitiveTypes((byte) 0, (short) 0, 0, 0, 0, true, 'd');
    }

    @Generated("d")
    @MethodOverload(field = "i", defaultInt = -1)
    @MethodOverload(field = "str", defaultString = "hahaha")
    private void printPrimitiveTypes(String str, byte b, short s, int i, long l, float f, double d, boolean bool, char c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    @Generated("d")
    @MethodOverload(field = "i", defaultInt = -1)
    @MethodOverload(field = "str", defaultString = "hahaha")
    private void printBoxedTypes(String str, Byte b, Short s, Integer i, Long l, Float f, Double d, Boolean bool, Character c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    @Test
    public void printErrorAnnotationTest(){
        printErrorAnnotation(new Object(), 233);
        printErrorAnnotation(233);
//        printErrorAnnotation(new Object());
    }

    @Generated("d")
    @MethodOverload(field = "obj", defaultNull = true)
    @MethodOverload(field = "ddd", defaultNull = true)
    @MethodOverload(field = "i", defaultBool = true)
    private void printErrorAnnotation(Object obj, int i) {
        System.out.println(String.format("Object=%s, int=%s", obj, i));
    }

    @MethodOverload(field = "str1", defaultString = "true")
//    @MethodOverload(field = "str2", defaultString = "true")
    private void print(String str1, String str2) {
        System.out.println(String.format("String1=%s, String2=%s", str1, str2));
    }
}
