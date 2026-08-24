package com.andres.senderos.persistence.repository;

import com.andres.senderos.persistence.entity.HikeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HikeHistoryRepository extends JpaRepository<HikeHistoryEntity, Long> {
    List<HikeHistoryEntity> findByUserId(Long userId);
}
