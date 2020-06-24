package io.github.leibnizhu.nusadua;

import io.github.leibnizhu.nusadua.annotation.MethodOverload;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Leibniz on 2020/6/24 1:11 PM
 */
public class MultiDefaultValueTest {
    @Test
    public void twoDefaultValueTest() {
        System.out.println("twoDefaultValueTest start====");
        //2 parameters
        assertEquals("str=aaa, i=123", twoDefaultValue("aaa", 123));
        //1 parameters
        assertEquals("str=aaa, i=9527", twoDefaultValue("aaa"));
        assertEquals("str=test, i=123", twoDefaultValue(123));
        //0 parameters
        assertEquals("str=test, i=9527", twoDefaultValue());
    }

    @MethodOverload(field = "i", defaultInt = 9527)
    @MethodOverload(field = "str", defaultString = "test")
    private String twoDefaultValue(String str, int i) {
        String result = String.format("str=%s, i=%s", str, i);
        System.out.println(result);
        return result;
    }

    @Test
    public void threeDefaultValueTest() {
        System.out.println("threeDefaultValueTest start====");
        //3 parameters
        assertEquals("str=aaa, i=123, d=-2.71828", threeDefaultValue("aaa", 123, -2.71828));
        //2 parameters
        assertEquals("str=test, i=123, d=-2.71828", threeDefaultValue(123, -2.71828));
        assertEquals("str=aaa, i=9527, d=-2.71828", threeDefaultValue("aaa", -2.71828));
        assertEquals("str=aaa, i=123, d=3.1415926", threeDefaultValue("aaa", 123));
        //1 parameters
        assertEquals("str=test, i=9527, d=-2.71828", threeDefaultValue(-2.71828));
        assertEquals("str=test, i=123, d=3.1415926", threeDefaultValue(123));
        assertEquals("str=aaa, i=9527, d=3.1415926", threeDefaultValue("aaa"));
        //0 parameters
        assertEquals("str=test, i=9527, d=3.1415926", threeDefaultValue());
    }

    @MethodOverload(field = "i", defaultInt = 9527)
    @MethodOverload(field = "str", defaultString = "test")
    @MethodOverload(field = "d", defaultDouble = 3.1415926)
    private String threeDefaultValue(String str, int i, double d) {
        String result = String.format("str=%s, i=%s, d=%s", str, i, d);
        System.out.println(result);
        return result;
    }

    @Test
    public void fourDefaultValueTest() {
        System.out.println("fourDefaultValueTest start====");
        //4 parameters
        assertEquals("str=aaa, i=123, d=-2.71828, c=@", fourDefaultValue("aaa", 123, -2.71828, '@'));
        //3 parameters
        assertEquals("str=test, i=123, d=-2.71828, c=@", fourDefaultValue(123, -2.71828, '@'));
        assertEquals("str=aaa, i=9527, d=-2.71828, c=@", fourDefaultValue("aaa", -2.71828, '@'));
        assertEquals("str=aaa, i=123, d=3.1415926, c=@", fourDefaultValue("aaa", 123, '@'));
        assertEquals("str=aaa, i=123, d=-2.71828, c=X", fourDefaultValue("aaa", 123, -2.71828));
        //2 parameters
        assertEquals("str=test, i=9527, d=-2.71828, c=@", fourDefaultValue(-2.71828, '@'));
        assertEquals("str=test, i=123, d=3.1415926, c=@", fourDefaultValue(123, '@'));
        assertEquals("str=test, i=123, d=-2.71828, c=X", fourDefaultValue(123, -2.71828));
        assertEquals("str=aaa, i=9527, d=3.1415926, c=@", fourDefaultValue("aaa", '@'));
        assertEquals("str=aaa, i=9527, d=-2.71828, c=X", fourDefaultValue("aaa", -2.71828));
        assertEquals("str=aaa, i=123, d=3.1415926, c=X", fourDefaultValue("aaa", 123));
        //1 parameters
        assertEquals("str=test, i=9527, d=3.1415926, c=@", fourDefaultValue('@'));
        assertEquals("str=test, i=9527, d=-2.71828, c=X", fourDefaultValue(-2.71828));
        assertEquals("str=test, i=123, d=3.1415926, c=X", fourDefaultValue(123));
        assertEquals("str=aaa, i=9527, d=3.1415926, c=X", fourDefaultValue("aaa"));
        //0 parameters
        assertEquals("str=test, i=9527, d=3.1415926, c=X", fourDefaultValue());
    }

    @MethodOverload(field = "c", defaultChar = 'X')
    @MethodOverload(field = "i", defaultInt = 9527)
    @MethodOverload(field = "str", defaultString = "test")
    @MethodOverload(field = "d", defaultDouble = 3.1415926)
    private String fourDefaultValue(String str, int i, double d, char c) {
        String result = String.format("str=%s, i=%s, d=%s, c=%s", str, i, d, c);
        System.out.println(result);
        return result;
    }
}
