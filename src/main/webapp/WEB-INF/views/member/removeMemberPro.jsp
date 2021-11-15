<%@page import="org.mindrot.jbcrypt.BCrypt"%>
<%@page import="com.example.domain.MemberVO"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
// passwd 가져오기
String passwd = request.getParameter("passwd");

// MemberDAO 객체 준비
MemberDAO memberDAO = MemberDAO.getInstance();

// 세션에서 로그인 아이디 가져오기
String id = (String) session.getAttribute("id");

// DB에서 아이디로 자신의 정보를 VO로 가져오기
MemberVO memberVO = memberDAO.getMemberById(id);

// if (passwd.equals(memberVO.getPasswd()))

if (BCrypt.checkpw(passwd, memberVO.getPasswd()) == true) {
	
	memberDAO.deleteById(id); // DB 레코드 삭제
	
	session.invalidate(); // 세션 비우기
	
	// 쿠키값 가져오기
	Cookie[] cookies = request.getCookies();

	// 특정 쿠키 삭제하기(브라우저가 삭제하도록 유효기간 0초로 설정해서 보내기)
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("loginId")) {
				cookie.setMaxAge(0); // 쿠키 유효기간 0초 설정(삭제 의도)
				cookie.setPath("/");
				response.addCookie(cookie); // 응답객체에 추가하기
			}
		}
	}
	%>
	<script>
		alert('회원탈퇴 하였습니다.');
		location.href = '/index.jsp';
	</script>
	<%
} else {
	%>
	<script>
		alert('비밀번호 틀림');
		history.back();
	</script>
	<%
}
%>
