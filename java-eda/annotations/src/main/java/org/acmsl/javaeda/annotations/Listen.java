package org.acmsl.javaeda.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  A method-level annotation to mark methods listening to certain types of events.

  Class name: Listen

  Responsibilities:
    - Mark methods listening to specific events.

  Collaborators:
    - org.acmsl.javaeda.annotations.ListenProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listen {
}
