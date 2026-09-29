package com.example.servicedirectory.serviceentry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceEntryRepository extends JpaRepository<ServiceEntry, Long>,
        JpaSpecificationExecutor<ServiceEntry> {
}