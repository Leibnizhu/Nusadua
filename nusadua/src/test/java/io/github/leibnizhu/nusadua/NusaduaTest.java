package io.github.leibnizhu.nusadua;


import io.github.leibnizhu.nusadua.annotation.MethodOverload;
import org.junit.Test;

import javax.annotation.Generated;
import java.util.Arrays;

/**
 * @author Leibniz on 2020/06/19 12:15 AM
 */
public class NusaduaTest {
    @Test
    public void primitiveTypesTest() {
        System.out.println("primitiveTypesTest start====");
        printPrimitiveTypes("a", (byte) 0, (short) 0, 0, 0L, 0, 0, true, 'd');
        //omit str
        printPrimitiveTypes((byte) 0, (short) 0, 0, 0, 0L, 0, true, 'd');
        //omit i
        printPrimitiveTypes("a", (byte) 0, (short) 0, 0, 0L, 0, true, 'd');
        //omit str and i
        printPrimitiveTypes((byte) 0, (short) 0, 0, 0, 0L, true, 'd');
    }

    @Generated("d")
    @MethodOverload(field = "i", defaultInt = -1)
    @MethodOverload(field = "str", defaultString = "hahaha")
    private void printPrimitiveTypes(String str, byte b, short s, int i, long l, float f, double d, boolean bool, char c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    @Test
    public void printBoxedTypesTest() {
        System.out.println("printBoxedTypesTest start====");
        printBoxedTypes("a", (byte) 0, (short) 0, 0, 0L, 0f, 0.0, true, 'd');
        //omit str
        printBoxedTypes((byte) 0, (short) 0, 0, 0L, 0f, 0.0, true, 'd');
        //omit i
        printBoxedTypes("a", (byte) 0, (short) 0, 0L, 0f, 0.0, true, 'd');
        //omit str and i
        printBoxedTypes((byte) 0, (short) 0, 0L, 0f, 0.0, true, 'd');
    }

    @MethodOverload(field = "i", defaultInt = -1)
    @MethodOverload(field = "bool", defaultBool = true)
    @MethodOverload(field = "str", defaultString = "hahaha")
    private void printBoxedTypes(String str, Byte b, Short s, Integer i, Long l, Float f, Double d, Boolean bool, Character c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    @Test
    public void printErrorAnnotationTest() {
        System.out.println("printErrorAnnotationTest start====");
        printErrorAnnotation(new Object(), 233);
        printErrorAnnotation(new Object());
        printErrorAnnotation(233);
        printErrorAnnotation();
    }

    @MethodOverload(field = "obj", defaultNull = true)
    @MethodOverload(field = "ddd", defaultNull = true)
    @MethodOverload(field = "i", defaultBool = true)
    @MethodOverload(field = "i", defaultInt = 9527)
    private void printErrorAnnotation(Object obj, int i) {
        System.out.println(String.format("Object=%s, int=%s", obj, i));
    }

    @MethodOverload(field = "str1", defaultString = "true")
//    @MethodOverload(field = "str2", defaultString = "true")
    private void print(String str1, String str2) {
        System.out.println(String.format("String1=%s, String2=%s", str1, str2));
    }

    @Test
    public void primitiveTypeArrsTest() {
        System.out.println("primitiveTypeArrsTest start====");
        printPrimitiveTypeArrs(new String[]{"a", "b", "c"}, new byte[]{0, 127}, new short[]{0, 1, 2}, new int[]{-1, 0, 1},
                new long[]{0L, -1L}, new float[]{1, 2}, new double[]{1, 3}, new boolean[]{true, false}, new char[]{'d', '?'});
        //omit str
        printPrimitiveTypeArrs(new byte[]{0, 127}, new short[]{0, 1, 2}, new int[]{-1, 0, 1},
                new long[]{0L, -1L}, new float[]{1, 2}, new double[]{1, 3}, new boolean[]{true, false}, new char[]{'d', '?'});
        //omit i
        printPrimitiveTypeArrs(new String[]{"a", "b", "c"}, new byte[]{0, 127}, new short[]{0, 1, 2},
                new long[]{0L, -1L}, new float[]{1, 2}, new double[]{1, 3}, new boolean[]{true, false}, new char[]{'d', '?'});
        //omit str and i
        printPrimitiveTypeArrs(new byte[]{0, 127}, new short[]{0, 1, 2}, new long[]{0L, -1L},
                new float[]{1, 2}, new double[]{1, 3}, new boolean[]{true, false}, new char[]{'d', '?'});
        //omit str and i and b
        printPrimitiveTypeArrs(new short[]{0, 1, 2}, new long[]{0L, -1L},
                new float[]{1, 2}, new double[]{1, 3}, new boolean[]{true, false}, new char[]{'d', '?'});
    }

    @MethodOverload(field = "b", defaultNull = true)
    @MethodOverload(field = "i", defaultIntArr = {1, 2, 3})
    @MethodOverload(field = "str", defaultStringArr = {"hahaha", "hehehe", "xixixi"})
    private void printPrimitiveTypeArrs(String[] str, byte[] b, short[] s, int[] i, long[] l, float[] f, double[] d, boolean[] bool, char[] c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                Arrays.toString(str), Arrays.toString(b), Arrays.toString(s), Arrays.toString(i),
                Arrays.toString(l), Arrays.toString(f), Arrays.toString(d), Arrays.toString(bool), Arrays.toString(c)));
    }

