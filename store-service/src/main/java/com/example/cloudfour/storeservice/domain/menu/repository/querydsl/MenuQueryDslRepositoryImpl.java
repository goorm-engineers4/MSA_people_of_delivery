package com.example.cloudfour.storeservice.domain.menu.repository.querydsl;

import com.example.cloudfour.storeservice.domain.menu.entity.Menu;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.cloudfour.storeservice.domain.menu.entity.QMenu.menu;
import static com.example.cloudfour.storeservice.domain.menu.entity.QMenuCategory.menuCategory;
import static com.example.cloudfour.storeservice.domain.store.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class MenuQueryDslRepositoryImpl implements MenuQueryDslRepository{
    private final JPAQueryFactory query;

    @Override
    public List<Menu> findByStoreIdAndDeletedFalseOrderByCreatedAtDesc(UUID storeId) {
        return query.selectFrom(menu).join(menu.store, store).fetchJoin()
                .where(store.id.eq(storeId), store.isDeleted.eq(false))
                .orderBy(menu.createdAt.desc()).fetch();
    }

    @Override
    public Slice<Menu> findByStoreIdAndDeletedFalseAndCreatedAtBefore(UUID storeId, LocalDateTime cursor, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Menu> menus = query.selectFrom(menu).join(menu.store,store).fetchJoin()
                .where(store.id.eq(storeId), store.isDeleted.eq(false)
                ,store.createdAt.lt(cursor)).orderBy(store.createdAt.desc()).limit(pageSize+1).fetch();

        boolean hasNext = menus.size() > pageSize;
        if(hasNext){
            menus.remove(pageSize);
        }
        return new SliceImpl<>(menus,pageable,hasNext);
    }

    @Override
    public Slice<Menu> findByStoreIdAndMenuCategoryIdAndDeletedFalseAndCreatedAtBefore(UUID storeId, UUID menuCategoryId, LocalDateTime cursor, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        List<Menu> menus = query.selectFrom(menu).join(menu.store,store).join(menu.menuCategory,menuCategory).fetchJoin()
                .where(store.id.eq(storeId), store.isDeleted.eq(false), menuCategory.id.eq(menuCategoryId)
                        ,store.createdAt.lt(cursor)).orderBy(store.createdAt.desc()).limit(pageSize+1).fetch();

        boolean hasNext = menus.size() > pageSize;
        if(hasNext){
            menus.remove(pageSize);
        }
        return new SliceImpl<>(menus,pageable,hasNext);
    }

    @Override
    public boolean existsByNameAndStoreId(String name, UUID storeId) {
        return query.selectFrom(menu).join(menu.store, store).fetchJoin()
                .where(menu.name.eq(name), store.id.eq(storeId)).fetchFirst() != null;
    }
}
//    @Query("SELECT m FROM Menu m " +
//            "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
//            "WHERE m.store.isDeleted = false AND m.store.userIsDeleted = false " +
//            "GROUP BY m.id " +
//            "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
//    List<Menu> findTopMenusByOrderCount(Pageable pageable);


//    @Query("SELECT m FROM Menu m " +
//            "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
//            "LEFT JOIN oi.order o " +
//            "WHERE m.store.isDeleted = false AND m.store.userIsDeleted = false " +
//            "AND o.createdAt BETWEEN :startTime AND :endTime " +
//            "GROUP BY m.id " +
//            "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
//    List<Menu> findTopMenusByTimeRange(@Param("startTime") LocalDateTime startTime,
//                                       @Param("endTime") LocalDateTime endTime,
//                                       Pageable pageable);
//
//    @Query("SELECT m FROM Menu m " +
//            "LEFT JOIN OrderItem oi ON m.id = oi.menu.id " +
//            "WHERE m.store.isDeleted = false AND m.store.userIsDeleted = false " +
//            "AND m.store.region.siDo = :si " +
//            "AND m.store.region.siGunGu = :gu " +
//            "GROUP BY m.id " +
//            "ORDER BY COUNT(oi.id) DESC, m.createdAt DESC")
//    List<Menu> findTopMenusByRegion(@Param("si") String si, @Param("gu") String gu, Pageable pageable);