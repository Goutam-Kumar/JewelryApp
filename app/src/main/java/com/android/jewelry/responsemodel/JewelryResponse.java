package com.android.jewelry.responsemodel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class JewelryResponse{

	@SerializedName("image")
	private String image;

	@SerializedName("cost")
	private int cost;

	@SerializedName("design_id")
	private String designId;

	@SerializedName("name")
	private String name;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCost(int cost){
		this.cost = cost;
	}

	public int getCost(){
		return cost;
	}

	public void setDesignId(String designId){
		this.designId = designId;
	}

	public String getDesignId(){
		return designId;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"JewelryResponse{" + 
			"image = '" + image + '\'' + 
			",cost = '" + cost + '\'' + 
			",design_id = '" + designId + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}