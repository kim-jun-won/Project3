package Project3.LMS.service;

import Project3.LMS.domain.Student;
import Project3.LMS.exception.UserNotFoundException;
import Project3.LMS.repostiory.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    /**
     * student 한명을 조회하는 로직
     */
    public Student findByStudent(String id) {
        Student student = studentRepository.findBySid(id).orElse(null);
        if (student == null) {
            throw new UserNotFoundException("학생을 조회할 수 없습니다.");
        }
        return student;
    }

    /**
     * 학번으로 sutdent 한명을 조회하는 로직
     */
    public Student findBySid(String sid) {
        return studentRepository.findBySid(sid).orElse(null);
    }

    /**
     * student 전체를 조회하는 로직
     */
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

}
