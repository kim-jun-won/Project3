package Project3.LMS;

import lombok.Data;

@Data
public class CourseForm {
    private String courseName;
    private Long professorId;
    private int credits;
    private String day;
    private int time;

}
