package gotcha_domain.report;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserReportType {
    BAD_LANGUAGE("욕설"),
    HATE_SPEECH("혐오 발언"),
    INAPPROPRIATE_NAME("불쾌감을 주거나 부적절한 이름"),
    SPAM("도배"),
    OTHER("기타. 신고 기록을 참조하세요.");

    String reason;

    public String getReason() {
        return reason;
    }
}
