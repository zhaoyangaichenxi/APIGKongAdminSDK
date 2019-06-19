package com.inspur.cloud.admin.entity;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FirstConfig {
	@SerializedName("httpMethod")
	private String httpMethod;
	
	@SerializedName("backendContentType")
	private String backendContentType;
	
	@SerializedName("requestPath")
	private String requestPath;
	
	@SerializedName("backendPath")
	private String backendPath;
	
	@SerializedName("pathParams")
	private ArrayList<String> pathParams;
	
	@SerializedName("replace")
	private ArrayList<String> replace;
	
	@SerializedName("add")
	private ArrayList<String> add;	
}
