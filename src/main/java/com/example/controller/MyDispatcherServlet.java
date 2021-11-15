package com.example.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.controller.home.HomeAction;
import com.example.controller.member.MemberJoinAction;
import com.example.controller.member.MemberJoinProAction;
import com.example.controller.member.MemberLoginAction;

// MVC(모델2): Model-View-Controller 패턴으로 작성하는 방식

// 컨트롤러: 뷰와 모델 사이에서 제어를 담당하는 클래스

// 프론트 컨트롤러 패턴: 모든 사용자 요청을 전담해서 받아 처리하는 클래스
//                       뷰(JSP)가 직접적으로 요청을 받으면 안됨!

// MyDispatcherServlet 클래스를 프론트 컨트롤러 역할로 작성.

//@WebServlet("*.do")
public class MyDispatcherServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("service 호출됨.");
		
		// http://localhost:8090/myweb/
		// http://localhost:8090/
		
		// 1. 사용자 요청주소를 통해 요청 명령어 구하기
		String requestURI = request.getRequestURI();
		System.out.println("requestURI : " + requestURI); // "/myweb/index.do"
		                                                  // "/index.do"
		String contextPath = request.getContextPath();
		System.out.println("contextPath : " + contextPath); // "/myweb"
		                                                    // ""
		String command = requestURI.substring(contextPath.length());
		System.out.println("command : " + command); // "/index.do"
		
		
		
		// 2. 명령어를 처리를 담당하는 해당 컨트롤러를 가져와서 실행하기
		ActionForward forward = null;
		Action action = null;
		
		ActionFactory actionFactory = ActionFactory.getInstance();
		action = actionFactory.getAction(command); // 키에 해당하는 값이 없을때는 null을 리턴
		
		if (action != null) {
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		// 3. 작업 처리 후 사용자에게 알맞은 응답(화면 또는 데이터)을 보내기
		// 이동방식 2가지: redirect (*.do) , forward (.jsp)
		if (forward != null) {
			if (forward.isRedirect() == true) {
				response.sendRedirect(forward.getPath()); // *.do 리다이렉트로 다시 요청 받음
			} else { // forward 이동
				String prefix = "/WEB-INF/views/";
				String suffix = ".jsp";
				String path = prefix + forward.getPath() + suffix;
				
				RequestDispatcher dispatcher = request.getRequestDispatcher(path); // .jsp 바로 실행 
				dispatcher.forward(request, response);
			}
		}
		
	} // service
	
}








