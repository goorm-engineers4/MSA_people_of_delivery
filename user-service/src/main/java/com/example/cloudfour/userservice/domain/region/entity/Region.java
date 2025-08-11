package com.example.cloudfour.userservice.domain.region.entity;

import com.example.cloudfour.userservice.domain.region.exception.RegionErrorCode;
import com.example.cloudfour.userservice.domain.region.exception.RegionException;
import com.example.cloudfour.userservice.domain.user.entity.UserAddress;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
        name = "p_region",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_region_sgd",
                columnNames = {"si","gu","dong"}
        )
)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region")
    private List<UserAddress> addresses = new ArrayList<>();

    public static Region ofRaw(String si, String gu, String dong) {
        Region r = new Region();
        r.si = si;
        r.gu = gu;
        r.dong = dong;
        return r;
    }

}
