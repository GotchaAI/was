package Gotcha.domain.notification.dto;

import org.springframework.data.domain.Sort;

public enum NotificationSortType {
    DATE_DESC("createdAt", Sort.Direction.DESC),
    DATE_ASC("createdAt", Sort.Direction.ASC);

    private final String type;
    private final Sort.Direction direction;


    NotificationSortType(String type, Sort.Direction direction){
        this.type = type;
        this.direction = direction;
    }

    public Sort getSort(){
        return Sort.by(direction, type);
    }
}
