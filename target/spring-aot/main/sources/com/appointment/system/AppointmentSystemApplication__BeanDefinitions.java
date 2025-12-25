package com.appointment.system;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AppointmentSystemApplication}.
 */
@Generated
public class AppointmentSystemApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'appointmentSystemApplication'.
   */
  public static BeanDefinition getAppointmentSystemApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AppointmentSystemApplication.class);
    beanDefinition.setInstanceSupplier(AppointmentSystemApplication::new);
    return beanDefinition;
  }
}
