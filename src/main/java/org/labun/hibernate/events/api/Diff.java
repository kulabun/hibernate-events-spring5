package org.labun.hibernate.events.api;

/**
 * @author Konstantin Labun
 */
public class Diff {

  private final String fieldName;
  private final Object oldValue;
  private final Object newValue;

  public Diff(String fieldName, Object oldValue, Object newValue) {
    this.fieldName = fieldName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public String fieldName() {
    return fieldName;
  }

  public Object oldValue() {
    return oldValue;
  }

  public Object newValue() {
    return newValue;
  }
}
