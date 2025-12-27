package com.appointment.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class AppointmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentSystemApplication.class, args);
	}

}
