package XMLUploadExternalFile;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultDefinitionCreator extends HttpServlet {
    static String path;
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
        path=getServletContext().getRealPath("/");
       if(request.getParameter("n1")!=null){
            File e = createStencil(request.getParameter("n1"));
           Path path = Paths.get("tempDefinition.xml");
           System.out.println(path.toAbsolutePath());
       }
    }

    public File createStencil(String xml){
        try {
            String f = getServletContext().getRealPath("/");
            File myObj = new File(getServletContext().getRealPath("/") + "tempDefinition.xml");
            if (myObj.exists()) {
                FileWriter file = new FileWriter(myObj,false);
                file.write(xml);
                file.close();
            } else {
                myObj.createNewFile();
                FileWriter file = new FileWriter(myObj,false);
                file.write(xml);
                file.close();
            }
            System.out.println("Fatto");
            return myObj;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    public File getDefinition() {
        try{
            File myObj = new File(path + "tempDefinition.xml");
            if (myObj.exists()) {
                return myObj;
            }
        }catch (NullPointerException e) {
            return null;
        }
        return null;
    }

    }