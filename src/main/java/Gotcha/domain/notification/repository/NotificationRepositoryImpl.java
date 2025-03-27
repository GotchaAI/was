package Gotcha.domain.notification.repository;

import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.entity.Notification;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Gotcha.domain.notification.entity.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notification> getNotifications(String keyword, Pageable pageable, NotificationSortType sort) {
        BooleanExpression condition = (keyword != null && !keyword.isBlank()) ?
                notification.title.containsIgnoreCase(keyword) : null;

        List<Notification> notifications = queryFactory.
                selectFrom(notification).
                where(condition).
                orderBy(sort.getOrder()).
                offset(pageable.getOffset()).
                limit(pageable.getPageSize()).
                fetch();

        Long total = Optional.ofNullable(
                queryFactory.
                        select(notification.count()).
                        from(notification).
                        where(condition).
                        fetchOne()
        ).orElse(0L);

        return new PageImpl<>(notifications, pageable, total);
    }


}
