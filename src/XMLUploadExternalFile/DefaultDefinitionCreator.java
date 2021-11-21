package XMLUploadExternalFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DefaultDefinitionCreator extends HttpServlet {
    static InputStream targetStream;
    public DefaultDefinitionCreator()
    {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        targetStream= new ByteArrayInputStream(request.getParameter("n1").getBytes());
    }

    public InputStream getDefinition() {
        if(targetStream!=null){
            return targetStream;
        }
        return null;
    }

    }