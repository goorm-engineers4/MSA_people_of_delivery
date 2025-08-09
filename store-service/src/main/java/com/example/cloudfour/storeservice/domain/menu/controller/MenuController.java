package com.example.cloudfour.storeservice.domain.menu.controller;

import com.example.cloudfour.storeservice.domain.menu.dto.MenuRequestDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.dto.MenuOptionResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.service.command.MenuCommandService;
import com.example.cloudfour.storeservice.domain.menu.service.query.MenuQueryService;
import com.example.modulecommon.apiPayLoad.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
@Tag(name = "Menu", description = "메뉴 API by 정병민")
public class MenuController {

    private final MenuCommandService menuCommandService;
    private final MenuQueryService menuQueryService;

    
    private UUID getUserIdFromJwt(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        if (userId == null || userId.isBlank()) {
            userId = jwt.getSubject();
        }
        return UUID.fromString(userId);
    }

    @PostMapping("/{storeId}")
    @Operation(summary = "메뉴 생성", description = "메뉴를 생성합니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> createMenu(
            @PathVariable("storeId") UUID storeId,
            @RequestBody MenuRequestDTO.MenuCreateRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.createMenu(requestDTO, storeId, userId);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @GetMapping("/{menuId}/detail")
    @Operation(summary = "메뉴 상세 조회", description = "메뉴의 상세 정보를 조회합니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> getMenuDetail(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuResponseDTO.MenuDetailResponseDTO result = menuQueryService.getMenuDetail(menuId, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "해당 가게 메뉴 목록 조회", description = "가게의 메뉴 목록을 조회합니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 부분을 표시합니다")
    @Parameter(name = "size", description = "size만큼 데이터를 가져옵니다.")
    public CustomResponse<MenuResponseDTO.MenuStoreListResponseDTO> getMenusByStore(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(name = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuResponseDTO.MenuStoreListResponseDTO result =
                menuQueryService.getMenusByStoreWithCursor(storeId, cursor, size, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/{storeId}/category")
    @Operation(summary = "해당 가게 메뉴 카테고리 별 목록 조회", description = "가게의 카테고리 별 목록을 조회합니다.")
    public CustomResponse<MenuResponseDTO.MenuStoreListResponseDTO> getMenusByCategory(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(name = "categoryId") UUID categoryId,
            @RequestParam(name = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuResponseDTO.MenuStoreListResponseDTO result =
                menuQueryService.getMenusByStoreWithCategory(storeId, categoryId, cursor, size, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/top")
    @Operation(summary = "인기 메뉴 TOP20 조회", description = "인기 메뉴 TOP20을 조회합니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTopResponseDTO>> getTopMenus(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        List<MenuResponseDTO.MenuTopResponseDTO> result = menuQueryService.getTopMenus(userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/timetop")
    @Operation(summary = "시간대별 인기 메뉴 TOP20 조회", description = "시간대별 인기 메뉴 TOP20을 조회합니다.")
    public CustomResponse<List<MenuResponseDTO.MenuTimeTopResponseDTO>> getTimeTopMenus(
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        List<MenuResponseDTO.MenuTimeTopResponseDTO> result = menuQueryService.getTimeTopMenus(userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/regiontop")
    @Operation(summary = "지역별 인기 메뉴 TOP20 조회", description = "지역별 인기 메뉴 TOP20을 조회합니다.")
    public CustomResponse<List<MenuResponseDTO.MenuRegionTopResponseDTO>> getRegionTopMenus(
            @RequestParam String si,
            @RequestParam String gu,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        List<MenuResponseDTO.MenuRegionTopResponseDTO> result = menuQueryService.getRegionTopMenus(si, gu, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PatchMapping("/{menuId}")
    @Operation(summary = "메뉴 수정", description = "메뉴를 수정합니다.")
    public CustomResponse<MenuResponseDTO.MenuDetailResponseDTO> updateMenu(
            @RequestBody MenuRequestDTO.MenuUpdateRequestDTO requestDTO,
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuResponseDTO.MenuDetailResponseDTO result = menuCommandService.updateMenu(menuId, requestDTO, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @DeleteMapping("/{menuId}/deleted")
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다.")
    public CustomResponse<String> deleteMenu(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        menuCommandService.deleteMenu(menuId, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, "메뉴 삭제 완료");
    }

    @PostMapping("/{menuId}/options")
    @Operation(summary = "메뉴 옵션 생성", description = "특정 메뉴에 새로운 옵션을 추가합니다.")
    public CustomResponse<MenuOptionResponseDTO.MenuOptionDetailResponseDTO> createMenuOption(
            @PathVariable("menuId") UUID menuId,
            @RequestBody MenuRequestDTO.MenuOptionCreateRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);

        MenuOptionResponseDTO.MenuOptionDetailResponseDTO result =
                menuCommandService.createMenuOption(requestDTO, userId, menuId);
        return CustomResponse.onSuccess(HttpStatus.CREATED, result);
    }

    @GetMapping("/{menuId}/options")
    @Operation(summary = "메뉴별 옵션 목록 조회", description = "특정 메뉴의 모든 옵션을 조회합니다.")
    public CustomResponse<MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO> getMenuOptions(
            @PathVariable("menuId") UUID menuId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuOptionResponseDTO.MenuOptionsByMenuResponseDTO result =
                menuQueryService.getMenuOptionsByMenu(menuId, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @GetMapping("/options/{optionId}/detail")
    @Operation(summary = "메뉴 옵션 상세 조회", description = "메뉴 옵션의 상세 정보를 조회합니다.")
    public CustomResponse<MenuOptionResponseDTO.MenuOptionDetailResponseDTO> getMenuOptionDetail(
            @PathVariable("optionId") UUID optionId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuOptionResponseDTO.MenuOptionDetailResponseDTO result =
                menuQueryService.getMenuOptionDetail(optionId, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @PatchMapping("/options/{optionId}")
    @Operation(summary = "메뉴 옵션 수정", description = "메뉴 옵션의 정보를 수정합니다.")
    public CustomResponse<MenuOptionResponseDTO.MenuOptionDetailResponseDTO> updateMenuOption(
            @PathVariable("optionId") UUID optionId,
            @RequestBody MenuRequestDTO.MenuOptionUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        MenuOptionResponseDTO.MenuOptionDetailResponseDTO result =
                menuCommandService.updateMenuOption(optionId, requestDTO, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, result);
    }

    @DeleteMapping("/options/{optionId}/deleted")
    @Operation(summary = "메뉴 옵션 삭제", description = "메뉴 옵션을 삭제합니다.")
    public CustomResponse<String> deleteMenuOption(
            @PathVariable("optionId") UUID optionId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = getUserIdFromJwt(jwt);
        menuCommandService.deleteMenuOption(optionId, userId);
        return CustomResponse.onSuccess(HttpStatus.OK, "메뉴 옵션 삭제 완료");
    }
}
