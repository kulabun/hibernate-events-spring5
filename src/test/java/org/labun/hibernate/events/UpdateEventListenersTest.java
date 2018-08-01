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
import org.labun.hibernate.events.UpdateEventListenersTest.UpdateTestConfig;
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
@SpringBootTest(classes = UpdateTestConfig.class)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = UpdateEventListenersTest.class)
@Transactional
public class UpdateEventListenersTest {

  @Autowired
  private EntityManager em;

  @Test
  public void shouldCallAfterUpdate() {
    Dog p = new Dog("Hello", 5);
    em.persist(p);
    em.flush();

    p.setName("Marry");
    em.merge(p);
    em.flush();
    Assertions.assertEquals("Hulk", p.getName());

    em.refresh(p);
    Assertions.assertEquals("Marry", p.getName());
  }

  @Configuration
  public static class UpdateTestConfig {

    @Bean
    public OnEventListener<Dog> eventListener() {
      return new OnEventListener<Dog>() {
        @Override
        public void onUpdate(OnUpdateEntityEvent<Dog> event) {
          event.entity().setName("Hulk");
        }

        @Override
        public Class<?> getTargetType() {
          return Dog.class;
        }
      };
    }
  }

  @Entity
  @Table(name = "dog")
  public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private int age;

    public Dog(String name, int age) {
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
