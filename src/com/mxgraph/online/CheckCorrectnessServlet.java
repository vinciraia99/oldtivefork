package com.mxgraph.online;


import it.unisa.di.weblab.localcontext.ParserXML;
import it.unisa.di.weblab.localcontext.ParserXMLDefinition;
import it.unisa.di.weblab.localcontext.Tester;
import it.unisa.di.weblab.localcontext.Tester.Result;
import it.unisa.di.weblab.localcontext.interactive.TesterInteractive;
import it.unisa.di.weblab.localcontext.semantic.TesterSemantic;
import it.unisa.di.weblab.localcontext.semantic.TesterSemantic.ResultSemantic;
import it.unisa.di.weblab.localcontext.semantic.TextArea;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import XMLUploadExternalFile.*;



import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.mxgraph.util.mxBase64;

public class CheckCorrectnessServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private static final Logger log = Logger.getLogger(HttpServlet.class
			.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckCorrectnessServlet()  
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
		try {
			handlePost(request, response);
		} catch (CloneNotSupportedException | ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, CloneNotSupportedException, ScriptException  {
		//if (request.getContentLength() < Constants.MAX_REQUEST_SIZE)
		HttpSession session=request.getSession();  //VINCENZORAIA

		if (request.getContentLength() < 10485760)
		{
			long t0 = System.currentTimeMillis();
			String enc = request.getParameter("data");
			String xml = null;

			try 
			{
				if (enc != null && enc.length() > 0) 
				{
					xml = Utils.inflate(mxBase64.decode(URLDecoder.decode(enc,
							"UTF-8")));
				} else 
				{
					xml = request.getParameter("xml");

					if (xml != null && xml.length() > 0) 
					{
						xml = URLDecoder.decode(xml, "UTF-8");
					}
					response.setContentType("text/html");
					//response.setHeader("Content-Type", "text/html");
					response.setStatus(HttpServletResponse.SC_OK);
					ServletContext context = request.getSession().getServletContext();
					String s = context.getRealPath(File.separator); 
					
					
					//PrintWriter out = response.getWriter();
					ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
					Result res=null;
					//VINCENZO RAIA
					DefaultDefinitionCreator d = new DefaultDefinitionCreator();
					SemanticDefinitionCreator semantic = new SemanticDefinitionCreator();
					File temp = d.getDefinition();
					//---------------------
					if(temp !=null){
						res=Tester.run(input, new FileInputStream(temp));
					}else{
						res=Tester.run(input, new FileInputStream(new File(s+"\\defaultDefinition.xml")));
					}
					HashMap<String, ArrayList<TextArea>> texts = res.getTexts();
					
					HashMap<String, ArrayList<String>> problems;
					if (!res.isError()) {
						temp = semantic.getSemantic();
						ResultSemantic resSem = null;
						if(temp != null){
							resSem = TesterSemantic.runSem(new FileInputStream(temp),res);
						}else{
							resSem = TesterSemantic.runSem(new FileInputStream(new File(s+"\\defaultDefinitionSemantic.xml")),res);
						}
						problems = resSem.getProblems();
						if (!resSem.isError()){	
							ArrayList<String> c = new ArrayList<String>();
							c.add("The diagram is correct.");
							problems.put("Status", c);
							ArrayList<String> l = new ArrayList<String>();
							l.add(resSem.getGraphSemantic().getResult());
							problems.put("Result", l);
						}
						
					} else {
						problems = res.getResultSelfCheck();
						ArrayList<String> problemsGlobal = res.getResultGlobalCheck();
						
						if (!problemsGlobal.isEmpty()) {
							problems.put("Global Constraints", problemsGlobal);
						}
					}
					
					try {
						JSONObject json      = new JSONObject();
						JSONArray  arrayobj = new JSONArray();
						
						if(problems.size()!=0) {
							JSONObject obj;
							String result = "";
							for(Map.Entry<String,ArrayList<String>> entry:problems.entrySet()) {
							
								obj = new JSONObject();
								obj.put("Key", entry.getKey());
								result = "";
								
								String msg = entry.getKey();
								if (texts.containsKey(entry.getKey())){
									ArrayList<TextArea> txl = texts.get(entry.getKey());
									msg = txl.get(0).getValue();
								}
								obj.put("Msg", msg);	

								for(int i=0;i<entry.getValue().size();i++) {
									result = result+entry.getValue().get(i)+"\n";
								}															
								obj.put("Error", result);
								arrayobj.put(obj);
							}
						}
					
						json.put("Result", arrayobj);
					
						response.setContentType("application/json");
						response.getWriter().print(json.toString());
					
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
			 catch (IllegalArgumentException e) 
			{
				log.warning("Error parsing xml contents : " + xml
						+ System.getProperty("line.separator")
						+ "Original stack trace : " + e.getMessage());
				long mem = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();

				log.info("save: ip=" + request.getRemoteAddr() + " ref=\""
						+ request.getHeader("Referer") + "\" in="
						+ request.getContentLength() + " enc="
						+ ((enc != null) ? enc.length() : "[none]") + " xml="
						+ ((xml != null) ? xml.length() : "[none]") + " dt="
						+ request.getContentLength() + " mem=" + mem + " dt="
						+ (System.currentTimeMillis() - t0));
			}
			
		}
		else 
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}	

}
