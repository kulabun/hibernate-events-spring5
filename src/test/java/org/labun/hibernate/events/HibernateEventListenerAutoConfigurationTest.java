package org.labun.hibernate.events;

import javax.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Konstantin Labun
 */
@SpringBootTest(classes = HibernateEventListenerAutoConfigurationTest.class)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = HibernateEventListenerAutoConfigurationTest.class)
@Transactional
public class HibernateEventListenerAutoConfigurationTest {

  @Autowired
  private EventListenerRegistrar registrar;

  @Test
  public void shouldLoadContext() {
    Assertions.assertNotNull(registrar);
  }
}
