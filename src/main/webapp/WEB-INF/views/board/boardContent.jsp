<%@page import="com.example.domain.AttachVO"%>
<%@page import="java.util.List"%>
<%@page import="com.example.repository.AttachDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.example.domain.BoardVO"%>
<%@page import="com.example.repository.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
// 상세보기 글번호 파라미터값 가져오기
int num = Integer.parseInt(request.getParameter("num"));

// 요청 페이지번호 파라미터값 가져오기
String pageNum = request.getParameter("pageNum");

// DAO객체 준비
BoardDAO boardDAO = BoardDAO.getInstance();
AttachDAO attachDAO = AttachDAO.getInstance();

// 조회수 1 증가시키기
boardDAO.updateReadcount(num);

// 상세보기할 글 한개 가져오기
BoardVO boardVO = boardDAO.getBoard(num);

// 화면에 표시할 날짜형식
SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

// 첨부파일 정보 리스트 가져오기
List<AttachVO> attachList = attachDAO.getAttachesByBno(num);
%>
    
    
<!DOCTYPE html>
<html lang="ko">

<head>
  <jsp:include page="/WEB-INF/views/include/head.jsp" />
  <style>
    time.comment-date {
      font-size: 13px;
    }
  </style>
</head>

<body>

  <!-- Navbar area -->
  <jsp:include page="/WEB-INF/views/include/top.jsp" />
  <!-- end of Navbar area -->


  <!-- Page Layout here -->
  <div class="row container">

	<!-- left menu area -->
	<jsp:include page="/WEB-INF/views/include/left.jsp" />
    <!-- end of left menu area -->

    <div class="col s12 m8 l9">
      <!-- page content  -->
      <div class="section">

        <div class="card-panel">
          <div class="row">
            <div class="col s12" style="padding: 0 2%;">

              <h5>게시판 상세보기</h5>
              <div class="divider" style="margin: 30px 0;"></div>

              <table class="striped" id="boardList">
                  <tr>
                    <th class="center-align">제목</th>
                    <td colspan="5"><%=boardVO.getSubject() %></td>
                  </tr>
                  <tr>
                    <th class="center-align">작성자</th>
                    <td><%=boardVO.getMid() %></td>
                    <th class="center-align">작성일</th>
                    <td><%=sdf.format(boardVO.getRegDate()) %></td>
                    <th class="center-align">조회수</th>
                    <td><%=boardVO.getReadcount() %></td>
                  </tr>
                  <tr>
                    <th class="center-align">내용</th>
                    <td colspan="5">
                      <pre><%=boardVO.getContent() %></pre>
                    </td>
                  </tr>
                  <tr>
                    <th class="center-align">첨부파일</th>
                    <td colspan="5">
                   	<%
                   	if (attachList.size() > 0) { // 첨부파일이 있으면
                   		%>
                   		<ul>
                   		<%
                   		for (AttachVO attach : attachList) {
                   			if (attach.getFiletype().equals("O")) { // 일반파일
                   				// 다운로드할 일반파일 경로
                   				String fileCallPath = attach.getUploadpath() + "/" + attach.getFilename();
                   				%>
                       			<li>
                       				<a href="/board/download.jsp?fileName=<%=fileCallPath %>">
	                       				<i class="material-icons">file_present</i>
	                       				<%=attach.getFilename() %>
                       				</a>
                       			</li>
                       			<%
                   			} else if (attach.getFiletype().equals("I")) { // 이미지파일
                   				// 썸네일 이미지 경로
                   				String fileCallPath = attach.getUploadpath() + "/s_" + attach.getFilename();
                   				// 원본 이미지 경로
                   				String fileCallPathOrigin = attach.getUploadpath() + "/" + attach.getFilename();
                   				%>
                       			<li>
                       				<a href="/board/display.jsp?fileName=<%=fileCallPathOrigin %>">
                       					<img src="/board/display.jsp?fileName=<%=fileCallPath %>">
                       				</a>
                       			</li>
                       			<%
                   			}
                   			
                   		}
                   		%>
                   		</ul>
                   		<%
                   	} else { // attachList.size() == 0  // 첨부파일이 없으면
                   		%>첨부파일 없음<%
                   	}
                   	%>
                    </td>
                  </tr>
              </table>


              <div class="section">
                <div class="row">
                  <div class="col s12 right-align">
                  <%
                  String id = (String) session.getAttribute("id");
                  if (id != null) { // 로그인 했을때
                	  if (id.equals(boardVO.getMid())) { // 로그인 아이디와 글작성자 아이디가 같을때
                		  %>
                		<a class="btn waves-effect waves-light" href="/board/boardModify.jsp?num=<%=boardVO.getNum() %>&pageNum=<%=pageNum %>">
	                      <i class="material-icons left">edit</i>글수정
	                    </a>
	                    <a class="btn waves-effect waves-light" onclick="remove(event)">
	                      <i class="material-icons left">delete</i>글삭제
	                    </a>
                		  <%
                	  }
                	 %>
                    <a class="btn waves-effect waves-light" href="/board/replyWrite.jsp?reRef=<%=boardVO.getReRef() %>&reLev=<%=boardVO.getReLev() %>&reSeq=<%=boardVO.getReSeq() %>&pageNum=<%=pageNum %>">
                      <i class="material-icons left">reply</i>답글
                    </a>
                	 <% 
                  }
                  %>
                    <a class="btn waves-effect waves-light" href="/board/boardList.jsp?pageNum=<%=pageNum %>">
                      <i class="material-icons left">list</i>글목록
                    </a>
                  </div>
                </div>
              </div>

              
              <!-- comment area -->
              
              <!-- end of comment area -->

            </div>
          </div>
        </div>
        <!-- end of card-panel -->

      </div>
    </div>

  </div>

  <!-- footer area -->
  <jsp:include page="/WEB-INF/views/include/bottom.jsp" />
  <!-- end of footer area -->


  <!-- Scripts -->
  <jsp:include page="/WEB-INF/views/include/commonJs.jsp" />
  <script>
    // 글삭제 버튼을 클릭하면 호출되는 함수
  	function remove(event) {
    	event.preventDefault(); // a태그 기본동작 막기
    	
  		var isRemove = confirm('이 글을 삭제하시겠습니까?');
  		if (isRemove == true) {
  			location.href = '/board/boardRemove.jsp?num=<%=boardVO.getNum() %>&pageNum=<%=pageNum %>';
  		}
  	}
  </script>
</body>

</html>






