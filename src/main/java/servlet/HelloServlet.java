package servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "MyServlet", 
        urlPatterns = {"/hello/*"}
    )
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HashMap<String, String> thing = new HashMap<String, String>();
        for (int i = 0; i < thing.size(); i++) {

        }

        String requestPath = req.getPathInfo();
        String response = "hello heroku: " + requestPath;

        ServletOutputStream out = resp.getOutputStream();
        out.write(response.getBytes());
        out.flush();
        out.close();
    }
    
}
