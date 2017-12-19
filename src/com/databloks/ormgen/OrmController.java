package com.databloks.ormgen;

import java.io.BufferedReader;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.databloks.dal.GenerationSet;
import com.databloks.dal.GenerationSets;
import com.databloks.dal.SqlHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrmController extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public OrmController() {
                super();
        }

        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      	   
			String method = request.getMethod().toUpperCase();
      	    System.out.println(String.format("OrmController:%s", method));
			switch(method)
			{
			   	default:
			   		super.service(request, response);
			   		break;
			   	case "GENERATE":
			   		doGenerate(request, response);
			   		break;
			}
      	}
        
        protected void doGenerate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
        {
        	StringBuffer buf = new StringBuffer();
        	try
        	{
        		BufferedReader reader = request.getReader();
        		String line = null;
        		while ((line = reader.readLine()) != null)
        		{
        			buf.append(line);
        		}
        	} catch (Exception e) 
        	{ e.printStackTrace(); }
          
			System.out.println(buf.toString());          

			Gson gson = new Gson();
			GenerationSet generationSet = gson.fromJson(buf.toString(), GenerationSet.class);
			System.out.println(generationSet.getDatabaseName());
			System.out.println(generationSet.getDataSource());
			System.out.println(generationSet.getDestinationFolder());          
			System.out.println(generationSet.getPassword());          
			System.out.println(generationSet.getProviderType());          
			System.out.println(generationSet.getUserId());   
			
			DashboardManager dashboardManager = new DashboardManager(generationSet);
			dashboardManager.GenerateModel();
        }
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
        {
        }
}