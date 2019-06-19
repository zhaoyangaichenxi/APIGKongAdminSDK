package com.inspur.cloud.admin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inspur.cloud.admin.entity.Plugin;
import com.inspur.cloud.admin.entity.Route;
import com.inspur.cloud.admin.entity.ServiceApi;
import com.inspur.cloud.admin.entity.YamlRules;

@Component
public class KongAdminUtil {

	/**
	 * Logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(KongAdminUtil.class);


	@Value("${kong.templateurl}")
	private String templateurl;
	
	@Value("${kong.kongurl}")
	private String kongurl;
	
	@Value("${kong.kongrealurl}")
	private String kongrealurl;
	
	
	@SuppressWarnings("unchecked")
	public boolean createApi(ArrayList<ServiceApi> serverlist,ArrayList<Route> routeList,ArrayList<Plugin> pluginList,String clusterIp) throws Exception {
		String kongName = clusterIp.replace(":", "");
		String beanToYaml = beanToYaml(serverlist, routeList,pluginList,kongName);
		try {
			clusterIp = "http://"+clusterIp+"/config";
			HttpClientUtil.sendHttpPostJson(clusterIp, beanToYaml);
			return true;
		} catch (Exception e) {
			logger.error("msg", e);
			return false;
		}
	
	}
	
	public String beanToYaml(ArrayList<ServiceApi> serverlist,ArrayList<Route> routeList,ArrayList<Plugin> pluginList,String clusterIp)throws Exception{
		Yaml yaml = new Yaml();
		YamlRules rule = new YamlRules();
		rule.setServices(serverlist);
		rule.setRoutes(routeList);
		if(pluginList!=null) {		
			rule.setPlugins(pluginList);
		}
		InputStream in = getClass().getResourceAsStream(templateurl);
		File dest = new File(kongurl+clusterIp+".yml");
		copyFileUsingFileStreams(in, dest);
		FileWriter fileWriter = new FileWriter(kongurl+clusterIp+".yml",true);
		yaml.dump(rule,fileWriter);
		fileWriter.close();
		deleteLine(kongurl+clusterIp+".yml",clusterIp);
		String header = "config: |\n";
		appendFileHeader(header.getBytes(), kongrealurl+clusterIp+".yml");
		String yamlToJson = YamlToJson(kongrealurl+clusterIp+".yml");
		//deleteFile(kongurl+clusterIp+".yml");
		//deleteFile(kongrealurl+clusterIp+".yml");
		return yamlToJson;
	}
	
	private String YamlToJson(String dest)throws Exception{
		Gson gs = new GsonBuilder().disableHtmlEscaping().create();
		Map<String, Object> loaded = null;
		FileInputStream fis = new FileInputStream(dest);
		Yaml yaml = new Yaml();
		loaded = (Map<String, Object>) yaml.load(fis);
		logger.info(gs.toJson(loaded));
		String paramsJson = gs.toJson(loaded);
		fis.close();
		return paramsJson;
	}
	
	public boolean deleteFile(String sPath) {  
	    Boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  
	
	private  void copyFileUsingFileStreams(InputStream input, File dest)
	        throws IOException {      
	    OutputStream output = null;    
	    try {
	           //input = new FileInputStream(source);
	           output = new FileOutputStream(dest);        
	           byte[] buf = new byte[1024];        
	           int bytesRead;        
	           while ((bytesRead = input.read(buf)) > 0) {
	               output.write(buf, 0, bytesRead);
	           }
	    } finally {
	        input.close();
	        output.close();
	    }
	}
	
	private  void appendFileHeader(byte[] header,String srcPath) throws Exception{
		RandomAccessFile src = new RandomAccessFile(srcPath, "rw");
		int srcLength = (int)src.length() ;
		byte[] buff = new byte[srcLength];
			src.read(buff , 0, srcLength);
			src.seek(0);
			src.write(header);
			src.seek(header.length);
			src.write(buff);
			src.close();
	}


	
	private void deleteLine(String dest,String clusterIp) throws Exception{
		File inFile = new File(dest);
        File outFile = new File(kongrealurl+clusterIp+".yml");

        BufferedReader br = null;
        String readedLine;
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            br = new BufferedReader(new FileReader(inFile));
            int idx = 0;
            //此处要去掉yaml中的注释，否则转换会出现问题
            while ((readedLine = br.readLine()) != null) {
            	if(readedLine.contains("- config: !!com.inspur.cloud.admin.entity.FirstConfig")) {
            		bw.write("  "+readedLine.replaceAll("- config: !!com.inspur.cloud.admin.entity.FirstConfig", "- config:")+"\r\n");
            		readedLine = br.readLine();
            	}else if(readedLine.contains("- config: !!com.inspur.cloud.admin.entity.SecondConfig")) {
            		bw.write("  "+readedLine.replaceAll("- config: !!com.inspur.cloud.admin.entity.SecondConfig","- config:")+"\r\n");
            		readedLine = br.readLine();
            	}
            	else if (readedLine.contains("!!")) {
                    continue;
                }
                bw.write("  "+readedLine + "\n");
                if (idx++ == 100) {
                    bw.flush();
                    idx = 0;
                }
            }
            bw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

	}
}
