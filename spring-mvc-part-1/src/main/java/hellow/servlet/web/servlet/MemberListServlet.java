package hellow.servlet.web.servlet;

import hellow.servlet.domain.member.Member;
import hellow.servlet.domain.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("    <meta charset=\"utf-8\">");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("<a href=/index.html>메인</a>");
        writer.println("<table>");
        writer.println("    <thead>");
        writer.println("    <th>id</th>");
        writer.println("    <th>username</th>");
        writer.println("    <th>age</th>");
        writer.println("    </thead>\n");
        writer.println("    <tbody>\n");

        for (Member member: members) {
            writer.println("    <tr>\n");
            writer.println("        <td>" + member.getId() + "</td>\n");
            writer.println("        <td>" + member.getUsername() + "</td>\n");
            writer.println("        <td>" + member.getAge() + "</td>\n");
            writer.println("     </tr>\n");
        }

        writer.println("     </tbody>\n");
        writer.println("</table>\n");
        writer.println("</body>\n");
        writer.println("</html>");
    }
}
