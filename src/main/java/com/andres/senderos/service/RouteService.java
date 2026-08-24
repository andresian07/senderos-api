package com.andres.senderos.service;

import com.andres.senderos.dto.RouteCreateDto;
import com.andres.senderos.dto.RouteResponseDto;
import com.andres.senderos.dto.RouteUpdateDto;
import com.andres.senderos.persistence.entity.RouteEntity;
import com.andres.senderos.persistence.repository.RouteRepository;
import com.andres.senderos.web.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public RouteResponseDto get(Long id) {
        Optional<RouteEntity> optionalRouteEntity = this.routeRepository.findById(id);
        RouteEntity route = optionalRouteEntity.orElseThrow(() -> new NotFoundException("la ruta con el id: " + id + " no se encuentra"));

        return this.toResponseDto(route);
    }

    public List<RouteResponseDto> getAll(){
        List<RouteResponseDto> routes = this.routeRepository.findAll().stream().map( route -> toResponseDto(route)).toList();
        return routes;

    }

    public RouteResponseDto save(RouteCreateDto dto){
        RouteEntity newRoute = new RouteEntity();
        newRoute.setName(dto.name());
        newRoute.setRegion(dto.region());
        newRoute.setDistanceKm(dto.distanceKm());
        newRoute.setElevationGainM(dto.elevationGainM());
        newRoute.setDifficulty(dto.difficulty());
        newRoute.setLatitude(dto.latitude());
        newRoute.setLongitude(dto.longitude());
        RouteEntity saveRoute = this.routeRepository.save(newRoute);
        return this.toResponseDto(saveRoute);



    }

    public RouteResponseDto update(Long id, RouteUpdateDto dto) {
        Optional<RouteEntity> optionalRoute = this.routeRepository.findById(id);
        RouteEntity route = optionalRoute.orElseThrow(() -> new NotFoundException("la ruta con el id: " + id + " no se encuentra"));

        if (dto.name() != null) {
            route.setName(dto.name());
        }
        if (dto.region() != null){
            route.setRegion(dto.region());
        }
        if (dto.distanceKm() != null){
            route.setDistanceKm(dto.distanceKm());
        }
        if (dto.elevationGainM() != null){
            route.setElevationGainM(dto.elevationGainM());
        }
        if (dto.difficulty() != null){
            route.setDifficulty(dto.difficulty());
        }
        if (dto.latitude() != null){
            route.setLatitude(dto.latitude());
        }
        if (dto.longitude() != null){
            route.setLongitude(dto.longitude());
        }
        RouteEntity updateDto = this.routeRepository.save(route);
        return this.toResponseDto(updateDto);


    }

    public RouteResponseDto delete(Long id) {
        Optional<RouteEntity> optionalRouteEntity = this.routeRepository.findById(id);
        RouteEntity route = optionalRouteEntity.orElseThrow(() -> new NotFoundException("la ruta con el id: " + id + " no se encuentra"));
        this.routeRepository.delete(route);

        return this.toResponseDto(route);

    }

    private RouteResponseDto toResponseDto(RouteEntity route) {
        // Armá el record RouteResponseDto a partir de los getters de RouteEntity
       return new RouteResponseDto(
               route.getId(),
               route.getName(),
               route.getRegion(),
               route.getDistanceKm(),
               route.getElevationGainM(),
               route.getDifficulty(),
               route.getLatitude(),
               route.getLongitude()
       );


    }
}
