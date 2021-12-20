package XMLUploadExternalFile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.net.URLEncoder.encode;

@WebServlet("/uploadexternal")
public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        String stencil = request.getParameter("stencil");
        String rules = request.getParameter("rules");
        String connector = request.getParameter("connector");
        String semantic = request.getParameter("semantic");



        if(stencil != null && rules != null && connector!=null){
            String js = URLEncoder.encode(stencil, StandardCharsets.UTF_8.toString());
            String jr = URLEncoder.encode(rules, StandardCharsets.UTF_8.toString());
            String jc = URLEncoder.encode(connector, StandardCharsets.UTF_8.toString());
            request.setAttribute("stencil",js);
            request.setAttribute("rules",jr);
            request.setAttribute("connector",jc);
            request.getRequestDispatcher("WEB-INF/jsp/uploadExternal.jsp").forward(request,response);
        }

        if(stencil != null && rules != null && connector!=null && semantic!= null){
            String js = URLEncoder.encode(stencil, StandardCharsets.UTF_8.toString());
            String jr = URLEncoder.encode(rules, StandardCharsets.UTF_8.toString());
            String jc = URLEncoder.encode(connector, StandardCharsets.UTF_8.toString());
            String jsem = URLEncoder.encode(semantic, StandardCharsets.UTF_8.toString());
            request.setAttribute("stencil",js);
            request.setAttribute("rules",jr);
            request.setAttribute("connector",jc);
            request.setAttribute("semantic",jsem);
            request.getRequestDispatcher("WEB-INF/jsp/uploadExternal.jsp").forward(request,response);
        }
    }

    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        request.getRequestDispatcher("index.html").forward(request,response);
    }
}
