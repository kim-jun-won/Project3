package Project3.LMS.dto;

import Project3.LMS.domain.UserType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

/**
 * 엔티티를 외부에 노출시키지 않기 위해 User DTO 생성
 */
@Getter @Setter
public class UserDTO {

    private String uid; //학번
    private String name;
    private String email;
    private String department;
    private String password;
    private String phoneNumber;

    private UserType userType;
}
