package Project3.LMS.service;

import Project3.LMS.domain.Course;
import Project3.LMS.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
public class CourseServiceTest {

    @Autowired CourseService courseService;
    @Autowired CourseRepository courseRepository;

    @Test
    public void 강의등록() throws Exception{
        //given
        Course course = new Course();
        course.setCourseName("운영체제");

        //when
        Long saveId = courseService.registerCourse(course);

        //then
        assertEquals(saveId, course.getId());
    }

}