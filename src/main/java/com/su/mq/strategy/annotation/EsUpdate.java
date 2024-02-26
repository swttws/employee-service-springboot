package com.su.mq.strategy.annotation;

import java.lang.annotation.*;

/**
 * @author suweitao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsUpdate {

    public String value() default "";

}
