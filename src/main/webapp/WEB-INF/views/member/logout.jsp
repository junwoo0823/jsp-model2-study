<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
// 로그아웃 처리. 세션 초기화
// session.removeAttribute("id"); // 키에 해당하는 요소 한개 삭제
session.invalidate(); // 초기화. 모두 삭제

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

response.sendRedirect("/member/login.jsp"); // 로그인 화면으로 이동
%>
<%--
<script>
	alert('로그아웃 되었습니다.');
	location.href = '/member/login.jsp'; // 로그인 화면으로 이동
</script>
 --%>
