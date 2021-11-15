package com.example.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

@ServerEndpoint(value = "/simpleChat")
public class SimpleChatEndpoint {
	
	private static final List<Session> sessionList = new Vector<>(); // 채팅방 1개
	
	@OnOpen
	public void handleOpen(Session session) throws IOException {
		System.out.println("@OnOpen : 클라이언트 " + session.getId() + " 가 서버에 연결됨...");
		
		sessionList.add(session); // 접속한 사용자(클라이언트)를 리스트에 추가
	}
	
	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {
		System.out.println("@OnMessage : 클라이언트 " + session.getId() + " 로부터 메시지를 받음...");
		
		// 브로드캐스팅하기
		for (Session sess : sessionList) {
			sess.getBasicRemote().sendText(message);
		} // for
	}
	
	@OnClose
	public void handleClose(Session session, CloseReason closeReason) throws IOException {
		System.out.println("@OnClose : 클라이언트 " + session.getId() + " 와 " + closeReason + " 이유로 인해 연결이 끊어짐...");
		
		sessionList.remove(session);
	}
	
	@OnError
	public void handleError(Session session, Throwable throwable) {
		System.out.println("@OnError : 현재 클라이언트 " + session.getId() + " 와 연결중에 에러가 발생됨...");
		
		System.out.println(throwable.getMessage());
		throwable.printStackTrace();
	}
}









