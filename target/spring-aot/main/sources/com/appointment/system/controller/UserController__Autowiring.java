package com.appointment.system.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UserController}.
 */
@Generated
public class UserController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UserController apply(RegisteredBean registeredBean, UserController instance) {
    AutowiredFieldValueResolver.forRequiredField("UserServiceImpl").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
