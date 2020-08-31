package io.github.leibnizhu.nusadua;

import io.github.leibnizhu.nusadua.annotation.MethodOverload;
import org.junit.Test;

//import javax.annotation.Generated;

import static junit.framework.Assert.assertEquals;

/**
 * @author Leibniz on 2020/6/24 9:42 AM
 */
public class SinglePrimitiveTypeTest {
    @Test
    public void singlePrimitiveTypesTest() {
        System.out.println("singlePrimitiveTypesTest start====");
        //omit string
        System.out.println("singlePrimitiveTypesTest String argument====");
        assertEquals("Fixed string=fixed, str=test", defaultString("fixed", "test"));
        assertEquals("Fixed string=fixed, str=hahaha", defaultString("fixed"));
        //omit boolean
        System.out.println("singlePrimitiveTypesTest boolean argument====");
        assertEquals("Fixed string=fixed, b=false", defaultBoolean("fixed", false));
        assertEquals("Fixed string=fixed, b=true", defaultBoolean("fixed"));
        //omit byte
        System.out.println("singlePrimitiveTypesTest byte argument====");
        assertEquals("Fixed string=fixed, b=-47", defaultByte("fixed", (byte)-47));
        assertEquals("Fixed string=fixed, b=-122", defaultByte("fixed"));
        //omit short
        System.out.println("singlePrimitiveTypesTest short argument====");
        assertEquals("Fixed string=fixed, s=9527", defaultShort("fixed", (short)9527));
        assertEquals("Fixed string=fixed, s=-1024", defaultShort("fixed"));
        //omit int
        System.out.println("singlePrimitiveTypesTest int argument====");
        assertEquals("Fixed string=fixed, i=9527", defaultInt("fixed", 9527));
        assertEquals("Fixed string=fixed, i=233", defaultInt("fixed"));
        //omit long
        System.out.println("singlePrimitiveTypesTest long argument====");
        assertEquals("Fixed string=fixed, l=9527", defaultLong("fixed", 9527L));
        assertEquals("Fixed string=fixed, l=1591200000000", defaultLong("fixed"));
        //omit float
        System.out.println("singlePrimitiveTypesTest float argument====");
        assertEquals("Fixed string=fixed, f=-1.23456", defaultFloat("fixed", -1.23456F));
        assertEquals("Fixed string=fixed, f=2.71828", defaultFloat("fixed"));
        //omit double
        System.out.println("singlePrimitiveTypesTest double argument====");
        assertEquals("Fixed string=fixed, d=-6.62607004", defaultDouble("fixed", -6.62607004));
        assertEquals("Fixed string=fixed, d=3.14159265358", defaultDouble("fixed"));
        //omit char
        System.out.println("singlePrimitiveTypesTest char argument====");
        assertEquals("Fixed string=fixed, c=@", defaultChar("fixed", '@'));
        assertEquals("Fixed string=fixed, c=X", defaultChar("fixed"));
    }

    @SuppressWarnings("Test unused annotation")
    @MethodOverload(field = "str", defaultString = "hahaha")
    private String defaultString(String fixed, String str) {
        String result = String.format("Fixed string=%s, str=%s", fixed, str);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "b", defaultBool = true)
    private String defaultBoolean(String fixed, boolean b) {
        String result = String.format("Fixed string=%s, b=%s", fixed, b);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "b", defaultByte = -122)
    private String defaultByte(String fixed, byte b) {
        String result = String.format("Fixed string=%s, b=%s", fixed, b);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "s", defaultShort = -1024)
    private String defaultShort(String fixed, short s) {
        String result = String.format("Fixed string=%s, s=%s", fixed, s);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "i", defaultInt = 233)
    private String defaultInt(String fixed, int i) {
        String result = String.format("Fixed string=%s, i=%s", fixed, i);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "l", defaultLong = 1591200000000L)
    private String defaultLong(String fixed, long l) {
        String result = String.format("Fixed string=%s, l=%s", fixed, l);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "f", defaultFloat = 2.71828F)
    private String defaultFloat(String fixed, float f) {
        String result = String.format("Fixed string=%s, f=%s", fixed, f);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "d", defaultDouble = 3.14159265358)
    private String defaultDouble(String fixed, double d) {
        String result = String.format("Fixed string=%s, d=%s", fixed, d);
        System.out.println(result);
        return result;
    }

    @MethodOverload(field = "c", defaultChar = 'X')
    private String defaultChar(String fixed, char c) {
        String result = String.format("Fixed string=%s, c=%s", fixed, c);
        System.out.println(result);
        return result;
    }
}
