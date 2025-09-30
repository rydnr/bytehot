package org.acmsl.javaeda.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  Annotates methods used to apply events.

  Class name: Apply

  Responsibilities:
    - Label methods used to apply events.

  Collaborators:
    - org.acmsl.javaeda.domain.ApplicableEvent
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Apply {
}
