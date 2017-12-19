package com.databloks.ormgen;


import com.databloks.dal.Substitution;
import com.databloks.dal.Substitutions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GenDashboard extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public GenDashboard() {
            super();
        }

        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      	   System.out.println(String.format("GenIndexJsp:%s", request.getMethod()));
      	   
			String method = request.getMethod().toUpperCase();
      	    System.out.println(String.format("LoadOrmGen:%s", method));
			switch(method)
			{
			   	default:
			   		super.service(request, response);
			   		break;
			}

      	}
        
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
        {
        }
        
        public void loadDashboard(PrintWriter pw)
        {
    		StringBuffer buf = new StringBuffer();
        	
    		Substitution substitution = new Substitution("doctype", "html");
    		Substitutions substitutions = new Substitutions();
    		substitutions.add(substitution);
    		substitutions.add(new Substitution("title", "Dashboard	"));

        	buf.append(new GenJs().getFromFile(substitutions));
        	buf.append(TemplateUtilities.ReadFileToStringWithTokenSubstitution("dashboardTemplate.html", substitutions));
			pw.print(buf.toString());

        }
        
}