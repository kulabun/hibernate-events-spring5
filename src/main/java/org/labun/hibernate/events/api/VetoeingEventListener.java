package org.labun.hibernate.events.api;

/**
 * @author Konstantin Labun
 */
public interface VetoeingEventListener<T> {
  
  default boolean vetoeForDelete(OnDeleteEntityEvent<T> event) {
    return false;
  }

  default boolean vetoeForInsert(OnInsertEntityEvent<T> event) {
    return false;
  }

  default boolean vetoeForUpdate(OnUpdateEntityEvent<T> event) {
    return false;
  }

  default boolean support(Object obj) {
    return obj != null && getTargetType().isAssignableFrom(obj.getClass());
  }

  default Class<?> getTargetType() {
    return Object.class;
  }
}
