package Project3.LMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class OnlineLectureDTO {

    @NotBlank
    private String title;

    @NotNull
    private String url;

    @NotBlank
    private LocalDateTime deadline;
}
