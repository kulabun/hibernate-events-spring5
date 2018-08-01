package org.labun.hibernate.events;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.labun.hibernate.events.api.Diff;
import org.labun.hibernate.events.api.OnDeleteEntityEvent;
import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.OnInsertEntityEvent;
import org.labun.hibernate.events.api.OnUpdateEntityEvent;
import org.labun.hibernate.events.api.VetoeingEventListener;

/**
 * @author Konstantin Labun
 */
public class EventListenerService implements
    PreInsertEventListener, PostInsertEventListener,
    PreUpdateEventListener, PostUpdateEventListener,
    PreDeleteEventListener, PostDeleteEventListener,
    EventListenerRegistrar {

  private List<OnEventListener> onEventListeners = new ArrayList<>();
  private List<VetoeingEventListener> vetoeingEventListeners = new ArrayList<>();

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    OnDeleteEntityEvent e = new OnDeleteEntityEvent<>(event.getEntity());
    onEventListeners
        .stream()
        .filter(it -> it.support(event.getEntity()))
        .forEach(it -> it.onDelete(e));
  }

  @Override
  public void onPostInsert(PostInsertEvent event) {
    OnInsertEntityEvent e = new OnInsertEntityEvent(event.getEntity());
    onEventListeners
        .stream()
        .filter(it -> it.support(event.getEntity()))
        .forEach(it -> it.onInsert(e));
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    OnUpdateEntityEvent e = new OnUpdateEntityEvent(event.getEntity(), diffs(event));
    onEventListeners
        .stream()
        .filter(it -> it.support(event.getEntity()))
        .forEach(it -> it.onUpdate(e));
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

  @Override
  public boolean onPreDelete(PreDeleteEvent event) {
    OnDeleteEntityEvent e = new OnDeleteEntityEvent(event.getEntity());
    return delegateUntilVetoed(listener -> listener.vetoeForDelete(e), e.entity());
  }

  @Override
  public boolean onPreInsert(PreInsertEvent event) {
    OnInsertEntityEvent e = new OnInsertEntityEvent<>(event.getEntity());
    return delegateUntilVetoed(listener -> listener.vetoeForInsert(e), e.entity());
  }

  @Override
  public boolean onPreUpdate(PreUpdateEvent event) {
    OnUpdateEntityEvent e = new OnUpdateEntityEvent<>(event.getEntity(), diffs(event));
    return delegateUntilVetoed(listener -> listener.vetoeForUpdate(e), e.entity());
  }

  private boolean delegateUntilVetoed(Function<VetoeingEventListener, Boolean> f, Object entity) {
    Iterator<VetoeingEventListener> it = vetoeingEventListeners.iterator();
    boolean veto = false;
    while (it.hasNext() && !veto) {
      VetoeingEventListener listener = it.next();
      if (listener.support(entity)) {
        f.apply(listener);
      }
    }
    return veto;
  }

  private List<Diff> diffs(PostUpdateEvent event) {
    String[] names = event.getPersister().getPropertyNames();
    Object[] oldState = event.getOldState();
    Object[] state = event.getState();
    int[] dirtyProperties = event.getDirtyProperties();
    return getDiffs(dirtyProperties, names, oldState, state);
  }

  private List<Diff> diffs(PreUpdateEvent event) {
    String[] names = event.getPersister().getPropertyNames();
    Object[] oldState = event.getOldState();
    Object[] state = event.getState();
    int[] dirtyProperties = calculateDirtyProperties(oldState, state);
    return getDiffs(dirtyProperties, names, oldState, state);
  }

  private int[] calculateDirtyProperties(Object[] oldState, Object[] state) {
    int[] changed = new int[state.length];
    int pointer = 0;
    for (int i = 0; i < oldState.length; i++) {
      if (!Objects.equals(oldState[i], state[i])) {
        changed[pointer] = i;
        pointer++;
      }
    }
    return Arrays.copyOf(changed, pointer);
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

  @Override
  public void register(VetoeingEventListener eventListener) {
    vetoeingEventListeners.add(eventListener);
  }
}
