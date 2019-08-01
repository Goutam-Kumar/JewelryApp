package com.android.jewelry.responsemodel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class PartyResponse{

	@SerializedName("partyname")
	private String partyname;

	public void setPartyname(String partyname){
		this.partyname = partyname;
	}

	public String getPartyname(){
		return partyname;
	}

	@Override
 	public String toString(){
		return 
			"PartyResponse{" + 
			"partyname = '" + partyname + '\'' + 
			"}";
		}
}