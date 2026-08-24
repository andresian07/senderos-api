package com.andres.senderos.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Table(name = "hike_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HikeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hike_history")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "id_route")
    private RouteEntity route;

    @Column(name = "completed_at")
    private LocalDate completedAt;


}
