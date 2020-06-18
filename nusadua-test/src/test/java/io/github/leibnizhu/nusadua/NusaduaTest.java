package io.github.leibnizhu.nusadua;


/**
 * @author Leibniz on 2020/06/19 12:15 AM
 */
public class NusaduaTest {

    @MethodOverload(field = "srr", defaultString = "hahaha")
    private void print(String str, byte b, short s, int i, long l, float f, double d, boolean bool, char c) {
        System.out.println(String.format("String=%s, byte=%s, short=%s, int=%s, long=%s, float=%s, double=%s, boolean=%s, char=%s",
                str, b, s, i, l, f, d, bool, c));
    }

    public static void main(String[] args){
        NusaduaTest nusaduaTest = new NusaduaTest();
        nusaduaTest.print("a", (byte)0, (short)0, 0, 0, 0, 0, true, 'd');
    }

}
