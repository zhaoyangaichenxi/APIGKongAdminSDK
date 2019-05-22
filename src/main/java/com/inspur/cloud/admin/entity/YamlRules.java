package com.inspur.cloud.admin.entity;

import java.util.ArrayList;

import lombok.Data;

@Data
public class YamlRules {

	//private String  _format_version ;
	private ArrayList<ServiceApi> services;
	private ArrayList<Route> routes;
}