    @Test
    public void boxedTypeArrsTest() {
        System.out.println("boxedTypeArrsTest start====");
        printBoxedTypeArrs(new String[]{"a", "b", "c"}, new Byte[]{0, 127}, new Short[]{0, 1, 2}, new Integer[]{-1, 0, 1},
                new Long[]{0L, -1L}, new Float[]{1f, 2f}, new Double[]{1.0, 3.0}, new Boolean[]{true, false}, new Character[]{'d', '?'});
        //omit str
        printBoxedTypeArrs(new Byte[]{0, 127}, new Short[]{0, 1, 2}, new Integer[]{-1, 0, 1},
                new Long[]{0L, -1L}, new Float[]{1f, 2f}, new Double[]{1.0, 3.0}, new Boolean[]{true, false}, new Character[]{'d', '?'});
        //omit i
        printBoxedTypeArrs(new String[]{"a", "b", "c"}, new Byte[]{0, 127}, new Short[]{0, 1, 2},
                new Long[]{0L, -1L}, new Float[]{1f, 2f}, new Double[]{1.0, 3.0}, new Boolean[]{true, false}, new Character[]{'d', '?'});
//        //omit str and i
        printBoxedTypeArrs(new Byte[]{0, 127}, new Short[]{0, 1, 2}, new Long[]{0L, -1L},
                new Float[]{1f, 2f}, new Double[]{1.0, 3.0}, new Boolean[]{true, false}, new Character[]{'d', '?'});
//        //omit str and i and b
        printBoxedTypeArrs(new Short[]{0, 1, 2}, new Long[]{0L, -1L},
                new Float[]{1f, 2f}, new Double[]{1.0, 3.0}, new Boolean[]{true, false}, new Character[]{'d', '?'});
    }

    @MethodOverload(field = "b", defaultNull = true)
    @MethodOverload(field = "i", defaultIntArr = {3, 2, 1})
    @MethodOverload(field = "str", defaultStringArr = {"hahaha", "hehehe", "xixixi"})
    private void printBoxedTypeArrs(String[] str, Byte[] b, Short[] s, Integer[] i, Long[] l, Float[] f, Double[] d, Boolean[] bool, Character[] c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                Arrays.toString(str), Arrays.toString(b), Arrays.toString(s), Arrays.toString(i),
                Arrays.toString(l), Arrays.toString(f), Arrays.toString(d), Arrays.toString(bool), Arrays.toString(c)));
    }
}
