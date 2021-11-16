package XMLUploadExternalFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SemanticDefinitionCreator extends HttpServlet {

    public SemanticDefinitionCreator()
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
       if(request.getParameter("n1")!=null){
            createStencil(request.getParameter("n1"));
       }
    }

    public void createStencil(String xml){
        try {
            String f = getServletContext().getRealPath("/");
            File myObj = new File(getServletContext().getRealPath("/") + "tempSemanticDefinition.xml");
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
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public File getSemantic(){
        try{
            File myObj = new File(getServletContext().getRealPath("/") + "tempSemanticDefinition.xml");
            if (myObj.exists()) {
                return myObj;
            }
        }catch (NullPointerException e){
            return null;
        }
        return null;
    }


}