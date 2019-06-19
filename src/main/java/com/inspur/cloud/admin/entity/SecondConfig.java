package com.inspur.cloud.admin.entity;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SecondConfig {

	@SerializedName("httpMethod")
	private String httpMethod;
	
	
	@SerializedName("iamEndpoint")
	private String iamEndpoint;
	
	@SerializedName("timeout")
	private Long timeout;
}
