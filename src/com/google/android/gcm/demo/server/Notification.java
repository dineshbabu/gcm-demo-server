package com.google.android.gcm.demo.server;

public interface Notification {
	
	public void send(boolean blocked, String storyID, String userID);

}
