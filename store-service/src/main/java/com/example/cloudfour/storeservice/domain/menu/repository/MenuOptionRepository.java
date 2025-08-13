package com.example.cloudfour.storeservice.domain.menu.repository;

import com.example.cloudfour.storeservice.domain.menu.entity.MenuOption;
import com.example.cloudfour.storeservice.domain.menu.repository.querydsl.MenuOptionQueryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuOption, UUID> , MenuOptionQueryDslRepository {

}