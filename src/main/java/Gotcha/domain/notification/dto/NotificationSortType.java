package Gotcha.domain.notification.dto;

import lombok.Getter;
import org.springframework.data.domain.Sort;
import com.querydsl.core.types.OrderSpecifier;

import static Gotcha.domain.notification.entity.QNotification.notification;

public enum NotificationSortType {
    DATE_DESC("createdAt", Sort.Direction.DESC, notification.createdAt.desc()),
    DATE_ASC("createdAt", Sort.Direction.ASC, notification.createdAt.asc());

    private String type;
    private Sort.Direction direction;

    @Getter
    private OrderSpecifier<?> order;

    NotificationSortType(String type, Sort.Direction direction, OrderSpecifier<?> order){
        this.type = type;
        this.direction = direction;
        this.order = order;
    }

    public Sort getSort(){
        return Sort.by(direction, type);
    }
}
