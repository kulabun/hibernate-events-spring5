package org.labun.hibernate.events;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Konstantin Labun
 */
public class TransactionExecutor {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void run(Runnable runnable) {
    runnable.run();
  }
}
