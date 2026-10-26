package com.example.servicedirectory.serviceentry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceEntryRepository extends JpaRepository<ServiceEntry, Long>,
        JpaSpecificationExecutor<ServiceEntry> {

    @Override
    @EntityGraph(attributePaths = "team")
    Page<ServiceEntry> findAll(Specification<ServiceEntry> spec, Pageable pageable);
}