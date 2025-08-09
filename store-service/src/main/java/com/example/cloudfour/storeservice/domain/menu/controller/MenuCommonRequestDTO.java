package com.example.cloudfour.storeservice.domain.menu.controller;
import com.example.cloudfour.storeservice.domain.menu.enums.MenuStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuCommonRequestDTO {
    @Getter
    public static class MenuCommonMainRequestDTO{
        private String name;
        private String content;
        private Integer price;
        private String menuPicture;
        private MenuStatus status;
        private String category;
    }

    @Getter
    public static class MenuOptionCommonRequestDTO{
        private String optionName;
        private Integer additionalPrice;
    }
}
