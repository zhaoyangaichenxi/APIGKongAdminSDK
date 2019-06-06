package com.inspur.cloud.demo.service;

import java.util.ArrayList;
import java.util.UUID;

import javax.swing.RepaintManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.inspur.cloud.admin.KongApplication;
import com.inspur.cloud.admin.entity.Config;
import com.inspur.cloud.admin.entity.Plugin;
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
		long currentTimeMillis = System.currentTimeMillis();
		String name = String.valueOf(currentTimeMillis);
		Thread.sleep(10);
		long currentTimeMillis1 = System.currentTimeMillis();
		String name1 = String.valueOf(currentTimeMillis1);
		System.out.println(name);
		System.out.println(name1);
		ArrayList<ServiceApi> serverlist = new ArrayList<ServiceApi>();
    	ServiceApi server = new ServiceApi();
    	server.setName(name);
    	server.setUrl("http://www.example.com");
    	ServiceApi server1 = new ServiceApi();
    	server1.setName(name1);
    	server1.setUrl("http://www.baidu.com");
    	serverlist.add(server);
    	serverlist.add(server1);
    	ArrayList<Route> routeList = new ArrayList<Route>();
    	Route route = new Route();
    	route.setName(name);
    	ArrayList<String> list = new ArrayList<String>();
    	list.add("/qw");
    	route.setPaths(list);
    	route.setService(name);
    	Route route1 = new Route();
    	route1.setName(name1);
    	ArrayList<String> list1 = new ArrayList<String>();
    	list1.add("/qwer");
    	route1.setPaths(list1);
    	route1.setService(name1);
    	routeList.add(route);
    	routeList.add(route1);
    	ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
    	Plugin plugin = new Plugin();
    	plugin.setName("plugin");
    	plugin.setRoute(name);
    	Config config = new Config();
    	config.setHttpMethod("POST");
    	config.setBackendContentType("application/json");
    	config.setRequestPath("/myroute/userid/abc/678/commentid");
		config.setBackendPath("http://mockbin.org");
    	ArrayList<String> pathParams = new ArrayList<String>();
    	pathParams.add("userid");
    	pathParams.add("commentid");
		config.setPathParams(pathParams);
		ArrayList<String> replace = new ArrayList<String>();
		replace.add("path:userid;body:msg.testid1");
		replace.add("path:commentid;body:msg.testid2.testid3");
		config.setReplace(replace);
		ArrayList<String> add = new ArrayList<String>();
		add.add("head:testkey2:333");
		add.add("query:testkey3:555");
		config.setAdd(add);
		plugin.setConfig(config);
		pluginList.add(plugin);
		boolean createApi = kongAdminUtil.createApi(serverlist, routeList, pluginList,"10.110.25.114:32226");
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
    	ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
    	Plugin plugin = new Plugin();
    	plugin.setName("plugin");
    	plugin.setRoute("myroute");
    	Config config = new Config();
    	config.setHttpMethod("POST");
    	config.setBackendContentType("application/json");
    	config.setRequestPath("/myroute/userid/abc/678/commentid");
		config.setBackendPath("http://mockbin.org");
    	ArrayList<String> pathParams = new ArrayList<String>();
    	pathParams.add("userid");
    	pathParams.add("commentid");
		config.setPathParams(pathParams);
		ArrayList<String> replace = new ArrayList<String>();
		replace.add("path:userid;body:msg.testid1");
		replace.add("path:commentid;body:msg.testid2.testid3");
		config.setReplace(replace);
		ArrayList<String> add = new ArrayList<String>();
		add.add("head:testkey2:333");
		add.add("query:testkey3:555");
		config.setAdd(add);
		plugin.setConfig(config);
		pluginList.add(plugin);
    	String beanToYaml = kongAdminUtil.beanToYaml(serverlist, routeList,pluginList,"10.212.121.33");
    	System.out.println(beanToYaml);
	}
	
	@Test
	public void deleteFile() {
		kongAdminUtil.deleteFile("kong.yml");
	}
}