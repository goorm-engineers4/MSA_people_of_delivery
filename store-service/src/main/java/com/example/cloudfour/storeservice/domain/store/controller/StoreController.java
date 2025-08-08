package com.example.cloudfour.storeservice.domain.store.controller;

import com.example.cloudfour.storeservice.domain.store.dto.StoreRequestDTO;
import com.example.cloudfour.storeservice.domain.store.dto.StoreResponseDTO;
import com.example.cloudfour.storeservice.domain.store.service.command.StoreCommandService;
import com.example.cloudfour.storeservice.domain.store.service.query.StoreQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
@Tag(name = "Store", description = "가게 API by 지윤")
public class StoreController {

    private final StoreCommandService storeCommandService;
    private final StoreQueryService storeQueryService;

    private UUID getUserIdFromJwt(Jwt jwt) {
        String userId = jwt.getClaimAsString("userId");
        if (userId == null || userId.isBlank()) userId = jwt.getSubject();
        return UUID.fromString(userId);
    }

    @PostMapping("")
    public ResponseEntity<StoreResponseDTO.StoreCreateResponseDTO> createStore(
            @RequestBody StoreRequestDTO.StoreCreateRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        var body = storeCommandService.createStore(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("")
    @Operation(summary = "가게 목록 조회", description = "전체 가게 목록을 커서 기반으로 조회합니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 기준 시간입니다.")
    @Parameter(name = "size", description = "가져올 데이터 수입니다.")
    public ResponseEntity<StoreResponseDTO.StoreCursorListResponseDTO> getStoreList(
            @RequestParam(name = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        StoreResponseDTO.StoreCursorListResponseDTO body =
                storeQueryService.getAllStores(cursor, size, keyword, userId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 정보 조회", description = "가게의 상세 정보를 조회합니다.")
    public ResponseEntity<StoreResponseDTO.StoreDetailResponseDTO> getStoreDetail(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        StoreResponseDTO.StoreDetailResponseDTO body = storeQueryService.getStoreById(storeId, userId);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{storeId}")
    @Operation(summary = "가게 정보 수정", description = "본인의 가게 정보를 수정합니다.")
    public ResponseEntity<StoreResponseDTO.StoreUpdateResponseDTO> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDTO.StoreUpdateRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        StoreResponseDTO.StoreUpdateResponseDTO body = storeCommandService.updateStore(storeId, dto, userId);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{storeId}/deleted")
    @Operation(summary = "가게 삭제", description = "본인의 가게를 삭제합니다.")
    public ResponseEntity<String> deleteStore(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        storeCommandService.deleteStore(storeId, userId);
        return ResponseEntity.ok("가게 삭제 완료");
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 가게 목록 조회", description = "카테고리 ID로 해당 카테고리의 가게 목록을 커서 기반으로 조회합니다.")
    @Parameter(name = "cursor", description = "데이터가 시작하는 기준 시간입니다.")
    @Parameter(name = "size", description = "가져올 데이터 수입니다.")
    public ResponseEntity<StoreResponseDTO.StoreCursorListResponseDTO> getStoresByCategory(
            @PathVariable UUID categoryId,
            @RequestParam(name = "cursor", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = getUserIdFromJwt(jwt);
        StoreResponseDTO.StoreCursorListResponseDTO body =
                storeQueryService.getStoresByCategory(categoryId, cursor, size, userId);
        return ResponseEntity.ok(body);
    }


}
