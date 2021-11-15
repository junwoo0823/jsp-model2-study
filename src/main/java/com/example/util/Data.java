package com.example.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.Session;

import org.quartz.plugins.history.LoggingTriggerHistoryPlugin;

public class Data {
	
	public static final List<Session> SESSION_LIST = new ArrayList<>();

	
	public static void broadcast(String message) {
		for (Session session : SESSION_LIST) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // for
	}
	
	public static void main(String[] args) {
		String str = System.getProperty("user.dir");
		System.out.println(str);
		
		Path path = Paths.get("");
		String dirName = path.toAbsolutePath().toString();
		System.out.println(dirName);

		String str1 = Data.class.getResource("").getPath();
		System.out.println(str1);
	}
}
