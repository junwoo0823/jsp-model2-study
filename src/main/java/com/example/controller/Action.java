package com.example.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 요청 명령어를 처리하는 컨트롤러 역할의 인터페이스 정의
public interface Action {

	public ActionForward execute(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception;
}
