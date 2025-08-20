package com.example.cloudfour.storeservice.domain.commondto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponseDTO {
    String siDo;
    String siGunGu;
    String eupMyeonDong;
}