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
import org.labun.hibernate.events.DeleteEventListenersTest.DeleteTestConfig;
import org.labun.hibernate.events.UpdateEventListenersTest.UpdateTestConfig;
import org.labun.hibernate.events.api.OnDeleteEntityEvent;
import org.labun.hibernate.events.api.OnEventListener;
import org.labun.hibernate.events.api.OnUpdateEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Konstantin Labun
 */
@SpringBootTest(classes = DeleteTestConfig.class)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = DeleteEventListenersTest.class)
@Transactional
public class DeleteEventListenersTest {

  @Autowired
  private EntityManager em;

  @Test
  public void shouldCallAfterDelete() {
    Cat p = new Cat("Hello", 5);
    em.persist(p);
    em.flush();

    em.remove(p);
    em.flush();
    Assertions.assertEquals("Hulk", p.getName());

    Assertions.assertEquals(false, em.contains(p));
  }

  @Configuration
  public static class DeleteTestConfig {

    @Bean
    public OnEventListener<Cat> eventListener() {
      return new OnEventListener<Cat>() {
        @Override
        public void onDelete(OnDeleteEntityEvent<Cat> event) {
          event.entity().setName("Hulk");
        }

        @Override
        public Class<?> getTargetType() {
          return Cat.class;
        }
      };
    }
  }

  @Entity
  @Table(name = "cat")
  public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private int age;

    public Cat(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }
}
