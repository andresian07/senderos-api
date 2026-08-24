package com.andres.senderos.service;

import com.andres.senderos.dto.HikeHistoryCreateDto;
import com.andres.senderos.dto.HikeHistoryResponseDto;
import com.andres.senderos.dto.RouteResponseDto;
import com.andres.senderos.dto.RouteUpdateDto;
import com.andres.senderos.persistence.entity.HikeHistoryEntity;
import com.andres.senderos.persistence.entity.RouteEntity;
import com.andres.senderos.persistence.entity.UserEntity;
import com.andres.senderos.persistence.repository.HikeHistoryRepository;
import com.andres.senderos.persistence.repository.RouteRepository;
import com.andres.senderos.persistence.repository.UserRepository;
import com.andres.senderos.web.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HikeHistoryService {
    private final HikeHistoryRepository hikeHistoryRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public HikeHistoryService(HikeHistoryRepository hikeHistoryRepository, RouteRepository routeRepository, UserRepository userRepository) {
        this.hikeHistoryRepository = hikeHistoryRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    public HikeHistoryResponseDto save(HikeHistoryCreateDto dto, Long userId) {
        UserEntity user = this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("el usuario con el id: " + userId + " no se encuentra"));


        RouteEntity route = this.routeRepository.findById(dto.routeId())
                .orElseThrow(() -> new NotFoundException("la ruta con el id: " + dto.routeId() + " no se encuentra"));

        HikeHistoryEntity newHike = new HikeHistoryEntity();
        newHike.setUser(user);
        newHike.setRoute(route);
        newHike.setCompletedAt(dto.completedAt());


        HikeHistoryEntity saved = this.hikeHistoryRepository.save(newHike);
        return this.toResponseDto(saved);
    }

    public List<HikeHistoryResponseDto> getHistoryForUser(Long userId) {
        return this.hikeHistoryRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }




    private HikeHistoryResponseDto toResponseDto(HikeHistoryEntity hike) {
        return new HikeHistoryResponseDto(
                hike.getId(),
                hike.getUser().getId(),
                hike.getRoute().getId(),
                hike.getRoute().getName(),
                hike.getRoute().getDifficulty(),
                hike.getCompletedAt()
        );
    }
}
