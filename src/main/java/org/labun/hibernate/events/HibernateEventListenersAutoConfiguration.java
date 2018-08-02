package org.labun.hibernate.events;

/**
 * @author Konstantin Labun
 */

import java.util.List;
import javax.persistence.EntityManager;
import org.labun.hibernate.events.api.OnEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateEventListenersAutoConfiguration {

  @Bean
  public EventListenerRegistrar eventListenerRegistrar(
      @Autowired(required = false) List<OnEventListener> onEventListeners
  ) {
    EventListenerRegistrar registrar = Services.find(EventListenerRegistrar.class);
    if (onEventListeners != null) {
      onEventListeners.forEach(it -> registrar.register(it));
    }
    return registrar;
  }

  @Autowired
  private void initSessionManager(EntityManager em) {
    SessionManager.init(em);
  }

}
