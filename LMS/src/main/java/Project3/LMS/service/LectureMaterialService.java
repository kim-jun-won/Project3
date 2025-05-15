package Project3.LMS.service;

import Project3.LMS.domain.LectureMaterial;
import Project3.LMS.repostiory.LectureMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureMaterialService {

    private final LectureMaterialRepository materialRepository;

    public LectureMaterial saveMaterial(LectureMaterial material) {
        return materialRepository.save(material);
    }

    public List<LectureMaterial> getMaterialsByCourseId(Long courseId) {
        return materialRepository.findByCourseId(courseId);
    }

    public LectureMaterial getMaterial(Long id) {
        return materialRepository.findById(id).orElse(null);
    }

    public void incrementViewCount(LectureMaterial material) {
        material.incrementViewCount();
        materialRepository.save(material); // 저장
    }

    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }

}
