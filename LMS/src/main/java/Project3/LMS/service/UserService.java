package Project3.LMS.service;

import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.domain.User;
import Project3.LMS.domain.UserType;
import Project3.LMS.dto.UserDTO;
import Project3.LMS.exception.DuplicateExistEmail;
import Project3.LMS.exception.DuplicateUserException;
import Project3.LMS.exception.UserNotFoundException;
import Project3.LMS.repostiory.ProfessorRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    /**
     * 회원가입 로직
     */
    public Long join(UserDTO userDTO){

        //학번이 이미 존재하는지 검사
        duplicateUser(userDTO.getUid());
        //이메일이 이미 존재하는지 검사
        duplicateEmail(userDTO.getEmail());

        //사용자 입력으로 받은 userDTO 정보를 user에 복사
        User user = new User();
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setDepartment(userDTO.getDepartment());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setUid(userDTO.getUid());
        user.setUserType(userDTO.getUserType());
        userRepository.save(user);

        //회원가입에서 선택한 신분이 학생일 경우
        if(userDTO.getUserType()== UserType.STUDENT){
            //학생 객체 생성해서 정보 저장
            Student student = new Student();
            student.setName(userDTO.getName());
            student.setPassword(userDTO.getPassword());
            student.setDepartment(userDTO.getDepartment());
            student.setEmail(userDTO.getEmail());
            student.setPhoneNumber(userDTO.getPhoneNumber());
            student.setSid(userDTO.getUid());
            student.setUser(user);
            studentRepository.save(student);
        }

        //회원가입에서 선택한 신분이 교수일 경우
        if(userDTO.getUserType()== UserType.PROFESSOR){
            Professor professor = new Professor();
            professor.setName(userDTO.getName());
            professor.setPassword(userDTO.getPassword());
            professor.setDepartment(userDTO.getDepartment());
            professor.setEmail(userDTO.getEmail());
            professor.setPhoneNumber(userDTO.getPhoneNumber());
            professor.setPid(userDTO.getUid());
            professor.setUser(user);
            professorRepository.save(professor);
        }


        return user.getId();
    }

    /**
     * 회원가입시 학번 중복을 찾는 로직
     */
    public void duplicateUser(String id) {
        User user = userRepository.findByUid(id).orElse(null);

        if (user != null) {
            throw new DuplicateUserException("이미 존재하는 학번입니다.");
        }

    }

    /**
     * 회원가입시 이메일 중복을 찾는 로직
     */
    public void duplicateEmail(String email) {
        User user=userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new DuplicateExistEmail("이미 존재하는 이메일입니다.");
        }
    }

    /**
     * User 한명을 조회하는 로직
     */
    public User findByUser(String id) {
        User user = userRepository.findByUid(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User를 조회할 수 없습니다.");
        }
        return user;
    }

    /**
     * 학번으로 User을 조회하는 로직
     */
    public User findByuid(String uid) {
        return userRepository.findByUid(uid).orElse(null);
    }


    /**
     * User 전체 조회하는 로직
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }




}
