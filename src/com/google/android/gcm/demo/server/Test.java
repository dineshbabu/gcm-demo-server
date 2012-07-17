package com.google.android.gcm.demo.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	  File storyFile = new File("C:\\apache-tomcat-6.0.33\\StoryList.txt");
	  BufferedReader br = new BufferedReader(new FileReader(storyFile));
	  String line = "";
	   
	  while( (line = br.readLine()) != null ){
		  String[] storyDetails = line.split(",");  
		  System.out.println(storyDetails);
	  }
		
	}

}
