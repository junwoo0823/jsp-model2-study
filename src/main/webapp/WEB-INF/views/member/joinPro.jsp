<%@page import="org.mindrot.jbcrypt.BCrypt"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@page import="java.sql.Timestamp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%-- post 요청 한글처리는 필터로 처리함 --%>

<%-- MemberVO 객체 준비 --%>
<jsp:useBean id="memberVO" class="com.example.domain.MemberVO"/>

<%-- 사용자 입력값 가져오기 -> MemberVO 객체에 저장하기 --%>
<jsp:setProperty property="*" name="memberVO"/>

<%-- 회원가입날짜 설정 --%>
<% memberVO.setRegDate(new Timestamp(System.currentTimeMillis())); %>

<%
// 비밀번호를 jbcrypt 라이브러리 사용해서 암호화하여 저장하기
String passwd = memberVO.getPasswd();
String pwHash = BCrypt.hashpw(passwd, BCrypt.gensalt()); // 60글자로 암호화된 문자열 리턴함
memberVO.setPasswd(pwHash); // 암호화된 비밀번호 문자열로 수정하기
%>

<% 
// 생년월일 문자열에서 하이픈(-) 기호 제거하기
String birthday = memberVO.getBirthday();
birthday = birthday.replace("-", ""); // 하이픈 문자열을 빈문자열로 대체
memberVO.setBirthday(birthday);

System.out.println(memberVO); // 서버 콘솔 출력
%>

<%-- MemberDAO 객체 준비 --%>
<% MemberDAO memberDAO = MemberDAO.getInstance(); %>

<%-- 회원가입 insert 처리하기 --%> 
<% memberDAO.insert(memberVO); %>

<script>
	alert('회원가입 성공');
	location.href = '/member/login.jsp'; // 로그인 화면 요청
</script>






