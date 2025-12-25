package org.springframework.boot.security.autoconfigure.actuate.web.servlet;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Bean definitions for {@link ManagementWebSecurityAutoConfiguration}.
 */
@Generated
public class ManagementWebSecurityAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'managementWebSecurityAutoConfiguration'.
   */
  public static BeanDefinition getManagementWebSecurityAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ManagementWebSecurityAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ManagementWebSecurityAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'managementSecurityFilterChain'.
   */
  private static BeanInstanceSupplier<SecurityFilterChain> getManagementSecurityFilterChainInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<SecurityFilterChain>forFactoryMethod(ManagementWebSecurityAutoConfiguration.class, "managementSecurityFilterChain", Environment.class, HttpSecurity.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean("org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration", ManagementWebSecurityAutoConfiguration.class).managementSecurityFilterChain(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'managementSecurityFilterChain'.
   */
  public static BeanDefinition getManagementSecurityFilterChainBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(SecurityFilterChain.class);
    beanDefinition.setFactoryBeanName("org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration");
    beanDefinition.setInstanceSupplier(getManagementSecurityFilterChainInstanceSupplier());
    return beanDefinition;
  }
}
