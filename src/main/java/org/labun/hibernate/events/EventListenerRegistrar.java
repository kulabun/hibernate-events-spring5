package org.labun.hibernate.events;

import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.VetoeingEventListener;

/**
 * @author Konstantin Labun
 */
public interface EventListenerRegistrar {

  void register(OnEventListener eventListener);

  void register(VetoeingEventListener eventListener);
}
