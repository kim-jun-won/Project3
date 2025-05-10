package service;

import Project3.LMS.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CourseRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    CourseRepository courseRepository;

    //==강의 등록==//
    public Long registerCourse(Course course) {

        validateDuplicateCourse(course);
        courseRepository.save(course);
        return course.getId();
    }



    private void validateDuplicateCourse(Course course) {


    }
}
