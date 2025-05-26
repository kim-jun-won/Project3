package Project3.LMS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GradeInputDTO {
    private Long studentId;
    private String studentName;
    private String sid; // 학번
    private String department; // 학과
    private String grade;

}
