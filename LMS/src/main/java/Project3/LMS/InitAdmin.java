package Project3.LMS;

import Project3.LMS.domain.Admin;
import Project3.LMS.domain.User;
import Project3.LMS.domain.UserType;
import Project3.LMS.repostiory.AdminRepository;
import Project3.LMS.repostiory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


/**
 * 시작시 관리자 한명 데이터 베이스에 설정.
 */
@Configuration
@RequiredArgsConstructor
public class InitAdmin {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

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
        };
    }
}
