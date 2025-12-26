package com.appointment.system.config;

import com.appointment.system.enums.AppointmentStatus;
import com.appointment.system.enums.UserRole;
import com.appointment.system.model.*;
import com.appointment.system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, 
                     ServiceRepository serviceRepository,
                     WorkingScheduleRepository workingScheduleRepository,
                     AppointmentRepository appointmentRepository,
                     BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.workingScheduleRepository = workingScheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create ADMIN user
        User admin = userRepository.findByEmail("admin@example.com")
            .orElseGet(() -> {
                User a = new User();
                a.setName("System Admin");
                a.setEmail("admin@example.com");
                a.setPassword(passwordEncoder.encode("admin123"));
                a.setRole(UserRole.ADMIN);
                a.setCompanyName("Appointment System Inc.");
                a.setActive(true);
                return userRepository.save(a);
            });
        
        // Create STAFF users
        User staff1 = userRepository.findByEmail("dr.smith@clinic.com")
            .orElseGet(() -> {
                User s = new User();
                s.setName("Dr. John Smith");
                s.setEmail("dr.smith@clinic.com");
                s.setPassword(passwordEncoder.encode("staff123"));
                s.setRole(UserRole.STAFF);
                s.setSpecialty("Dentist");
                s.setLicenseNumber("DENT-12345");
                s.setActive(true);
                return userRepository.save(s);
            });
        
        User staff2 = userRepository.findByEmail("nurse.jones@clinic.com")
            .orElseGet(() -> {
                User s = new User();
                s.setName("Nurse Sarah Jones");
                s.setEmail("nurse.jones@clinic.com");
                s.setPassword(passwordEncoder.encode("staff123"));
                s.setRole(UserRole.STAFF);
                s.setSpecialty("Registered Nurse");
                s.setLicenseNumber("RN-67890");
                s.setActive(true);
                return userRepository.save(s);
            });
        
        // Create CUSTOMER users
        User customer1 = userRepository.findByEmail("customer1@example.com")
            .orElseGet(() -> {
                User c = new User();
                c.setName("Alice Johnson");
                c.setEmail("customer1@example.com");
                c.setPassword(passwordEncoder.encode("customer123"));
                c.setRole(UserRole.CUSTOMER);
                c.setPhoneNumber("+1-555-0101");
                c.setAddress("123 Main St, New York, NY");
                c.setActive(true);
                return userRepository.save(c);
            });
        
        User customer2 = userRepository.findByEmail("customer2@example.com")
            .orElseGet(() -> {
                User c = new User();
                c.setName("Bob Williams");
                c.setEmail("customer2@example.com");
                c.setPassword(passwordEncoder.encode("customer123"));
                c.setRole(UserRole.CUSTOMER);
                c.setPhoneNumber("+1-555-0102");
                c.setAddress("456 Oak Ave, Los Angeles, CA");
                c.setActive(true);
                return userRepository.save(c);
            });
        
        // Create Services if they don't exist
        if (serviceRepository.count() == 0) {
            Service service1 = new Service();
            service1.setName("Dental Cleaning");
            service1.setDescription("Professional teeth cleaning and oral examination");
            service1.setDuration(60);
            service1.setPrice(120.0);
            service1.setProvider(staff1);
            service1.setActive(true);
            serviceRepository.save(service1);
            
            Service service2 = new Service();
            service2.setName("Teeth Whitening");
            service2.setDescription("Professional teeth whitening treatment");
            service2.setDuration(90);
            service2.setPrice(250.0);
            service2.setProvider(staff1);
            service2.setActive(true);
            serviceRepository.save(service2);
            
            Service service3 = new Service();
            service3.setName("Basic Checkup");
            service3.setDescription("General health checkup and consultation");
            service3.setDuration(30);
            service3.setPrice(75.0);
            service3.setProvider(staff2);
            service3.setActive(true);
            serviceRepository.save(service3);
            
            Service service4 = new Service();
            service4.setName("Vaccination");
            service4.setDescription("Annual flu vaccination");
            service4.setDuration(15);
            service4.setPrice(50.0);
            service4.setProvider(staff2);
            service4.setActive(true);
            serviceRepository.save(service4);
        }
        
        // Create Working Schedules
        if (workingScheduleRepository.count() == 0) {
            // Staff 1 schedule (Monday to Friday, 9am-5pm)
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                    WorkingSchedule schedule = new WorkingSchedule();
                    schedule.setStaff(staff1);
                    schedule.setDayOfWeek(day);
                    schedule.setStartTime(LocalTime.of(9, 0));
                    schedule.setEndTime(LocalTime.of(17, 0));
                    schedule.setHoliday(false);
                    workingScheduleRepository.save(schedule);
                }
            }
            
            // Staff 2 schedule (Monday to Saturday, 8am-4pm)
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day != DayOfWeek.SUNDAY) {
                    WorkingSchedule schedule = new WorkingSchedule();
                    schedule.setStaff(staff2);
                    schedule.setDayOfWeek(day);
                    schedule.setStartTime(LocalTime.of(8, 0));
                    schedule.setEndTime(LocalTime.of(16, 0));
                    schedule.setHoliday(false);
                    workingScheduleRepository.save(schedule);
                }
            }
        }
        
        // Create Appointments
        if (appointmentRepository.count() == 0) {
            Service dentalService = serviceRepository.findByIsActiveTrue().get(0);
            Service checkupService = serviceRepository.findByIsActiveTrue().get(2);
            
            // Create some past appointments
            Appointment appointment1 = new Appointment();
            appointment1.setCustomer(customer1);
            appointment1.setStaff(staff1);
            appointment1.setService(dentalService);
            appointment1.setAppointmentDateTime(LocalDateTime.now().minusDays(5).withHour(10).withMinute(0));
            appointment1.setEndDateTime(appointment1.getAppointmentDateTime().plusMinutes(dentalService.getDuration()));
            appointment1.setStatus(AppointmentStatus.COMPLETED);
            appointment1.setNotes("Regular checkup");
            appointmentRepository.save(appointment1);
            
            Appointment appointment2 = new Appointment();
            appointment2.setCustomer(customer2);
            appointment2.setStaff(staff2);
            appointment2.setService(checkupService);
            appointment2.setAppointmentDateTime(LocalDateTime.now().minusDays(3).withHour(11).withMinute(0));
            appointment2.setEndDateTime(appointment2.getAppointmentDateTime().plusMinutes(checkupService.getDuration()));
            appointment2.setStatus(AppointmentStatus.COMPLETED);
            appointment2.setNotes("Annual checkup");
            appointmentRepository.save(appointment2);
            
            // Create some upcoming appointments
            Appointment appointment3 = new Appointment();
            appointment3.setCustomer(customer1);
            appointment3.setStaff(staff1);
            appointment3.setService(dentalService);
            appointment3.setAppointmentDateTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0));
            appointment3.setEndDateTime(appointment3.getAppointmentDateTime().plusMinutes(dentalService.getDuration()));
            appointment3.setStatus(AppointmentStatus.APPROVED);
            appointment3.setNotes("Follow-up appointment");
            appointmentRepository.save(appointment3);
            
            Appointment appointment4 = new Appointment();
            appointment4.setCustomer(customer2);
            appointment4.setStaff(staff2);
            appointment4.setService(checkupService);
            appointment4.setAppointmentDateTime(LocalDateTime.now().plusDays(3).withHour(9).withMinute(30));
            appointment4.setEndDateTime(appointment4.getAppointmentDateTime().plusMinutes(checkupService.getDuration()));
            appointment4.setStatus(AppointmentStatus.PENDING);
            appointment4.setNotes("New patient");
            appointmentRepository.save(appointment4);
        }
    }
}