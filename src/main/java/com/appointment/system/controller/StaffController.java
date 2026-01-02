// package com.appointment.system.controller;

// import com.appointment.system.dto.Responses.ApiResponse;
// import com.appointment.system.dto.Responses.StaffResponse;
// import com.appointment.system.service.StaffServiceImpl;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/staff")
// @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
// public class StaffController extends BaseController {
    
//     @Autowired
//     private StaffServiceImpl StaffServiceImpl;
    
//     @GetMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
//     public ResponseEntity<ApiResponse<StaffResponse>> getStaffProfile(@PathVariable Long id) {
//         return ok(StaffResponse.fromUser(StaffServiceImpl.getStaffById(id)), "Staff profile retrieved");
//     }
    
//     @GetMapping("/all")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllStaff() {
//         List<StaffResponse> staffList = StaffServiceImpl.getAllStaff().stream()
//                 .map(StaffResponse::fromUser)
//                 .collect(Collectors.toList());
//         return ok(staffList, "All staff members retrieved");
//     }
// }