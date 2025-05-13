package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.OnlineLecture;
import Project3.LMS.dto.OnlineLectureDTO;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.OnlineLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OnlineLectureService {
    private final OnlineLectureRepository onlineLectureRepository;
    private final CourseRepository courseRepository;


    @Transactional
    public Long addOnlineLecture(OnlineLectureDTO onlineLectureDTO, Long courseId) {

        Course course = courseRepository.findOne(courseId);

        OnlineLecture onlineLecture = OnlineLecture.createOnlineLecture(
                onlineLectureDTO.getTitle(),
                onlineLectureDTO.getUrl(),
                onlineLectureDTO.getDeadline(),
                course
        );

        course.addOnlineLecture(onlineLecture);

        onlineLectureRepository.save(onlineLecture);

        return onlineLecture.getId();
    }
}
