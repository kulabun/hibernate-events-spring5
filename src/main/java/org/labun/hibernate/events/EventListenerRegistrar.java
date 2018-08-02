package org.labun.hibernate.events;

import org.labun.hibernate.events.api.OnEventListener;

/**
 * @author Konstantin Labun
 */
public interface EventListenerRegistrar {

  void register(OnEventListener eventListener);
}
