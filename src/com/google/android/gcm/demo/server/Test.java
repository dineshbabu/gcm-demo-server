package com.google.android.gcm.demo.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Test {

	static long mostRecentDelegate = 0;
	static long mostRecentBlocked = 0;
	public static final String DATA_SERVER = "http://172.20.10.5:8080/gcm-demo/sendAll";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	  /*File storyFile = new File("C:\\apache-tomcat-6.0.33\\StoryList.txt");
	  BufferedReader br = new BufferedReader(new FileReader(storyFile));
	  String line = "";
	   System.out.println(Boolean.valueOf("true"));
	  while( (line = br.readLine()) != null ){
		  String[] storyDetails = line.split(",");  
		  System.out.println(storyDetails);
	  }*/
		String folderName = "c:\\agilator";
		String storyFile = "";
		File file = null;
		while(true){		
			
			file = new File(folderName);
			
			//Read delegated stories
			
			String delegatedStory = "";
			
			String[] delegatedList = file.list(new FilenameFilter(){			
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("delegate.json");
					//return name.endsWith("delegate.json");
				}
			});
			
			for (int i = 0; i < delegatedList.length; i++) {
				storyFile = folderName+ "\\"+delegatedList[i];
				delegatedStory = readStoryFile(storyFile);
				if(isNewFile(storyFile, false)){
					sendNotification(DATA_SERVER, delegatedStory, true);
				}			
			}
			
			//Read blocked stories
			String blockedStory = "";
			String[] blockedList = file.list(new FilenameFilter(){			
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("block.json");
					//return name.endsWith("delegate.json");
				}
			});
			
			for (int i = 0; i < blockedList.length; i++) {
				storyFile = folderName+ "\\"+blockedList[i];
				blockedStory = readStoryFile(storyFile);
				if(isNewFile(storyFile, false)){
					sendNotification(DATA_SERVER, blockedStory, false);	
				}			
			}
		}
				
	}
	
	public static String readStoryFile(String storyFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(storyFile));
		return br.readLine();  
	}
	
	public static boolean isNewFile(String filePath, boolean blocked){
		File file = new File(filePath);
		boolean isNew = false;
		if(blocked){
			if( mostRecentBlocked < file.lastModified() ){
				isNew = true;
				mostRecentDelegate = file.lastModified();
			}	
		}else{
			if( mostRecentDelegate < file.lastModified() ){
				isNew = true;
				mostRecentDelegate = file.lastModified();
			}
		}
		
		
		return isNew;
	}
	
	public static String sendNotification(String endpoint, String blockedStory, boolean android)
            throws IOException {
        URL url;
        BufferedReader reader = null;
        StringBuffer resp = new StringBuffer();
        String queryString ="blockedStory="+blockedStory+"&android="+android;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        HttpURLConnection conn = null;
        try {
        	
        	conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(queryString.getBytes().length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            //conn.setRequestProperty("blockedStory", blockedStory);
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(queryString.getBytes());
            out.close();
        	
            /*conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");*/
            InputStream response = conn.getInputStream();
            try {
                reader = new BufferedReader(new InputStreamReader(response));
                for (String line; (line = reader.readLine()) != null;) {
                	resp.append(line);
                }
            } finally {
                if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
            }
            return resp.toString();
           
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}
