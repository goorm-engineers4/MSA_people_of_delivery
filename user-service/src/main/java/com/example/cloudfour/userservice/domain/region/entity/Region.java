package com.example.cloudfour.userservice.domain.region.entity;

import com.example.cloudfour.userservice.domain.user.entity.UserAddress;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<UserAddress> addresses = new ArrayList<>();

    public static class RegionBuilder{
        private RegionBuilder id(UUID id){
            throw new UnsupportedOperationException("id 수정 불가");
        }
    }

}
