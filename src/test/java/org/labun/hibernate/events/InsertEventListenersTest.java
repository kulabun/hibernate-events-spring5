package org.labun.hibernate.events;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.labun.hibernate.events.InsertEventListenersTest.InsertTestConfig;
import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.OnInsertEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Konstantin Labun
 */
@SpringBootTest(classes = InsertTestConfig.class)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = InsertEventListenersTest.class)
@Transactional
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class InsertEventListenersTest {

  @Autowired
  private EntityManager em;

  @Test
  public void shouldCallAfterInsert() {
    Person p = new Person("Hello", "World");
    em.persist(p);
    Assertions.assertEquals("Boo", p.getFirstName());
    em.refresh(p);
    Assertions.assertEquals("Hello", p.getFirstName());
  }

  @Configuration
  public static class InsertTestConfig {

    @Bean
    public OnEventListener<Person> eventListener() {
      return new OnEventListener<Person>() {
        @Override
        public void onInsert(OnInsertEntityEvent<Person> event) {
          event.entity().setFirstName("Boo");
        }

        @Override
        public Class<?> getTargetType() {
          return Person.class;
        }
      };
    }
  }

  @Entity
  @Table(name = "person")
  public static class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;

    public Person(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }
}
