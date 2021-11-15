package com.example.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.example.util.Data;


// 각 클라이언트가 서버에 연결될때마다
// @ServerEndpoint 애노테이션이 붙은 클래스 객체가 
// 매번 별도의 스레드 내에서 생성되어 실행되는 구조임
@ServerEndpoint(value = "/number")
public class NumberEndpoint {
	
	@OnOpen
	public void handleOpen(Session session) throws IOException {
		System.out.println("@OnOpen : 클라이언트 " + session.getId() + " 가 서버에 연결됨...");
		
		// 1~20 까지 0.5초 간격으로 숫자를 전송하기. 전송이 끝나면 서버가 연결 종료함.
		for (int i=1; i<=20; i++) {
			try {
				Thread.sleep(500); // 현재 스레드를 500ms 잠자기
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// 현재 연결되어있는 클라이언트로 문자열 전송하기
			session.getBasicRemote().sendText(String.valueOf(i));
		} // for
		
		session.close(); // 현재 연결되어있는 클라이언트와 연결 끊기
	}
	
	@OnMessage
	public void handleMessage(Session session, String message) throws IOException {
		System.out.println("@OnMessage : 클라이언트 " + session.getId() + " 로부터 메시지를 받음...");
	}
	
	@OnClose
	public void handleClose(Session session, CloseReason closeReason) throws IOException {
		System.out.println("@OnClose : 클라이언트 " + session.getId() + " 가 현재 서버에 연결을 끊음...");
	}
	
	@OnError
	public void handleError(Session session, Throwable throwable) {
		System.out.println("@OnError : 현재 클라이언트 " + session.getId() + " 와 연결중에 에러가 발생됨...");
		
		System.out.println(throwable.getMessage());
		throwable.printStackTrace();
	}
	
}






