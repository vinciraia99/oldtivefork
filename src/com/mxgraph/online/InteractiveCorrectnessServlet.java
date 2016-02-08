package com.mxgraph.online;


import it.unisa.di.weblab.localcontext.GraphDisambiguation;
import it.unisa.di.weblab.localcontext.Tester;
import it.unisa.di.weblab.localcontext.Tester.Result;
import it.unisa.di.weblab.localcontext.interactive.TesterInteractive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;



import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.mxgraph.util.mxBase64;

public class InteractiveCorrectnessServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private static final Logger log = Logger.getLogger(HttpServlet.class
			.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InteractiveCorrectnessServlet()  
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
		
		boolean verbose = true;
				
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) 
		{
			String operazione = request.getParameter("Op");
			String id = null;
			String idS = null;
			String idC = null;
			String graphicRef = null;
			String connRef = null;
			
			if (verbose)
				System.out.print(" ************************************************* \n Operazione: ");			
			
			String printOp = "";
			
			switch(operazione) {
	    		case "parserXMLDefinition":
	    			
	    			printOp = "parserXMLDefinition(linguaggio)";
				
	    			ServletContext context = request.getSession().getServletContext();
	    			String s = context.getRealPath(File.separator); 
	    			TesterInteractive.parserXMLDefinition(new FileInputStream(new File(s+"defaultDefinition.xml")));
	    			response.getWriter().print(printOp);
	    			return;
		    	case "addConnector":
					id = request.getParameter("id");
					graphicRef = request.getParameter("graphicRef");
					printOp = "addConnector("+id+", "+ graphicRef +")";
		    		
		    		TesterInteractive.addConnector(id, graphicRef);
		    		break;
		    	case "addSymbol":
					id = request.getParameter("id");
					graphicRef = request.getParameter("graphicRef");
					printOp = "addSymbol("+id+", "+ graphicRef +")";
					
		    		TesterInteractive.addSymbol(id, graphicRef);
		    		break;
		    	case "addConnection":
					idS = request.getParameter("idS");
					idC = request.getParameter("idC");
					graphicRef = request.getParameter("graphicRef");
					connRef = request.getParameter("connRef");
					printOp = "addConnection("+idS+", " + idC +", "+ graphicRef +", "+ connRef + ")";
					
					TesterInteractive.addConnection(idS,idC,graphicRef,connRef);
		    		break;
		    	case "removeConnector":
					id = request.getParameter("id");
		    		printOp = "removeConnector("+id+")";
		    		
		    		TesterInteractive.removeConnector(id);
		    		break;
		    	case "removeSymbol":
					id = request.getParameter("id");
					printOp = "removeSymbol("+id+")";
		    		
		    		TesterInteractive.removeSymbol(id);
		    		break;
		    	case "removeConnection":
					idS = request.getParameter("idS");
					idC = request.getParameter("idC");
					graphicRef = request.getParameter("graphicRef");
					connRef = request.getParameter("connRef");
					printOp = "removeConnection("+idS+", " + idC +", "+ graphicRef +", "+ connRef + ")";
					
					TesterInteractive.removeConnection(idS,idC,graphicRef,connRef);
		    		break;
		    	case "test":
		    		GraphDisambiguation grpT = createGraph(request,response);
		    		System.out.println(" TEST ");
		    		System.out.println(" ************************************************* \n");
		    		printOp = TesterInteractive.test(grpT);
		    		response.getWriter().print(printOp);
					verbose = false;
					
		    		break;
		    	default:
		    		response.getWriter().print("Operazione Non Trovata");
		    		break;
			}
			
			HashMap<String, ArrayList<String>> problems = TesterInteractive.resultSelfCheck();
			ArrayList<String> problemsGlobal = TesterInteractive.resultGlobalCheck();
			
			try {
				JSONObject json      = new JSONObject();
				JSONArray  arrayobj = new JSONArray();
				
				if (!problemsGlobal.isEmpty()) {
					problems.put("Global Constraints", problemsGlobal);
				} else if (problems.containsKey("Global Constraints")) {
					problems.remove("Global Constraints");
				}
				
				if(problems.size()!=0) {
					JSONObject obj;
					String result = "";
					for(Map.Entry<String,ArrayList<String>> entry:problems.entrySet()) {
					
						obj = new JSONObject();
						obj.put("Key", entry.getKey());
						result = "";

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
		

			if (verbose) {
				System.out.println(printOp);
				System.out.println(" ************************************************* ");
				TesterInteractive.toStringGraphBuilderInteractive();
			}
			
		}
		else 
		{
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}
	
	private static GraphDisambiguation createGraph(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException, CloneNotSupportedException, ScriptException{
		
		long t0 = System.currentTimeMillis();
		String enc = request.getParameter("data");
		String xml = null;
		GraphDisambiguation gr = null;

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
				
				
				ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
				
				Result res=Tester.run(input, new FileInputStream(new File(s+"defaultDefinition.xml")));				
				gr = res.getGraphDisambiguation();
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
		return gr;

	}
	 

}
