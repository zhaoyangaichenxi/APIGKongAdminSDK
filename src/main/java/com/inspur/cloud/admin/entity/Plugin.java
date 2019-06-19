package com.inspur.cloud.admin.entity;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Plugin {
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("route")
	private String route;
	
	@SerializedName("config")
	private Object config;
}
