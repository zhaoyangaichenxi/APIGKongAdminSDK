package com.inspur.cloud.admin.entity;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ServiceApi {
	

	@SerializedName("name")
	private String name;
	
	@SerializedName("url")
	private String url;
	
}
