package Project3.LMS.controller;

import Project3.LMS.domain.*;
import Project3.LMS.dto.UserDTO;
import Project3.LMS.exception.DuplicateExistEmail;
import Project3.LMS.exception.DuplicateUserException;
import Project3.LMS.repostiory.ProfessorRepository;
import Project3.LMS.repostiory.StudentRepository;
import Project3.LMS.service.AdminService;
import Project3.LMS.service.ProfessorService;
import Project3.LMS.service.StudentService;
import Project3.LMS.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final AdminService adminService;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;


    @GetMapping("/")
    public String showStartPage() {
        return "login";
    }

    /**
     login 홈페이지 get요청
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (logout != null) {
            model.addAttribute("logoutSuccess", "로그아웃이 완료되었습니다.");
        }
        return "login"; // login.html 렌더링
    }

    /**
     *login post요청 -> 로그인을 성공한다면 home으로
     *                  아니라면 login화면으로 redircet
     */
    @PostMapping("/login")
    public String login(@RequestParam("uid") String uid,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        //학번을 db에서 조회
        User user = userService.findByuid(uid);

        //학번이 존재하지 않을 경우
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginfail", "존재하지 않는 학번입니다.");
            return "redirect:/login";
        }

        //비밀번호가 일치하지 않을 경우
        if(!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("passwordfail", "비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }

        //유저 타입이 학생일 경우
        if(user.getUserType()== UserType.STUDENT) {
            Student loginStudent = studentService.findBySid(user.getUid());
            //세션에 학생 저장
            session.setAttribute("loginMember", loginStudent);
            return "redirect:/home";
        }

        if(user.getUserType()== UserType.PROFESSOR) {
            Professor loginProfessor=professorService.findByPid(user.getUid());
            //세션에 교수 저장
            session.setAttribute("loginMember", loginProfessor);
            return "redirect:/home";
        }

        if(user.getUserType()== UserType.ADMIN) {
            Admin loginAdmin= adminService.findByAid(user.getUid());
            //세션에 관리자 저장
            session.setAttribute("loginMember", loginAdmin);
            return "redirect:/home";
        }


        redirectAttributes.addFlashAttribute("error","예기치 못한 오류가 발생했습니다.");
        return "redirect:/login";
    }

    /**
     *     회원가입 화면 get 요청
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    /**
     * 회원가입 화면 post 요청 -> 회원가입 과정
     * @ModelAttribute -> 회원가입 폼에서 받은 정보를 객체로 자동 바인딩.
     */
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid UserDTO userDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.join(userDTO);
            redirectAttributes.addFlashAttribute("successRegister", "회원 가입이 완료되었습니다.");
            return "redirect:/login";
        }
        catch (DuplicateUserException | DuplicateExistEmail e){
            redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
            return "redirect:/register";
        }

    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpSession session,RedirectAttributes redirectAttributes) {
        session.invalidate();  // 세션을 무효화하여 로그아웃 처리
        return "redirect:/login?logout=true";    // 로그인 페이지로 리디렉션하면서 logout=true 전달
    }

    /**
     개인정보 조회
     */
    @GetMapping("/mypage")
    public String mypageForm(Model model, HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");

        UserDTO userDTO = new UserDTO();

        if (loginMember instanceof Student) {
            // ✅ 변경: user가 붙은 상태로 조회
            Student student = studentService.findBySidWithUser(((Student) loginMember).getSid());
            userDTO.setUid(student.getSid());
            userDTO.setName(student.getName());
            userDTO.setEmail(student.getEmail());
            userDTO.setDepartment(student.getDepartment());
            userDTO.setPassword(student.getPassword());
            userDTO.setPhoneNumber(student.getPhoneNumber());
            if (student.getUser() != null) {
                userDTO.setUserType(student.getUser().getUserType());
            }

        } else if (loginMember instanceof Professor) {
            Professor professor = professorService.findByPidWithUser(((Professor) loginMember).getPid());

            userDTO.setUid(professor.getPid());
            userDTO.setName(professor.getName());
            userDTO.setEmail(professor.getEmail());
            userDTO.setDepartment(professor.getDepartment());
            userDTO.setPassword(professor.getPassword());
            userDTO.setPhoneNumber(professor.getPhoneNumber());
            if (professor.getUser() != null) {
                userDTO.setUserType(professor.getUser().getUserType());
            }
        }

        model.addAttribute("userDTO", userDTO);
        return "/mypage/mypage";

    }

    @GetMapping("/mypage/edit")
    public String editMypageForm(Model model, HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");
        UserDTO userDTO = new UserDTO();

        if (loginMember instanceof Student) {
            Student student = studentService.findBySidWithUser(((Student) loginMember).getSid());

            userDTO.setUid(student.getSid());
            userDTO.setName(student.getName());
            userDTO.setEmail(student.getEmail());
            userDTO.setDepartment(student.getDepartment());
            userDTO.setPassword(student.getPassword());
            userDTO.setPhoneNumber(student.getPhoneNumber());
            if (student.getUser() != null) {
                userDTO.setUserType(student.getUser().getUserType());
            }

        } else if (loginMember instanceof Professor) {
            Professor professor = professorService.findByPidWithUser(((Professor) loginMember).getPid());

            userDTO.setUid(professor.getPid());
            userDTO.setName(professor.getName());
            userDTO.setEmail(professor.getEmail());
            userDTO.setDepartment(professor.getDepartment());
            userDTO.setPassword(professor.getPassword());
            userDTO.setPhoneNumber(professor.getPhoneNumber());
            if (professor.getUser() != null) {
                userDTO.setUserType(professor.getUser().getUserType());
            }
        }

        model.addAttribute("userDTO", userDTO);
        return "/mypage/mypageEdit";
    }

    @PostMapping("/mypage/edit")
    public String editMypage(@ModelAttribute UserDTO userDTO,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Object loginMember = session.getAttribute("loginMember");
        try {
            if (loginMember instanceof Student) {
                Student student = studentService.findBySidWithUser(userDTO.getUid());

                student.setEmail(userDTO.getEmail());
                student.setDepartment(userDTO.getDepartment());
                student.setPassword(userDTO.getPassword());
                student.setPhoneNumber(userDTO.getPhoneNumber());

                studentRepository.save(student); // 저장 메서드 필요

            } else if (loginMember instanceof Professor) {
                Professor professor = professorService.findByPidWithUser(userDTO.getUid());

                professor.setEmail(userDTO.getEmail());
                professor.setDepartment(userDTO.getDepartment());
                professor.setPassword(userDTO.getPassword());
                professor.setPhoneNumber(userDTO.getPhoneNumber());

                professorRepository.save(professor); // 저장 메서드 필요
            }

            redirectAttributes.addFlashAttribute("success", "개인정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "수정 중 오류가 발생했습니다.");
        }

        return "redirect:/mypage";
    }

    @GetMapping("/mypage/verify-password")
    public String showPasswordVerifyForm() {
        return "/mypage/verifyPw"; //
    }

    @PostMapping("/mypage/verify-password")
    public String verifyPassword(@RequestParam("password") String password,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Object loginMember = session.getAttribute("loginMember");

        if (loginMember instanceof Student student) {
            if (!student.getPassword().equals(password)) {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/mypage/verify-password";
            }
        } else if (loginMember instanceof Professor professor) {
            if (!professor.getPassword().equals(password)) {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/mypage/verify-password";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "유효하지 않은 사용자입니다.");
            return "redirect:/mypage/verify-password";
        }

        return "redirect:/mypage/edit";
    }

}
