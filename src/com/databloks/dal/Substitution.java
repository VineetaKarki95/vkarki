package com.databloks.dal;

public class Substitution
{
	private String token;
	private String value;
	
	public Substitution(){}
	public Substitution(String token, String value)
	{
		this.token = token;
		this.value = value;
	}
	
	public String getToken(){ return token; }
	public void setToken(String token){ this.token = token; }
	public String getValue(){ return value; }
	public void setValue(String value){ this.value = value; }
}
