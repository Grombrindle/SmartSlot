package com.appointment.system.service.interfaces;

import com.appointment.system.model.User;

public interface StaffService {
    User registerStaff(String name, String email, String password, 
                      String specialty, String licenseNumber);
    
    User getStaffById(Long id);
}