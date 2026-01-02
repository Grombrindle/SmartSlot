package com.appointment.system.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UserServiceImpl}.
 */
@Generated
public class UserService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UserServiceImpl apply(RegisteredBean registeredBean, UserServiceImpl instance) {
    AutowiredFieldValueResolver.forRequiredField("userRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
