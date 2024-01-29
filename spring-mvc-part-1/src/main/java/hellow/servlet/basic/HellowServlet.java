package hellow.servlet.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// name은 servlet 이름, urlPatterns 는 url 경로
@WebServlet(name = "hellowServlet", urlPatterns = "/hellow")
public class HellowServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("HellowServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username => " + username);

        // Response header
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        // Response body
        response.getWriter().write("hellow " + username);
    }
}
