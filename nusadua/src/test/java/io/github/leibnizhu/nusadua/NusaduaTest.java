package io.github.leibnizhu.nusadua;


import io.github.leibnizhu.nusadua.annotation.MethodOverload;

import javax.annotation.Generated;

/**
 * @author Leibniz on 2020/06/19 12:15 AM
 */
public class NusaduaTest {

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
    private void printBoxedTypes(String str, byte b, short s, int i, long l, float f, double d, boolean bool, char c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    @Generated("d")
    @MethodOverload(field = "obj", defaultNull = true)
    @MethodOverload(field = "ddd", defaultNull = true)
    @MethodOverload(field = "i", defaultBool = true)
    private void print(Object obj, int i) {
        System.out.println(String.format("Object=%s, int=%s", obj, i));
    }

    @MethodOverload(field = "str1", defaultString = "true")
//    @MethodOverload(field = "str2", defaultString = "true")
    private void print(String str1, String str2) {
        System.out.println(String.format("String1=%s, String2=%s", str1, str2));
    }

    public static void main(String[] args) {
        NusaduaTest nusaduaTest = new NusaduaTest();
        nusaduaTest.printPrimitiveTypes("a", (byte) 0, (short) 0, 0, 0, 0, 0, true, 'd');
    }

}
