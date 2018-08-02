package org.labun.hibernate.events;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.labun.hibernate.events.api.Diff;
import org.labun.hibernate.events.api.OnDeleteEntityEvent;
import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.OnInsertEntityEvent;
import org.labun.hibernate.events.api.OnUpdateEntityEvent;

/**
 * @author Konstantin Labun
 */
public class EventListenerService implements
    PostInsertEventListener,
    PostUpdateEventListener,
    PostDeleteEventListener,
    EventListenerRegistrar {

  private List<OnEventListener> onEventListeners = new ArrayList<>();

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    execute(event.getSession(), () -> {
      OnDeleteEntityEvent e = new OnDeleteEntityEvent<>(event.getEntity());
      onEventListeners
          .stream()
          .filter(it -> it.support(event.getEntity()))
          .forEach(it -> it.onDelete(e));
    });
  }

  @Override
  public void onPostInsert(PostInsertEvent event) {
    execute(event.getSession(), () -> {
      OnInsertEntityEvent e = new OnInsertEntityEvent(event.getEntity());
      onEventListeners
          .stream()
          .filter(it -> it.support(event.getEntity()))
          .forEach(it -> it.onInsert(e));
    });
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    execute(event.getSession(), () -> {
      OnUpdateEntityEvent e = new OnUpdateEntityEvent(event.getEntity(), diffs(event));
      onEventListeners
          .stream()
          .filter(it -> it.support(event.getEntity()))
          .forEach(it -> it.onUpdate(e));
    });

  }

  private void execute(EventSource ev, Runnable runnable) {
    if (!SessionManager.isRunnedWithSession()) {
      try (Session temporarySession = ev.sessionWithOptions()
          .connection()
          .autoClose(false)
          .connectionHandlingMode(
              PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION)
          .openSession()) {
        SessionManager.runWithSession(temporarySession, runnable);
        temporarySession.flush();
      }
    }
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

  private List<Diff> diffs(PostUpdateEvent event) {
    String[] names = event.getPersister().getPropertyNames();
    Object[] oldState = event.getOldState();
    Object[] state = event.getState();
    int[] dirtyProperties = event.getDirtyProperties();
    return getDiffs(dirtyProperties, names, oldState, state);
  }

  private List<Diff> getDiffs(int[] dirtyProperties, String[] names, Object[] oldState,
      Object[] state) {
    return unmodifiableList(stream(dirtyProperties)
        .mapToObj(i -> new Diff(names[i], oldState[i], state[i]))
        .collect(Collectors.toList()));
  }

  @Override
  public void register(OnEventListener eventListener) {
    onEventListeners.add(eventListener);
  }
}
