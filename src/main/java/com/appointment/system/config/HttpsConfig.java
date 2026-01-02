package com.appointment.system.config;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
// import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
// import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                // Security constraints for HTTPS redirection
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        
        // Add HTTP connector for redirection
        tomcat.addAdditionalConnectors(createHttpConnector());
        return tomcat;
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080); // HTTP port
        connector.setSecure(false);
        connector.setRedirectPort(8443); // HTTPS port
        return connector;
    }
}