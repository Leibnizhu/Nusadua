package io.github.leibnizhu.nusadua.annotation;

import java.lang.annotation.*;

/**
 * @author Leibniz on 2020/06/18 11:40 PM
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MethodOverloads {
    MethodOverload[] value();
}