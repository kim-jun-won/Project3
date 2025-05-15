package Project3.LMS;

import Project3.LMS.domain.*;
import Project3.LMS.repostiory.AdminRepository;
import Project3.LMS.repostiory.NoticeRepository;
import Project3.LMS.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;


/**
 * 시작시 관리자 한명 데이터 베이스에 설정.
 */
@Configuration
@RequiredArgsConstructor
public class InitAdmin {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final NoticeRepository noticeRepository;

    @Bean
    @Transactional
    public CommandLineRunner initAdminData() {
        return args -> {
            // 이미 관리자 유저가 존재하면 생성하지 않음
            if (userRepository.existsByEmail("admin@kw.ac.kr")) {
                return;
            }

            // 1. User 생성
            User adminUser = new User();
            adminUser.setUid("admin1");
            adminUser.setName("관리자");
            adminUser.setEmail("admin@kw.ac.kr");
            adminUser.setDepartment("정보지원처");
            adminUser.setPassword("1234");
            adminUser.setPhoneNumber("010-0000-0000");
            adminUser.setUserType(UserType.ADMIN);


            // 2. Admin 생성
            Admin admin = new Admin();
            admin.setAid(adminUser.getUid());
            admin.setName(adminUser.getName());
            admin.setEmail(adminUser.getEmail());
            admin.setDepartment(adminUser.getDepartment());
            admin.setPassword(adminUser.getPassword());
            admin.setPhoneNumber(adminUser.getPhoneNumber());
            admin.setUser(adminUser);
            adminRepository.save(admin);

            // 3. 공지사항 데이터
            String[][] adminNotices = {
                    {
                            "[필독] 2025학년도 1학기 개강일 및 수강 정정 안내",
                            "2025학년도 1학기 개강일은 **3월 4일(화)**입니다.\n" +
                                    "- 수강 정정 기간: 3월 4일(화) ~ 3월 10일(월)\n" +
                                    "- 정정은 KLAS → 수강신청 메뉴에서 가능합니다.\n" +
                                    "- 미정정 시 학점 인정 불가하오니 반드시 확인 바랍니다."
                    },
                    {
                            "[시스템 점검 안내] LMS 서비스 일시 중단",
                            "LMS 안정화 작업을 위한 시스템 점검이 진행됩니다.\n" +
                                    "- 점검일시: 2025년 3월 9일(일) 00:00 ~ 06:00\n" +
                                    "- 점검 중 서비스 접속 및 강의 수강 불가\n" +
                                    "- 불편을 드려 죄송하며, 양해 부탁드립니다."
                    },
                    {
                            "[보안 경고] 피싱 메일 주의",
                            "최근 '수강신청 오류 수정'을 사칭한 피싱 메일이 유포되고 있습니다.\n" +
                                    "- 출처가 불명확한 메일은 열람하지 마시고 즉시 삭제하세요.\n" +
                                    "- 학교 공식 도메인은 @kw.ac.kr입니다.\n" +
                                    "- 의심 메일 발견 시 it@kw.ac.kr로 신고 바랍니다."
                    },
                    {
                            "[신기능 안내] 강의자료 다크모드 기능 오픈!",
                            "학생들의 눈 건강 보호와 집중력 향상을 위해 다크모드 기능이 추가되었습니다.\n" +
                                    "- 설정 방법: KLAS → 내 정보 → 환경설정 → 테마 설정\n" +
                                    "- 밝은 테마, 어두운 테마 중 선택 가능"
                    },
                    {
                            "[공지] 2025년 1학기 중간고사 기간 안내",
                            "중간고사는 **4월 21일(월) ~ 4월 26일(토)** 진행 예정입니다.\n" +
                                    "- 시간표 및 시험실 정보는 추후 공지됩니다.\n" +
                                    "- 개인별 시험 시간은 KLAS → 시험 일정에서 확인 바랍니다."
                    }
            };

            for (String[] noticeData : adminNotices) {
                Notice notice = new Notice();
                notice.setWriterType(NoticeWriterType.ADMIN);
                notice.setAdmin(admin);
                notice.setTitle(noticeData[0]);
                notice.setContent(noticeData[1]);
                notice.setDate(LocalDateTime.now().minusDays(new Random().nextInt(7)));
                noticeRepository.save(notice);
            }
        };
    }
}
