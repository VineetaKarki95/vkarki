package com.databloks.dal;

public class GenerationSet {

	private String destinationFolder;
	private String templateFolder;
	private String providerType;
	private String dataSource;
	private String userId;
	private String password;
	private String databaseName;
	
	public GenerationSet(){}
	public GenerationSet(String destinationFolder,
			String templateFolder,
			String providerType,
			String dataSource,
			String userId,
			String password,
			String databaseName)
	{
		this.destinationFolder = destinationFolder;
		this.providerType = providerType;
		this.dataSource = dataSource;
		this.userId = userId;
		this.password = password;
		this.databaseName = databaseName;
	}
	
	public String getDestinationFolder(){ return destinationFolder; }
	public void setDestinationFolder(String destinationFolder){ this.destinationFolder = destinationFolder; }
	public String getTemplateFolder(){ return templateFolder; }
	public void setTemplateFolder(String templateFolder){ this.templateFolder = templateFolder; }
	public String getProviderType(){ return providerType; }
	public void setProviderType(String providerType){ this.providerType = providerType; }
	public String getDataSource(){ return dataSource; }
	public void setSataSource(String dataSource){ this.dataSource = dataSource; }
	public String getUserId(){ return userId; }
	public void setUserId(String userId){ this.userId = userId; }
	public String getPassword(){ return password; }
	public void setPassword(String password){ this.password = password; }
	public String getDatabaseName(){ return databaseName; }
	public void setDatabaseName(String databaseName){ this.databaseName = databaseName; }

}


