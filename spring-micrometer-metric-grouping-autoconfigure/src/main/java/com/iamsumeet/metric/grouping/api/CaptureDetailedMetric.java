package com.iamsumeet.metric.grouping.api;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to control the metric detail level
 *
 * @author Sumeet Mondal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CaptureDetailedMetric {

    @AliasFor("value")
    String uriGroupName() default "";

    @AliasFor("uriGroupName")
    String value() default "";
}
