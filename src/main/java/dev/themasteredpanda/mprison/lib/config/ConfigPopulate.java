package dev.themasteredpanda.mprison.lib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used over fields to populate the field with a value from a configuration file.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPopulate
{
    /**
     * The path within the config that the field annotated by this annotation
     * should use to populate the value retrieved from the file into the field.
     * @return
     */
    String value();

    /**
     * Whether or not to format the value. This only works in strings both plural and singular.
     * @return true for formatting otherwise false.
     */
    boolean format() default false;

    /**
     * Whether or not to serialize the colour values of the string, both plural and singular.
     * @return
     */
    boolean colour() default false;
}
