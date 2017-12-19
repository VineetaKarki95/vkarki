package com.databloks.ormgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import com.databloks.dal.Substitution;
import com.databloks.dal.Substitutions;

public class TemplateUtilities {

	public static String ApplySubstitutionList(String line, Substitutions SubstitutionList)
	{
		for(Substitution substitution : SubstitutionList)
		{			
			String tokenizedString = TokenizeString(substitution.getToken());
			String updatedLine = line.replaceAll(tokenizedString, substitution.getValue());
			line = updatedLine;
		}
		return line;
	}
	public static void CopyFileWithTokenSubstitution(String sourcePath, String targetPath, Substitutions substitutionList)
	{
		try
		{
			File file = new File(sourcePath);
			CopySourceToTargetWithTokenSubstitution(file, targetPath, substitutionList);
		}
		catch(Exception e)
		{
			
		}
		
	}
	public static void CopyResourceWithTokenSubstitution(String sourceRelativePath, String targetPath, Substitutions substitutionList)
	{
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL url = classLoader.getResource(sourceRelativePath);
			File file;
			if(url != null)
			{
				file = new File(url.getPath());
			}
			else
			{
				file = new File(sourceRelativePath);
			}
			CopySourceToTargetWithTokenSubstitution(file, targetPath, substitutionList);
		}
		catch(Exception e)
		{
			
		}
		
	}
	public static void CopySourceToTargetWithTokenSubstitution(File file, String targetPath, Substitutions substitutionList)
	{
		try
		{
			StringBuffer buf = new StringBuffer();
		    BufferedReader reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {
		        buf.append(ApplySubstitutionList(line, substitutionList)+"\n");
		    }		
			reader.close();
			PrintWriter out = new PrintWriter(targetPath);
			out.write(buf.toString());
			out.close();
		}
		catch(Exception e)
		{
			
		}
	}
	public static String ReadFileToString(String fileRelativePath)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL url = classLoader.getResource(fileRelativePath);
			File file = new File(url.getPath());
		    BufferedReader reader = new BufferedReader(new FileReader(file));

		    String line;
		    while ((line = reader.readLine()) != null) {
		        buf.append(line+"\n");
		    }		
			reader.close();
		}
		catch(Exception e)
		{
			
		}
		return buf.toString();
		
	}
	public static String ReadFileToStringWithTokenSubstitution(String fileRelativePath, Substitutions SubstitutionList)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL url = classLoader.getResource(fileRelativePath);
			File file;
			if(url != null)
			{
				file = new File(url.getPath());
			}
			else
			{
				file = new File(fileRelativePath);
			}

		    BufferedReader reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {
		        buf.append(ApplySubstitutionList(line, SubstitutionList)+"\n");
		    }		
			reader.close();
		}
		catch(Exception e)
		{
			
		}
		return buf.toString();
		
	}
	public static String TokenizeString(String token)
	{
		String tokenized = "\\{\\{" + token + "\\}\\}";
		return tokenized;
	}
}
