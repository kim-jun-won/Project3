// ✅ 사이드바 열고 닫기
function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    if (sidebar) {
        sidebar.classList.toggle("active");
    }
}

// ✅ DOM 로드 이후 실행
document.addEventListener("DOMContentLoaded", function () {
    // 1. 시간표 셀 색상 자동 배정
    const cells = document.querySelectorAll(".timetable-cell");
    const colorMap = {};
    let colorIndex = 0;
    const totalColors = 8;

    cells.forEach(cell => {
        const text = cell.innerText.trim();
        if (text !== '') {
            const courseName = text.split("(")[0].trim();
            if (!colorMap[courseName]) {
                colorMap[courseName] = colorIndex % totalColors;
                colorIndex++;
            }
            const assignedColor = colorMap[courseName];
            cell.classList.add(`color-${assignedColor}`);
        }
    });

    // 2. 역할 정보 읽기
    const role = document.getElementById("userRole")?.value || "guest";

    const accessMap = {
        student: [
            "/timetable",
            "/grades",
            "/notice/student",
            "/syllabus/student",
            "/content",
            "/mypage",
            "/enroll",
            "/enroll/gradeCheck"
        ],
        professor: [
            "/enroll/attendance",
            "/lecture-upload",
            "/notice/professor",
            "/syllabus/professor"
        ],
        admin: [
            "/enroll-schedule",
            "/grade-period",
            "/notice/admin",
            "/professors",
            "/students",
            "/course-manage"
        ]
    };

    // 3. 링크 클릭 시 권한 검사
    const sidebarLinks = document.querySelectorAll(".sidebar a");

    sidebarLinks.forEach(link => {
        const href = link.getAttribute("href");
        link.setAttribute("data-href", href);
        link.setAttribute("href", "javascript:void(0);");

        link.addEventListener("click", function () {
            const dest = link.getAttribute("data-href").replace(/\/+$/, "");
            const allowedPaths = accessMap[role] || [];

            const hasAccess = allowedPaths.some(path => {
                if (path === "/enroll") {
                    return dest === "/enroll";
                } else {
                    return dest === path || dest.startsWith(path + "/");
                }
            });

            if (!hasAccess) {
                alert("권한이 없습니다.");
                window.location.href = "/home";
            } else {
                window.location.href = dest;
            }
        });
    });
});
