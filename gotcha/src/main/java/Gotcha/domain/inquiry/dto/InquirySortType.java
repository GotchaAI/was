package Gotcha.domain.inquiry.dto;

import org.springframework.data.domain.Sort;

public enum InquirySortType {
    DATE_DESC("createdAt", Sort.Direction.DESC),
    DATE_ASC("createdAt", Sort.Direction.ASC);

    private final String type;
    private final Sort.Direction direction;

    InquirySortType(String type, Sort.Direction direction){
        this.type = type;
        this.direction = direction;
    }

    public Sort getSort(){
        return Sort.by(direction, type);
    }

}
