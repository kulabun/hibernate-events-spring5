package org.labun.hibernate.events;

import static javax.imageio.spi.ServiceRegistry.lookupProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Konstantin Labun
 */
class Services {

  private static Map<Class, Object> cache = new HashMap<>();

  private Services() {
  }

  public static <T> T find(Class<T> clazz) {
    return (T) cache.computeIfAbsent(clazz, c -> doFind(c));
  }

  private static <T> T doFind(Class<T> clazz) {
    List<T> list = getItems(lookupProviders(clazz));
    if (list.size() == 0) {
      throw new IllegalStateException("No " + clazz.getSimpleName() + " implementations found!");
    }
    if (list.size() > 1) {
      throw new IllegalStateException(
          "Multiple " + clazz.getSimpleName() + " implementations found!");
    }
    return list.get(0);
  }

  public static <T> ArrayList<T> getItems(Iterator<T> iterator) {
    ArrayList<T> list = new ArrayList<>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }
}
