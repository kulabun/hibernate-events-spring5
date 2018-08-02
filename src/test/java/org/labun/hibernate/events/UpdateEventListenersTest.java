package org.labun.hibernate.events;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Konstantin Labun
 */
@SpringBootTest(classes = UpdateTestConfig.class)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = UpdateEventListenersTest.class)
@Transactional
@TestPropertySource(properties = "logging.level.org.hibernate.SQL=debug")
public class UpdateEventListenersTest {

  @Autowired
  private TransactionExecutor transactionExecutor;

  @Test
  public void shouldCallAfterUpdate() {
    transactionExecutor.run(() -> {
      Dog p1 = new Dog("Dog1", 2);
      SessionManager.getSession().persist(p1);
      Dog p2 = new Dog("Dog2", 3);
      SessionManager.getSession().persist(p2);
    });

    transactionExecutor.run(() -> {
      Dog p1 = SessionManager.getSession().find(Dog.class, 1L);
      p1.setName("Marry");
      SessionManager.getSession().merge(p1);
    });

    transactionExecutor.run(() -> {
      Assertions.assertEquals("Marry", SessionManager.getSession().find(Dog.class, 1L).getName());
      Assertions.assertEquals("Hulk", SessionManager.getSession().find(Dog.class, 2L).getName());
    });
  }

  @Configuration
  public static class UpdateTestConfig {

    @Bean
    public OnEventListener<Dog> eventListener() {
      return new OnEventListener<Dog>() {
        @Override
        public void onUpdate(OnUpdateEntityEvent<Dog> event) {
          Dog d2 = SessionManager.getSession().find(Dog.class, 2L);
          d2.setName("Hulk");
          SessionManager.getSession().merge(d2);
        }

        @Override
        public Class<?> getTargetType() {
          return Dog.class;
        }
      };
    }

    @Bean
    public TransactionExecutor transactionExecutor() {
      return new TransactionExecutor();
    }
  }

  @Entity
  @Table(name = "dog")
  public static class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private int age;

    public Dog() {
    }

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
