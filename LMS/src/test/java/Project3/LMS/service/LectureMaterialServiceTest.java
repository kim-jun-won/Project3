package Project3.LMS.service;

import Project3.LMS.domain.LectureMaterial;
import Project3.LMS.repostiory.LectureMaterialRepository;
import Project3.LMS.service.LectureMaterialService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LectureMaterialServiceTest {

    @Test
    void 자료저장_테스트() {
        // 준비
        LectureMaterialRepository repo = mock(LectureMaterialRepository.class);
        LectureMaterialService service = new LectureMaterialService(repo);

        LectureMaterial material = new LectureMaterial();
        material.setTitle("자료제목");

        when(repo.save(material)).thenReturn(material);

        // 실행
        LectureMaterial result = service.saveMaterial(material);

        // 검증
        assertThat(result.getTitle()).isEqualTo("자료제목");
        verify(repo, times(1)).save(material);
    }
}
