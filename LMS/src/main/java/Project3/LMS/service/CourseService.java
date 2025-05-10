<<<<<<< Updated upstream
﻿package Project3.LMS.service;
=======
package Project3.LMS.service;
>>>>>>> Stashed changes

import Project3.LMS.domain.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Project3.LMS.repository.CourseRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    CourseRepository courseRepository;

    //==강의 등록==//
    @Transactional
    public Long registerCourse(Course course) {

        validateDuplicateCourse(course);
        courseRepository.save(course);
        return course.getId();
    }



    private void validateDuplicateCourse(Course course) {
        List <Course> findCourses = courseRepository.findByName(course.getCourseName());

        if(!findCourses.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 강의명입니다.");
        }

    }

    //==강의 삭제==//
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findOne(courseId);
        courseRepository.delete(course);
    }
}
