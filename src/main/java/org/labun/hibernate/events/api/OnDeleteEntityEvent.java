package org.labun.hibernate.events.api;

/**
 * @author Konstantin Labun
 */
public class OnDeleteEntityEvent<T> {

  private final T entity;

  public OnDeleteEntityEvent(T entity) {
    this.entity = entity;
  }

  public T entity() {
    return entity;
  }
}
