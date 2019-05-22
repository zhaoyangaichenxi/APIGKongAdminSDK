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
	public boolean createApi(ArrayList<ServiceApi> serverlist,ArrayList<Route> routeList,String clusterIp) throws Exception {
		String beanToYaml = beanToYaml(serverlist, routeList);
		try {
			clusterIp = "http://"+clusterIp+":8001/config";
			HttpClientUtil.sendHttpPostJson(clusterIp, beanToYaml);
			return true;
		} catch (Exception e) {
			logger.error("msg", e);
			return false;
		}
	
	}
	
	public String beanToYaml(ArrayList<ServiceApi> serverlist,ArrayList<Route> routeList)throws Exception{
		Yaml yaml = new Yaml();
		YamlRules rule = new YamlRules();
		rule.setServices(serverlist);
		rule.setRoutes(routeList);
		File source = new File(templateurl);
		File dest = new File(kongurl);
		copyFileUsingFileStreams(source, dest);
		FileWriter fileWriter = new FileWriter(kongurl,true);
		yaml.dump(rule,fileWriter);
		deleteLine(kongurl);
		String header = "config: |\n";
		appendFileHeader(header.getBytes(), kongrealurl);
		String yamlToJson = YamlToJson(kongrealurl);
		deleteFile(kongurl);
		deleteFile(kongrealurl);
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
	
	private  void copyFileUsingFileStreams(File source, File dest)
	        throws IOException {    
	    InputStream input = null;    
	    OutputStream output = null;    
	    try {
	           input = new FileInputStream(source);
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


	
	private void deleteLine(String dest) throws Exception{
		File inFile = new File(dest);
        File outFile = new File(kongrealurl);

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
            while ((readedLine = br.readLine()) != null) {
                if (readedLine.contains("!!")) {
                    continue;
                }
                bw.write("  "+readedLine + "\n");
                if (idx++ == 100) {
                    bw.flush();
                    idx = 0;
                }
            }
            bw.flush();
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
