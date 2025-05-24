package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.LearningTalk;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import Project3.LMS.repostiory.LearningTalkRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LearningTalkService {

    private final LearningTalkRepository learningTalkRepository;

    public LearningTalkService(LearningTalkRepository learningTalkRepository) {
        this.learningTalkRepository = learningTalkRepository;
    }

    public LearningTalk post(String title, String content, Course course, Student student) {
        LearningTalk talk = new LearningTalk();
        talk.setTitle(title);
        talk.setContent(content);
        talk.setCourse(course);
        talk.setStudent(student);
        talk.setCreatedAt(LocalDateTime.now());
        return learningTalkRepository.save(talk);
    }

    public LearningTalk post(String title, String content, Course course, Professor professor) {
        LearningTalk talk = new LearningTalk();
        talk.setTitle(title);
        talk.setContent(content);
        talk.setCourse(course);
        talk.setProfessor(professor);
        talk.setCreatedAt(LocalDateTime.now());
        return learningTalkRepository.save(talk);
    }

    public List<LearningTalk> getAllByCourse(Course course) {
        return learningTalkRepository.findByCourseOrderByCreatedAtAsc(course);
    }

    public LearningTalk getById(Long id) {
        return learningTalkRepository.findById(id).orElse(null);
    }
    public void save(LearningTalk talk) {
        learningTalkRepository.save(talk);
    }
}

