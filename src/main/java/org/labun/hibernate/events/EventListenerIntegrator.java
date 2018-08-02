package org.labun.hibernate.events;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @author Konstantin Labun
 */
public class EventListenerIntegrator implements Integrator {

  @Override
  public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
      SessionFactoryServiceRegistry serviceRegistry) {
    EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);
    EventListenerService eventListenerService = getEventListenerService();
    registry.appendListeners(EventType.POST_INSERT, eventListenerService);
    registry.appendListeners(EventType.POST_UPDATE, eventListenerService);
    registry.appendListeners(EventType.POST_DELETE, eventListenerService);
  }

  private EventListenerService getEventListenerService() {
    return (EventListenerService) Services.find(EventListenerRegistrar.class);
  }

  @Override
  public void disintegrate(SessionFactoryImplementor sessionFactory,
      SessionFactoryServiceRegistry serviceRegistry) {
    // nothing to do
  }
}
