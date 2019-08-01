package com.android.jewelry.responsemodel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class RodiumResponse{

	@SerializedName("name")
	private String name;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"RodiumResponse{" + 
			"name = '" + name + '\'' + 
			"}";
		}
}