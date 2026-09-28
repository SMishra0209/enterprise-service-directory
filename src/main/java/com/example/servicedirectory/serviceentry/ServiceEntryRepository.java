package com.example.servicedirectory.serviceentry;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceEntryRepository extends JpaRepository<ServiceEntry, Long> {
}