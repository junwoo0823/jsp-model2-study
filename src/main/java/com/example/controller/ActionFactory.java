package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.controller.home.HomeAction;
import com.example.controller.member.MemberJoinAction;
import com.example.controller.member.MemberJoinProAction;
import com.example.controller.member.MemberLoginAction;

public class ActionFactory {
	
	private static ActionFactory instance;
	
	public static ActionFactory getInstance() {
		if (instance == null) {
			instance = new ActionFactory();
		}
		return instance;
	}

	private Map<String, Action> actionMap = new HashMap<>();
	
	
	private ActionFactory() {
		// 명령어와 해당 명령어를 처리하는 컨트롤러 오브젝트(Action)를 등록
		actionMap.put("/index.do", new HomeAction());

		// member 관련 컨트롤러를 해당 명령어와 함께 등록
		actionMap.put("/memberJoin.do", new MemberJoinAction());
		actionMap.put("/memberJoinPro.do", new MemberJoinProAction());
		actionMap.put("/memberLogin.do", new MemberLoginAction());
		
		// board 관련 컨트롤러를 해당 명령어와 함께 등록
		// ...
		
	} // 기본생성자
	

	public Action getAction(String command) {
		Action action = null;
		action = actionMap.get(command);
		return action;
	} // getAction

}





