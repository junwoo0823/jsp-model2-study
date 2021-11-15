package com.example.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


// 각 클라이언트가 서버에 연결될때마다
// @ServerEndpoint 애노테이션이 붙은 클래스 객체가 
// 매번 별도의 스레드 내에서 생성되어 실행되는 구조임

@ServerEndpoint(value = "/echo")
public class EchoEndpoint {
	
	@OnOpen
	public void handleOpen(Session session) throws IOException {
		System.out.println("@OnOpen : 클라이언트 " + session.getId() + " 가 서버에 연결됨...");
	}
	
	@OnMessage
	public String handleMessage(Session session, String message) throws IOException {
		System.out.println("@OnMessage : 클라이언트 " + session.getId() + " 로부터 메시지를 받음...");
		
		System.out.println("클라이언트 " + session.getId() +  " 로부터 받은 메시지 : " + message);
		
		String echoMessage = "echo " + message;
		System.out.println("클라이언트 " + session.getId() + " 로 보내는 메시지 : " + echoMessage);
		
		return echoMessage;
	}
	
	@OnClose
	public void handleClose(Session session, CloseReason closeReason) throws IOException {
		System.out.println("@OnClose : 클라이언트 " + session.getId() + " 와 " + closeReason + " 이유로 인해 연결이 끊어짐...");
	}
	
	@OnError
	public void handleError(Session session, Throwable throwable) {
		System.out.println("@OnError : 현재 클라이언트 " + session.getId() + " 와 연결중에 에러가 발생됨...");
		
		System.out.println(throwable.getMessage());
		throwable.printStackTrace();
	}
	
}






