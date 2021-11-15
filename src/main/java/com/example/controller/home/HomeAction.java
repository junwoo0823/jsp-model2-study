package com.example.controller.home;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.controller.Action;
import com.example.controller.ActionForward;

// /index.do 명령어 처리를 담당하는 컨트롤러

public class HomeAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 처리...
		
		ActionForward forward = new ActionForward();
		forward.setRedirect(false);
		forward.setPath("index");
		
		return forward;
	}

}
