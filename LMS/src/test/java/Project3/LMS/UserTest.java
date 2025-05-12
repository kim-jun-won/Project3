package Project3.LMS;

import Project3.LMS.domain.Student;
import Project3.LMS.domain.User;
import Project3.LMS.domain.UserType;
import Project3.LMS.dto.UserDTO;
import Project3.LMS.service.StudentService;
import Project3.LMS.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserTest {
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;


    /**
     * 회원가입 테스트
     */
    @Test
    public void userJoinTest(){
        //given
        UserDTO user = new UserDTO();
        user.setName("홍왕기");
        user.setPassword("123456");
        user.setDepartment("컴퓨터 정보 공학부");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("abc@gmail.com");
        user.setUserType(UserType.STUDENT);
        user.setUid("2020202060");

        //when
        userService.join(user);
        Student student = studentService.findByStudent(user.getUid());

        //then
        assertThat(student.getSid()).isEqualTo(user.getUid());
    }
}
