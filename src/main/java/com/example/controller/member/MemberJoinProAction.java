package com.example.controller.member;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import com.example.controller.Action;
import com.example.controller.ActionForward;
import com.example.domain.MemberVO;
import com.example.repository.MemberDAO;

// 회원가입처리를 담당하는 컨트롤러 클래스
public class MemberJoinProAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		MemberVO memberVO = new MemberVO();
		
		memberVO.setId(request.getParameter("id"));
		memberVO.setPasswd(request.getParameter("passwd"));
		memberVO.setName(request.getParameter("name"));
		memberVO.setBirthday(request.getParameter("birthday"));
		memberVO.setGender(request.getParameter("gender"));
		memberVO.setEmail(request.getParameter("email"));
		memberVO.setRecvEmail(request.getParameter("recvEmail"));
		
		// 회원가입날짜 설정
		memberVO.setRegDate(new Timestamp(System.currentTimeMillis()));
		
		// 비밀번호를 jbcrypt 라이브러리 사용해서 암호화하여 저장하기
		String passwd = memberVO.getPasswd();
		String pwHash = BCrypt.hashpw(passwd, BCrypt.gensalt()); // 60글자로 암호화된 문자열 리턴함
		memberVO.setPasswd(pwHash); // 암호화된 비밀번호 문자열로 수정하기
		
		// 생년월일 문자열에서 하이픈(-) 기호 제거하기
		String birthday = memberVO.getBirthday();
		birthday = birthday.replace("-", ""); // 하이픈 문자열을 빈문자열로 대체
		memberVO.setBirthday(birthday);

		System.out.println(memberVO); // 서버 콘솔 출력
		
		// MemberDAO 객체 준비
		MemberDAO memberDAO = MemberDAO.getInstance();
		
		// 회원가입 insert 처리하기
		memberDAO.insert(memberVO);
		
		 
		// 폼 태그로부터 사용자 입력값을 받아서 처리한 이후에
		// 응답을 줄때는 항상 리다이렉트 방식을 사용함.
		ActionForward forward = new ActionForward();
		forward.setRedirect(true);
		forward.setPath("/memberLogin.do"); 
		
		return forward;
	}

}





