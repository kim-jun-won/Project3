package Project3.LMS.service;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.CourseRepository;
import Project3.LMS.repostiory.EnrollmentRepository;
import Project3.LMS.repostiory.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringTokenizer;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    /**
     * 교수 : 공지사항 등록
     * */
    public void createByProfessor(Long courseId, Professor professor, String title, String content){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new IllegalArgumentException("Course not found"));

        Notice notice = new Notice();
        notice.setCourse(course);
        notice.setProfessor(professor);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setWriterType(NoticeWriterType.PROFESSOR);
        notice.setDate(LocalDateTime.now());

        noticeRepository.save(notice);
    }

    /**
     * 교수 : 공지사항 수정
     * */
    public void updateNotice(Long noticeId, Professor professor, String title, String content) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (!notice.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("본인이 작성한 공지만 수정할 수 있습니다.");
        }

        notice.setTitle(title);
        notice.setContent(content);
    }

    /**
     * 교수 : 공지사항 삭제
     * */
    public void deleteNotice(Long noticeId, Professor professor) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (!notice.getProfessor().getId().equals(professor.getId())) {
            throw new IllegalArgumentException("본인이 작성한 공지만 삭제할 수 있습니다.");
        }

        noticeRepository.delete(notice);
    }

    /**
     * 교수 : 공지사항 조회
     * */
    public List<Notice> getProfessorNotices(List<Course> courses) {
        return noticeRepository.findByCourseIn(courses);
    }

    /**
     * 학생 : 수강과목 공지사항 조회
     * */
    public List<Notice> findStudentNotices(Student student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());
        List<Course> courses = enrollments.stream().map(Enrollment::getCourse).toList();
        return noticeRepository.findByCourseIn(courses);
    }

    /**
     * 관리자 : 공지사항 목록
     * */
    public List<Notice> findAdminNotices() {
        return noticeRepository.findByWriterType(NoticeWriterType.ADMIN);
    }

    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(()->new IllegalArgumentException("해당공지사항이 존재하지 않습니다."));
    }

    public List<Notice> findNoticesByCourse(Course course) {
        return noticeRepository.findByCourse(course);
    }

    /**
     * 관리자 : 공지사항 등록
     */
    public void createByAdmin(Admin admin, String title, String content) {
        Notice notice = new Notice();
        notice.setAdmin(admin);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setWriterType(NoticeWriterType.ADMIN);
        notice.setDate(LocalDateTime.now());

        noticeRepository.save(notice);
    }

    /**
     * 관리자 : 공지사항 수정
     */
    public void updateNoticeByAdmin(Long noticeId, Admin admin, String title, String content) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (!notice.getAdmin().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("본인이 작성한 공지만 수정할 수 있습니다.");
        }

        notice.setTitle(title);
        notice.setContent(content);
    }

    /**
     * 관리자 : 공지사항 삭제
     */
    public void deleteNoticeByAdmin(Long noticeId, Admin admin) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 존재하지 않습니다."));

        if (!notice.getAdmin().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("본인이 작성한 공지만 삭제할 수 있습니다.");
        }

        noticeRepository.delete(notice);
    }

}
