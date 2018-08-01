package org.labun.hibernate.events.api;

/**
 * @author Konstantin Labun
 */
public interface OnEventListener<T> {

  default void onDelete(OnDeleteEntityEvent<T> event) {
  }

  default void onInsert(OnInsertEntityEvent<T> event) {
  }

  default void onUpdate(OnUpdateEntityEvent<T> event) {
  }

  default boolean support(Object obj) {
    return obj != null && getTargetType().isAssignableFrom(obj.getClass());
  }

  default Class<?> getTargetType() {
    return Object.class;
  }
}
