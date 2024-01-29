package hellow.servlet.basic.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import hellow.servlet.basic.HellowData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: application/json
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");  // application/json 사용시 utf-8 생략가능

        HellowData hellowData = new HellowData();
        hellowData.setUsername("kimjihong");
        hellowData.setAge(20);

        // {"username" : "kimjihong", "age" : 20}
        String result = objectMapper.writeValueAsString(hellowData);
        response.getWriter().write(result);
    }
}
