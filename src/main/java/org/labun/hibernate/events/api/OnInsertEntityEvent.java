package org.labun.hibernate.events.api;

/**
 * @author Konstantin Labun
 */
public class OnInsertEntityEvent<T> {

  private final T entity;

  public OnInsertEntityEvent(T entity) {
    this.entity = entity;
  }

  public T entity() {
    return entity;
  }
}
