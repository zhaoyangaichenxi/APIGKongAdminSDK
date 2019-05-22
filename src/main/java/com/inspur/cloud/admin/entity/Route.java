package com.inspur.cloud.admin.entity;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

//1.1版本专用
@Data
public class Route {


	
	@SerializedName("name")
	private String name;
	
	@SerializedName("paths")
	private ArrayList<String> paths;
	
	@SerializedName("service")
	private String service;
	
}
