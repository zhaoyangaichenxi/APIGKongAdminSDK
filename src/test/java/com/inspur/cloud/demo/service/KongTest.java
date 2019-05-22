package com.inspur.cloud.demo.service;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.inspur.cloud.admin.KongApplication;
import com.inspur.cloud.admin.entity.Route;
import com.inspur.cloud.admin.entity.ServiceApi;
import com.inspur.cloud.admin.util.KongAdminUtil;

@SpringBootTest(classes = KongApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
public class KongTest {

	@Autowired
	private KongAdminUtil kongAdminUtil;

	@Test
	public void sendHttp() throws Exception {
		ArrayList<ServiceApi> serverlist = new ArrayList<ServiceApi>();
    	ServiceApi server = new ServiceApi();
    	server.setName("mytest");
    	server.setUrl("http://example.com");
    	ServiceApi server1 = new ServiceApi();
    	server1.setName("mytest1");
    	server1.setUrl("http://example.com");
    	serverlist.add(server);
    	serverlist.add(server1);
    	ArrayList<Route> routeList = new ArrayList<Route>();
    	Route route = new Route();
    	route.setName("myroute");
    	ArrayList<String> list = new ArrayList<String>();
    	list.add("/demo1");
    	route.setPaths(list);
    	route.setService("mytest");
    	Route route1 = new Route();
    	route1.setName("myroute1");
    	ArrayList<String> list1 = new ArrayList<String>();
    	list1.add("/demo2");
    	route1.setPaths(list1);
    	route1.setService("mytest1");
    	routeList.add(route);
    	routeList.add(route1);
		boolean createApi = kongAdminUtil.createApi(serverlist, routeList, "10.221.129.134");
		System.out.println(createApi);
	}

	@Test
	public void toKongXml() throws Exception {
		ArrayList<ServiceApi> serverlist = new ArrayList<ServiceApi>();
    	ServiceApi server = new ServiceApi();
    	server.setName("mytest");
    	server.setUrl("http://example.com");
    	serverlist.add(server);
    	ArrayList<Route> routeList = new ArrayList<Route>();
    	Route route = new Route();
    	route.setName("myroute");
    	ArrayList<String> list = new ArrayList<String>();
    	list.add("/test");
    	route.setPaths(list);
    	route.setService("mytest");
    	routeList.add(route);
    	String beanToYaml = kongAdminUtil.beanToYaml(serverlist, routeList);
    	System.out.println(beanToYaml);
	}
}