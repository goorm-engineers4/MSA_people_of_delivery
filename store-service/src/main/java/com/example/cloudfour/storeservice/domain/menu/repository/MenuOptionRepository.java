package com.example.cloudfour.storeservice.domain.menu.repository;

import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuOption, UUID> {

    @Query("select mo from MenuOption mo join fetch OrderItem oi on oi.menu.id = mo.menu.id where oi.order.id =:OrderId")
    List<MenuOption> findByOrderId(@Param("OrderId") UUID orderId);

    List<MenuOption> findByMenuIdOrderByAdditionalPrice(UUID menuId);

    boolean existsByMenuIdAndOptionName(UUID menuId, String optionName);

    List<MenuOption> findByMenuIdOrderById(UUID menuId);

    @Query("SELECT mo FROM MenuOption mo JOIN FETCH mo.menu WHERE mo.id = :optionId")
    java.util.Optional<MenuOption> findByIdWithMenu(@Param("optionId") UUID optionId);
}