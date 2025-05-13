package Project3.LMS.service;

import Project3.LMS.domain.Professor;
import Project3.LMS.domain.User;
import Project3.LMS.exception.UserNotFoundException;
import Project3.LMS.repostiory.ProfessorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    /**
     * 교수 한명을 조회하는 로직
     */
    public Professor findById(String id) {
        Professor professor = professorRepository.findByPid(id).orElse(null);
        if (professor == null) {
            throw new UserNotFoundException("교수를 조회할 수 없습니다.");
        }

        return professor;
    }

    /**
     * 학번으로 교수 한명을 조회하는 로직
     */
    public Professor findByPid(String pid) {
        return professorRepository.findByPid(pid).orElse(null);
    }



    /**
     * 교수 전체를 조회하는 로작
     */
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }
}