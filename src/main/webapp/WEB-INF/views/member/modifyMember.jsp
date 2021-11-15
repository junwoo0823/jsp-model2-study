<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.example.domain.MemberVO"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
// 세션에서 로그인 아이디 가져오기
String id = (String) session.getAttribute("id");

// DAO 객체 준비
MemberDAO memberDAO = MemberDAO.getInstance();

// 아이디에 해당하는 자신의 정보를 DB에서 가져오기
MemberVO memberVO = memberDAO.getMemberById(id);

// input type="date" 태그에 설정가능한 값이 되도록 생년월일 문자열을 변경하기
String birthday = memberVO.getBirthday(); // "20020127" -> "2002-01-27"

// 문자열 -> Date 객체 변환
SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
Date date = sdf.parse(birthday); // 생년월일 문자열을 Date 객체로 변환

// Date 객체 -> 문자열 변환
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
String strBirthday = sdf2.format(date);
%>
    
<!DOCTYPE html>
<html lang="ko">

<head>
	<jsp:include page="/WEB-INF/views/include/head.jsp" />
</head>

<body>

  <!-- Navbar area -->
  <jsp:include page="/WEB-INF/views/include/top.jsp" />
  <!-- end of Navbar area -->


  <!-- Page Layout -->
  <div class="row container">

	<!-- left menu area -->
	<jsp:include page="/WEB-INF/views/include/left.jsp" />
    <!-- end of left menu area -->

    <div class="col s12 m8 l9">
      <!-- Teal page content  -->
      <div class="section">

        <!-- card panel -->
        <div class="card-panel">
          <div class="row">
            <div class="col s12" style="padding: 0 5%;">

              <h5>내정보 수정</h5>
              <div class="divider" style="margin: 30px 0;"></div>

              <form action="/member/modifyMemberPro.jsp" method="POST" id="frm">
                <div class="row">
                  <div class="input-field col s12">
                    <i class="material-icons prefix">account_box</i>
                    <input id="id" type="text" class="validate" name="id" value="<%=memberVO.getId() %>" readonly>
                    <label for="id">아이디</label>
                  </div>
                </div>

                <div class="row">
                  <div class="input-field col s12">
                    <i class="material-icons prefix">lock</i>
                    <input id="passwd" type="password" class="validate" name="passwd">
                    <label for="passwd">비밀번호</label>
                  </div>
                </div>

                <div class="row">
                  <div class="input-field col s12">
                    <i class="material-icons prefix">person</i>
                    <input id="name" type="text" class="validate" name="name" value="<%=memberVO.getName() %>">
                    <label for="name">이름</label>
                  </div>
                </div>

                <div class="row">
                  <div class="input-field">
                    <i class="material-icons prefix">event</i>
                    <input type="date" id="birthday" name="birthday" value="<%=strBirthday %>">
                    <label for="birthday">생년월일</label>
                  </div>
                </div>

                <div class="row">
                  <div class="input-field">
                    <i class="material-icons prefix">wc</i>
                    <select name="gender">
                      <option value="" disabled <%=(memberVO.getGender() == null) ? "selected" : "" %>>성별을 선택하세요.</option>
                      <option value="M" <%=(memberVO.getGender().equals("M")) ? "selected" : "" %>>남자</option>
                      <option value="F" <%=(memberVO.getGender().equals("F")) ? "selected" : "" %>>여자</option>
                      <option value="N" <%=(memberVO.getGender().equals("N")) ? "selected" : "" %>>선택 안함</option>
                    </select>
                    <label>성별</label>
                  </div>
                </div>

                <div class="row">
                  <div class="input-field col s12">
                    <i class="material-icons prefix">mail</i>
                    <input id="email" type="email" class="validate" name="email" value="<%=memberVO.getEmail() %>">
                    <label for="email">본인 확인 이메일</label>
                  </div>
                </div>

                <p class="row center">
                  알림 이메일 수신 : &nbsp;&nbsp;
                  <label>
                    <input name="recvEmail" value="Y" type="radio" <%=(memberVO.getRecvEmail().equals("Y")) ? "checked" : "" %> />
                    <span>예</span>
                  </label>
                  &nbsp;&nbsp;
                  <label>
                    <input name="recvEmail" value="N" type="radio" <%=(memberVO.getRecvEmail().equals("N")) ? "checked" : "" %> />
                    <span>아니오</span>
                  </label>
                </p>

                <br>
                <div class="row center">
                  <button class="btn waves-effect waves-light" type="submit">수정
                    <i class="material-icons right">create</i>
                  </button>
                  &nbsp;&nbsp;
                  <button class="btn waves-effect waves-light" type="reset">초기화
                    <i class="material-icons right">clear</i>
                  </button>
                </div>
              </form>

            </div>
          </div>
        </div>
        <!-- end of card panel -->

      </div>
    </div>

  </div>
  <!-- end of Page Layout -->

  <!-- footer area -->
  <jsp:include page="/WEB-INF/views/include/bottom.jsp" />
  <!-- end of footer area -->


  <!--  Scripts-->
  <jsp:include page="/WEB-INF/views/include/commonJs.jsp" />
  <script>
  
  </script>
</body>

</html>





