package com.databloks.ormgen.modelgen;

import java.util.ArrayList;

public class ModelEntityKeys extends ArrayList<ModelEntityKey>
{
	public ModelEntityKeys(){}
    public boolean IsKey(String name)
    {
    	boolean isKey = false;
    	for(ModelEntityKey key : this)
    	{
    		if(key.getKeyName().equals(name))
    		{
    			isKey = true;
    			break;
    		}
    	}
    	return isKey;
    }
}
