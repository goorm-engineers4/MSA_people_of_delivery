package com.example.cloudfour.userservice.domain.user.controller;

import com.example.cloudfour.modulecommon.apiPayLoad.CustomResponse;
import com.example.cloudfour.userservice.domain.user.dto.UserRequestDTO;
import com.example.cloudfour.userservice.domain.user.dto.UserResponseDTO;
import com.example.cloudfour.userservice.domain.user.service.UserAddressService;
import com.example.cloudfour.userservice.domain.user.service.UserService;
import com.example.cloudfour.userservice.security.GatewayPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API by 모시은")
public class UserController {
    private final UserService userService;
    private final UserAddressService addressService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "내 계정의 상세 정보를 조회합니다.")
    public CustomResponse<UserResponseDTO.MeResponseDTO> getMyInfo(@AuthenticationPrincipal GatewayPrincipal user) {
        return CustomResponse.onSuccess(userService.getMyInfo(user.userId()));
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "내 계정의 상세 정보를 수정합니다.")
    public CustomResponse<Void> updateMyInfo(@AuthenticationPrincipal GatewayPrincipal user,
                                             @Valid @RequestBody UserRequestDTO.UserUpdateRequestDTO request) {
        userService.updateProfile(user.userId(), request.nickname(), request.number());
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/deleted")
    @Operation(summary = "내 계정 삭제", description = "내 계정을 삭제합니다.")
    public CustomResponse<Void> deleteAccount(@AuthenticationPrincipal GatewayPrincipal user) {
        userService.deleteAccount(user.userId());
        return CustomResponse.onSuccess(null);
    }

    @PostMapping("/addresses")
    @Operation(summary = "내 주소 등록", description = "내 주소를 등록합니다.")
    public CustomResponse<Void> addAddress(@Valid @RequestBody UserRequestDTO.AddressRequestDTO request,
                                           @AuthenticationPrincipal GatewayPrincipal userDetails) {
        addressService.addAddress(userDetails.userId(), request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @PostMapping("/addresses/by-text")
    @Operation(summary = "내 주소 등록(텍스트)", description = "전체 주소 문자열을 파싱해 시/구/동을 자동 등록합니다.")
    public CustomResponse<Void> addAddressByText(@Valid @RequestBody UserRequestDTO.AddressTextRequestDTO request,
                                                 @AuthenticationPrincipal GatewayPrincipal userDetails) {
        addressService.addAddressByText(userDetails.userId(), request);
        return CustomResponse.onSuccess(HttpStatus.CREATED, null);
    }

    @GetMapping("/addresses")
    @Operation(summary = "내 주소 조회", description = "내 주소를 조회합니다.")
    public CustomResponse<List<UserResponseDTO.AddressResponseDTO>> getAddressList(
            @AuthenticationPrincipal GatewayPrincipal user) {
        List<UserResponseDTO.AddressResponseDTO> list = addressService.getAddresses(user.userId());
        return CustomResponse.onSuccess(list);
    }

    @PatchMapping("/addresses/{addressId}")
    @Operation(summary = "내 주소 수정", description = "내 주소를 수정합니다.")
    public CustomResponse<Void> updateAddress(@PathVariable UUID addressId,
                                              @Valid @RequestBody UserRequestDTO.AddressRequestDTO request,
                                              @AuthenticationPrincipal GatewayPrincipal user) {
        addressService.updateAddress(user.userId(), addressId, request);
        return CustomResponse.onSuccess(null);
    }

    @PatchMapping("/addresses/delete/{addressId}")
    @Operation(summary = "내 주소 삭제", description = "내 주소를 삭제합니다.")
    public CustomResponse<Void> deleteAddress(@PathVariable UUID addressId,
                                              @AuthenticationPrincipal GatewayPrincipal user) {
        addressService.deleteAddress(user.userId(), addressId);
        return CustomResponse.onSuccess(null);
    }
}
