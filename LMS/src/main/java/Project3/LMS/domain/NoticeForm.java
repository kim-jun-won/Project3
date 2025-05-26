package Project3.LMS.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeForm {
    private Long noticeId;
    private Long courseId;
    private String title;
    private String content;
    private boolean fixed;
    private String fileName;
}