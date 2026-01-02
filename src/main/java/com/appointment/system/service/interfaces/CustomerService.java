package com.appointment.system.service.interfaces;

import com.appointment.system.model.User;

public interface CustomerService {
    User registerCustomer(String name, String email, String password, 
                         String phoneNumber, String address);
    
    User getCustomerById(Long id);
}