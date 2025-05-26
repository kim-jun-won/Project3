package Project3.LMS.service;

import Project3.LMS.domain.Admin;
import Project3.LMS.domain.Professor;
import Project3.LMS.repostiory.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;

    /**
     * 학번으로 교수 한명을 조회하는 로직
     */
    public Admin findByAid(String aid) {
        return adminRepository.findByaid(aid).orElse(null);
    }
}
