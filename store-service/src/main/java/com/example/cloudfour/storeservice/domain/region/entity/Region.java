package com.example.cloudfour.storeservice.domain.region.entity;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "p_region")
public class Region {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String si;

    @Column(nullable = false)
    private String gu;

    @Column(nullable = false)
    private String dong;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    public static class RegionBuilder{
        private RegionBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

}
