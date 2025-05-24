package Project3.LMS.service;

import Project3.LMS.domain.Admin;
import Project3.LMS.repostiory.AdminRepository;
import Project3.LMS.service.AdminService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Test
    void 관리자_조회_성공() {
        // given
        AdminRepository repo = mock(AdminRepository.class);
        AdminService service = new AdminService(repo);

        Admin admin = new Admin();
        admin.setAid("admin01");

        when(repo.findByaid("admin01")).thenReturn(Optional.of(admin));

        // when
        Admin result = service.findByAid("admin01");

        // then
        assertThat(result.getAid()).isEqualTo("admin01");
        verify(repo, times(1)).findByaid("admin01");
    }

    @Test
    void 관리자_조회_실패_null반환() {
        // given
        AdminRepository repo = mock(AdminRepository.class);
        AdminService service = new AdminService(repo);

        when(repo.findByaid("wrong")).thenReturn(Optional.empty());

        // when
        Admin result = service.findByAid("wrong");

        // then
        assertThat(result).isNull();
        verify(repo).findByaid("wrong");
    }
}
