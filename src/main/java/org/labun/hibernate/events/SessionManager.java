package org.labun.hibernate.events;

import javax.persistence.EntityManager;
import org.hibernate.Session;

/**
 * @author Konstantin Labun
 */
public class SessionManager {

  private static ThreadLocal<Session> sessionStore = new ThreadLocal<>();
  private static EntityManager em;

  static void init(EntityManager em) {
    SessionManager.em = em;
  }

  public static Session getSession() {
    Session session = sessionStore.get();
    if (session != null) {
      return session;
    }
    return em.unwrap(Session.class);
  }

  static void runWithSession(Session session, Runnable runnable) {
    if (sessionStore.get() != null) {
      throw new IllegalStateException("");
    }
    sessionStore.set(session);
    try {
      runnable.run();
    } finally {
      sessionStore.remove();
    }
  }

  static boolean isRunnedWithSession() {
    return sessionStore.get() != null;
  }
}
