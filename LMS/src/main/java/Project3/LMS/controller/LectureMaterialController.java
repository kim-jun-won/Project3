package Project3.LMS.controller;

import Project3.LMS.domain.Course;
import Project3.LMS.domain.LectureMaterial;
import Project3.LMS.domain.Professor;
import Project3.LMS.service.CourseService;
import Project3.LMS.service.LectureMaterialService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/materials")
public class LectureMaterialController {

    private final LectureMaterialService materialService;
    private final CourseService courseService;

    @GetMapping
    public String listMaterials(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
        Course course = courseService.findbyCourseId(courseId);
        List<LectureMaterial> materials = materialService.getMaterialsByCourseId(courseId);

        Object loginUser = session.getAttribute("loginMember");
        String userRole;

        if (loginUser instanceof Professor) {
            userRole = "professor";
        } else {
            userRole = "student";
        }

//        System.out.println("/////////// userRole: " + userRole);

        model.addAttribute("course", course);
        model.addAttribute("materials", materials);
        model.addAttribute("userRole", userRole); // ★ 여기 들어가야 template 조건식 작동

        return "material/materialList";
    }


    @GetMapping("/new")
    public String newMaterialForm(@PathVariable("courseId") Long courseId, Model model, HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");

        if (!(loginMember instanceof Professor)) {
            return "accessDenied"; // 비교 문자열 대신 객체 타입 검사
        }

        model.addAttribute("courseId", courseId);
        return "material/newMaterialForm";
    }

    // 자료 업로드 처리
    @PostMapping("/new")
    public String uploadMaterial(
            @PathVariable("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) throws IOException {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Professor professor)) {
            return "accessDenied";
        }

        Course course = courseService.findbyCourseId(courseId);
        String uploadDirPath = "C:/uploads";
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadDir, filename);
        file.transferTo(destination);

        LectureMaterial material = LectureMaterial.createMaterial(title, filename, course, professor.getName());
        materialService.saveMaterial(material);

        return "redirect:/courses/" + courseId + "/materials";
    }

    @GetMapping("/{materialId}")
    public String viewMaterial(@PathVariable("courseId") Long courseId,
                               @PathVariable("materialId") Long materialId,
                               Model model,
                               HttpSession session) {
        LectureMaterial material = materialService.getMaterial(materialId);
        if (material != null) {
            materialService.incrementViewCount(material); // 조회수 증가
        }

        Object loginUser = session.getAttribute("loginMember");
        String userRole = (loginUser instanceof Professor) ? "professor" : "student";

        model.addAttribute("material", material);
        model.addAttribute("userRole", userRole); //

        return "material/materialDetail";
    }

    @PostMapping("/{materialId}/edit")
    public String updateMaterial(@PathVariable("courseId") Long courseId,
                                 @PathVariable("materialId") Long materialId,
                                 @RequestParam("title") String title,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 HttpSession session) throws IOException {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Professor)) {
            return "accessDenied";
        }

        LectureMaterial material = materialService.getMaterial(materialId);
        if (material == null) {
            return "notFound";
        }

        // 제목 수정
        material.setTitle(title);

        // 파일이 업로드된 경우 파일 교체
        if (file != null && !file.isEmpty()) {
            String uploadDirPath = "C:/uploads";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir, filename);
            file.transferTo(destination);

            material.setFilePath(filename);
        }

        materialService.saveMaterial(material); // 저장 (update 처리)

        return "redirect:/courses/" + courseId + "/materials/" + materialId;
    }



    // 삭제
    @PostMapping("/{materialId}/delete")
    public String deleteMaterial(@PathVariable("courseId") Long courseId, @PathVariable("materialId") Long materialId, HttpSession session) {
        if (!(session.getAttribute("loginMember") instanceof Professor)) {
            return "accessDenied";
        }

        materialService.deleteMaterial(materialId);
        return "redirect:/courses/" + courseId + "/materials";
    }
    //수정
    @GetMapping("/{materialId}/edit")
    public String editMaterialForm(@PathVariable("courseId") Long courseId,
                                   @PathVariable("materialId") Long materialId,
                                   Model model,
                                   HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");
        if (!(loginMember instanceof Professor)) {
            return "accessDenied";
        }

        LectureMaterial material = materialService.getMaterial(materialId);
        if (material == null) {
            return "notFound"; // 또는 에러 처리
        }

        model.addAttribute("courseId", courseId);
        model.addAttribute("material", material);

        return "material/editMaterialForm";
    }

}