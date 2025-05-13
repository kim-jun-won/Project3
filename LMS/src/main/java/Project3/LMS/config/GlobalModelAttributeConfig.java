package Project3.LMS.config;

import Project3.LMS.domain.Admin;
import Project3.LMS.domain.Professor;
import Project3.LMS.domain.Student;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeConfig {

    @ModelAttribute
    public void addUserRoleToModel(HttpSession session, Model model) {
        Object loginMember = session.getAttribute("loginMember");

        if (loginMember instanceof Student) {
            model.addAttribute("userRole", "student");
        } else if (loginMember instanceof Professor) {
            model.addAttribute("userRole", "professor");
        } else if (loginMember instanceof Admin) {
            model.addAttribute("userRole", "admin");
        }
    }
}
