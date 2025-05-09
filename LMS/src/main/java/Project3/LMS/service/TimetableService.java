package Project3.LMS.service;

import Project3.LMS.domain.Student;
import Project3.LMS.domain.Timetable;
import Project3.LMS.repostiory.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;

    public List<Timetable> getStudentTimetable(Student student){
        return timetableRepository.findByStudent(student);
    }
}
