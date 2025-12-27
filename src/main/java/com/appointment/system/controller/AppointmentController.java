// package com.appointment.system.controller;

// import com.appointment.system.enums.AppointmentStatus;
// import com.appointment.system.model.Appointment;
// import com.appointment.system.service.AppointmentService;
// import com.appointment.system.service.UserService;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.web.PageableDefault;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Controller
// @RequestMapping("/admin")
// @PreAuthorize("hasRole('ADMIN')")
// public class AppointmentController {

//     private final AppointmentService appointmentService;
//     private final UserService userService;

//     public AppointmentController(AppointmentService appointmentService, UserService userService) {
//         this.appointmentService = appointmentService;
//         this.userService = userService;
//     }

//     @GetMapping("/appointments")
//     public String getAllAppointments(
//             Model model,
//             @PageableDefault(sort = "appointmentDateTime", direction = Sort.Direction.DESC) Pageable pageable,
//             @RequestParam(required = false) String status,
//             @RequestParam(required = false) String from,
//             @RequestParam(required = false) String to) {

//         LocalDateTime startDate = from != null && !from.isEmpty() ? 
//                 LocalDate.parse(from).atStartOfDay() : 
//                 LocalDateTime.now().minusMonths(1);
        
//         LocalDateTime endDate = to != null && !to.isEmpty() ? 
//                 LocalDate.parse(to).plusDays(1).atStartOfDay().minusSeconds(1) : 
//                 LocalDateTime.now().plusMonths(1);

//         List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        
//         if (status != null && !status.isEmpty()) {
//             try {
//                 AppointmentStatus statusEnum = AppointmentStatus.valueOf(status.toUpperCase());
//                 appointments = appointments.stream()
//                         .filter(a -> a.getStatus() == statusEnum)
//                         .collect(Collectors.toList());
//             } catch (IllegalArgumentException e) {
//                 // Invalid status, ignore filter
//             }
//         }

//         // Get statistics
//         long pendingCount = appointments.stream()
//                 .filter(a -> a.getStatus() == AppointmentStatus.PENDING)
//                 .count();
        
//         long approvedCount = appointments.stream()
//                 .filter(a -> a.getStatus() == AppointmentStatus.APPROVED)
//                 .count();
        
//         long completedCount = appointments.stream()
//                 .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
//                 .count();

//         model.addAttribute("appointments", appointments);
//         model.addAttribute("pendingCount", pendingCount);
//         model.addAttribute("approvedCount", approvedCount);
//         model.addAttribute("completedCount", completedCount);
//         model.addAttribute("statusFilter", status);
//         model.addAttribute("dateFrom", from);
//         model.addAttribute("dateTo", to);

//         return "admin-appointments";
//     }
// }