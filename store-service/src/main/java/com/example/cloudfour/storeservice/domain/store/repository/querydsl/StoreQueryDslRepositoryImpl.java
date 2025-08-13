package com.example.cloudfour.storeservice.domain.store.repository.querydsl;

import com.example.cloudfour.storeservice.domain.store.entity.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.cloudfour.storeservice.domain.region.entity.QRegion.region;
import static com.example.cloudfour.storeservice.domain.store.entity.QStore.store;
import static com.example.cloudfour.storeservice.domain.store.entity.QStoreCategory.storeCategory;

@Repository
@RequiredArgsConstructor
public class StoreQueryDslRepositoryImpl implements StoreQueryDslRepository{

    private final JPAQueryFactory query;

    @Override
    public Optional<Store> findByIdAndIsDeletedFalse(UUID storeId) {
        return Optional.ofNullable(query.selectFrom(store)
                .where(store.id.eq(storeId), store.isDeleted.eq(false), store.userIsDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Slice<Store> findAllByCategoryAndCursor(UUID categoryId, LocalDateTime cursor, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<Store> stores = query.selectFrom(store)
                .where(store.isDeleted.eq(false), store.storeCategory.id.eq(categoryId)
                        , store.createdAt.lt(cursor)).orderBy(store.createdAt.desc()).limit(pageSize+1)
                .fetch();

        boolean hasNext = stores.size() > pageSize;
        if(hasNext){
            stores.remove(pageSize);
        }
        return new SliceImpl<>(stores,pageable,hasNext);
    }

    @Override
    public Slice<Store> findAllByKeyWordAndRegion(String keyword, LocalDateTime cursor, Pageable pageable
    ,String siDo, String siGunGu, String eupMyeongDong) {
        int pageSize = pageable.getPageSize();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(store.isDeleted.eq(false));
        builder.and(store.createdAt.lt(cursor));

        if (keyword != null && !keyword.isEmpty()) {
            builder.and(
                    store.name.containsIgnoreCase(keyword)
                            .or(storeCategory.category.containsIgnoreCase(keyword))
            );
        }

        BooleanBuilder regionBuilder = new BooleanBuilder();
        if (siDo != null) regionBuilder.or(region.siDo.eq(siDo));
        if (siGunGu != null) regionBuilder.or(region.siGunGu.eq(siGunGu));
        if (eupMyeongDong != null) regionBuilder.or(region.eupMyeonDong.eq(eupMyeongDong));

        builder.and(regionBuilder);

        List<Store> stores = query.selectFrom(store).join(store.region,region).join(store.storeCategory,storeCategory).fetchJoin()
                .where(builder).orderBy(store.createdAt.desc()).limit(pageSize+1).fetch();
        boolean hasNext = stores.size() > pageSize;
        if(hasNext){
            stores.remove(pageSize);
        }
        return new SliceImpl<>(stores,pageable,hasNext);
    }

    @Override
    public boolean existsByName(String name) {
        return query.selectFrom(store).where(store.name.eq(name)).fetchFirst() != null;
    }
}

