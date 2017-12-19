package com.databloks.ormgen;

import java.io.*;
import java.net.URL;
import com.databloks.dal.Substitution;
import com.databloks.dal.Substitutions;

public class GenJs {
	public String getFromFile(Substitutions substitutions)
	{
		return TemplateUtilities.ReadFileToStringWithTokenSubstitution("ormGenTemplate.js", substitutions);
	}
}
