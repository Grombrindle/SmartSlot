package com.appointment.system.config;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link DataLoader}.
 */
@Generated
public class DataLoader__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static DataLoader apply(RegisteredBean registeredBean, DataLoader instance) {
    AutowiredFieldValueResolver.forRequiredField("userRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
