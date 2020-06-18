package io.github.leibnizhu.nusadua;

import java.lang.annotation.*;

/**
 * @author Leibniz on 2020/06/18 11:40 PM
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MethodOverload {
    String field();

    String defaultString() default "";

    byte defaultByte() default 0;

    short defaultShot() default 0;

    int defaultInt() default 0;

    long defaultLong() default 0;

    float defaultFloat() default 0;

    double defaultDouble() default 0;

    char defaultChar() default 0;

    boolean defaultBool() default false;

    String[] defaultStringArr() default {};

    byte[] defaultByteArr() default {};

    short[] defaultShotArr() default {};

    int[] defaultIntArr() default {};

    long[] defaultLongArr() default {};

    float[] defaultFloatArr() default {};

    double[] defaultDoubleArr() default {};

    char[] defaultCharArr() default {};

    boolean[] defaultBoolArr() default {};
}
