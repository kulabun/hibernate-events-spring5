package org.labun.hibernate.events;

/**
 * @author Konstantin Labun
 */

import java.util.List;
import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.VetoeingEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class HibernateEventListenersAutoConfiguration {

  @Bean
  public EventListenerRegistrar eventListenerRegistrar(
      @Autowired(required = false) List<OnEventListener> onEventListeners,
      @Autowired(required = false) List<VetoeingEventListener> vetoeingEventListeners
  ) {
    EventListenerRegistrar registrar = Services.find(EventListenerRegistrar.class);
    if (onEventListeners != null) {
      onEventListeners.forEach(it -> registrar.register(it));
    }
    if (vetoeingEventListeners != null) {
      vetoeingEventListeners.forEach(it -> registrar.register(it));
    }
    return registrar;
  }

}
