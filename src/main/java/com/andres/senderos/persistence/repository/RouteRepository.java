package com.andres.senderos.persistence.repository;

import com.andres.senderos.persistence.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
}