package com.appointment.system.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UserServiceImpl}.
 */
@Generated
public class UserService__BeanDefinitions {
  /**
   * Get the bean definition for 'UserServiceImpl'.
   */
  public static BeanDefinition getUserServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UserServiceImpl.class);
    InstanceSupplier<UserServiceImpl> instanceSupplier = InstanceSupplier.using(UserServiceImpl::new);
    instanceSupplier = instanceSupplier.andThen(UserService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
