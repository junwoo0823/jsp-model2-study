package com.example.controller;

import lombok.Data;

@Data
public class ActionForward {

	private boolean isRedirect = false; // false면 forward, true면 redirect 방식 이동
	private String path = null; // 리다이렉트일 경우 path는 *.do 형식의 경로
	                            // 포워드 일 경우 path는  .jsp 형식의 경로
}
