package com.fastcode.emailApi.domain.irepository;

import com.fastcode.emailApi.domain.model.EmailTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTrackingRepository extends JpaRepository<EmailTracking, Long> {
}


