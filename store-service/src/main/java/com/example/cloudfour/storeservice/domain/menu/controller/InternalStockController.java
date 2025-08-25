package com.example.cloudfour.storeservice.domain.menu.controller;

import com.example.cloudfour.storeservice.domain.menu.dto.StockResponseDTO;
import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.example.cloudfour.storeservice.domain.menu.entity.Stock;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.MenuException;
import com.example.cloudfour.storeservice.domain.menu.exception.StockErrorCode;
import com.example.cloudfour.storeservice.domain.menu.exception.StockException;
import com.example.cloudfour.storeservice.domain.menu.repository.MenuRepository;
import com.example.cloudfour.storeservice.domain.menu.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/stocks")
public class InternalStockController {
    private final StockRepository query;
    private final MenuRepository menuRepository;

    @GetMapping("/{menuId}")
    public StockResponseDTO getMenuDetail ( @PathVariable("menuId") UUID menuId ) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(()->new MenuException(MenuErrorCode.NOT_FOUND));
        UUID stockId = menu.getStock().getId();
        Stock stock = query.findByIdWithOptimisticLock(stockId).orElseThrow(()->new StockException(StockErrorCode.NOT_FOUND));
        return StockResponseDTO.builder().quantity(stock.getQuantity()).build();
    }
}
