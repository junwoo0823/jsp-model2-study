<%@page import="org.mindrot.jbcrypt.BCrypt"%>
<%@page import="com.example.domain.MemberVO"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
// 폼 id passwd rememberMe 파라미터 가져오기
String id = request.getParameter("id");
String passwd = request.getParameter("passwd");
String rememberMe = request.getParameter("rememberMe");

// MemberDAO 객체 준비하기
MemberDAO memberDAO = MemberDAO.getInstance();

MemberVO memberVO = memberDAO.getMemberById(id);

if (memberVO != null) { // id 일치
	// if (passwd.equals(memberVO.getPasswd()))
		
	if (BCrypt.checkpw(passwd, memberVO.getPasswd()) == true) { // passwd 일치
		// 로그인 인증 처리
		// 사용자 당 유지되는 세션객체에 기억할 데이터를 저장
		session.setAttribute("id", id);
	
		// 사용자가 로그인 상태유지를 체크했으면
		if (rememberMe != null) {
			// 쿠키 생성
			Cookie cookie = new Cookie("loginId", id);
			// 쿠키 유효시간(유통기한) 설정
			//cookie.setMaxAge(60 * 10); // 초단위로 설정. 10분 = 60초 * 10
			cookie.setMaxAge(60 * 60 * 24 * 7); // 1주일 설정.

			// 쿠키 경로설정
			cookie.setPath("/"); // 프로젝트 모든 경로에서 쿠키 받도록 설정
			
			// 클라이언트로 보낼 쿠키를 response 응답객체에 추가하기. -> 응답시 쿠키도 함께 보냄.
			response.addCookie(cookie);
		}
	
		// index.jsp 페이지로 이동하기
		//  리다이렉트 정보 : 서버가 시킨대로 재요청하는 주소
		response.sendRedirect("/index.jsp");
		//return;
		%>
		<%-- 
		<script>
			alert('로그인 성공');
			location.href = '/index.jsp';
		</script>
		--%>
		<%
	} else { // passwd 불일치
		%>
		<script>
			alert('비밀번호 틀림');
			history.back(); // 뒤로가기
		</script>
		<%
	}
} else { // id 불일치
	%>
	<script>
		alert('아이디 없음');
		//location.href = '/member/login.jsp'; // 로그인폼 페이지로 새로 요청하기
		history.back(); // 뒤로가기
	</script>
	<%
}
%>





