package org.labun.hibernate.events.api;

import java.util.List;

/**
 * @author Konstantin Labun
 */
public class OnUpdateEntityEvent<T> {

  private final T entity;
  private final List<Diff> diffs;

  public OnUpdateEntityEvent(T entity, List<Diff> diffs) {
    this.entity = entity;
    this.diffs = diffs;
  }

  public T entity() {
    return entity;
  }

  public List<Diff> diffs() {
    return diffs;
  }
}
