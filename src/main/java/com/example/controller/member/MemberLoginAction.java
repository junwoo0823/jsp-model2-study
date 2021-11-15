package com.example.controller.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.controller.Action;
import com.example.controller.ActionForward;

public class MemberLoginAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ActionForward forward = new ActionForward();
		forward.setRedirect(false);
		forward.setPath("member/login");
		
		return forward;
	}

}
