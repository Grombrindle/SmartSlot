package com.appointment.system.repository;

import com.appointment.system.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Page<Service> findByIsActiveTrue(Pageable pageable);
    List<Service> findByProviderIdAndIsActiveTrue(Long providerId);
    List<Service> findByIsActiveTrue();
    Optional<Service> findByIdAndIsActiveTrue(Long id);
}