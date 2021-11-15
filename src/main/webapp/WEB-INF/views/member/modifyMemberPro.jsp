<%@page import="org.mindrot.jbcrypt.BCrypt"%>
<%@page import="com.example.domain.MemberVO"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@page import="java.sql.Timestamp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:useBean id="memberVO" class="com.example.domain.MemberVO" />

<jsp:setProperty property="*" name="memberVO"/>

<% memberVO.setRegDate(new Timestamp(System.currentTimeMillis())); %>

<% 
// 생년월일 문자열에서 하이픈(-) 기호 제거하기
String birthday = memberVO.getBirthday();
birthday = birthday.replace("-", ""); // 하이픈 문자열을 빈문자열로 대체
memberVO.setBirthday(birthday);

System.out.println(memberVO); // 서버 콘솔 출력
%>


<%-- MemberDAO 객체 준비 --%>
<% MemberDAO memberDAO = MemberDAO.getInstance(); %>

<%-- DB 테이블에서 id에 해당하는 데이터 행 가져오기 --%>
<% MemberVO dbMemberVO = memberDAO.getMemberById(memberVO.getId()); %>

<%
// 비밀번호 일치하면 회원정보 수정하기
// if (memberVO.getPasswd().equals(dbMemberVO.getPasswd()))

if (BCrypt.checkpw(memberVO.getPasswd(), dbMemberVO.getPasswd()) == true) {
	
	memberDAO.updateById(memberVO); // 수정
	%>
	<script>
		alert('회원정보 수정성공');
		location.href = '/index.jsp';
	</script>	
	<%
} else {
	%>
	<script>
		alert('비밀번호 틀림');
		history.back(); // 뒤로가기
	</script>
	<%
}
%>


    
    
    