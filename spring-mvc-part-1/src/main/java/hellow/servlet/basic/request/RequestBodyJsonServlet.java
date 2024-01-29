package hellow.servlet.basic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import hellow.servlet.basic.HellowData;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    // Json 데이터 파싱을 위한 Jackson 라이브러리의 ObjectMapper class
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("message body => " + messageBody);

        HellowData hellowData = objectMapper.readValue(messageBody, HellowData.class);
        System.out.println(hellowData);

        System.out.println("hellowData.getAge => " + hellowData.getAge());
        System.out.println("hellowData.getUsername => " + hellowData.getUsername());

        response.getWriter().write("ok");
    }
}
