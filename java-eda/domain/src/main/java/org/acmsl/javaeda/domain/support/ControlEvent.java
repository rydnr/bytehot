package org.acmsl.javaeda.domain.support;

/**
  Represents events managed by the infrastructure layer.

  Class name: ControlEvent

  Responsibilities:
    - Represent events that need to be specifically managed by the infrastructure layer, usually rejected events that need to be reattempted.

  Collaborators:
    - org.acmsl.javaeda.application.ListenRegistry
*/
public interface ControlEvent {
}
