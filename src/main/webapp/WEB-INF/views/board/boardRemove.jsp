<%@page import="java.io.File"%>
<%@page import="com.example.domain.AttachVO"%>
<%@page import="java.util.List"%>
<%@page import="com.example.repository.AttachDAO"%>
<%@page import="com.example.repository.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

// num  pageNum   파라미터값 가져오기
int num = Integer.parseInt(request.getParameter("num"));
String pageNum = request.getParameter("pageNum");

// DAO 객체 준비
BoardDAO boardDAO = BoardDAO.getInstance();
AttachDAO attachDAO = AttachDAO.getInstance();

// 게시글번호에 첨부된 첨부파일 리스트 가져오기
List<AttachVO> attachList = attachDAO.getAttachesByBno(num);

//업로드 기준경로
String uploadFolder = "C:/ksw/upload";

// 첨부파일 삭제하기
for (AttachVO attach : attachList) {
	String path = uploadFolder + "/" + attach.getUploadpath() + "/" + attach.getFilename();
	File deleteFile = new File(path);
	
	if (deleteFile.exists()) { // 삭제할 파일이 해당경로에 존재하면
		deleteFile.delete();   // 파일 삭제하기
	} // if
	
	if (attach.getFiletype().equals("I")) { // 이미지 파일이면
		String thumbnailPath = uploadFolder + "/" + attach.getUploadpath() + "/s_" + attach.getFilename();
		File thumbnailFile = new File(thumbnailPath);
		
		if (thumbnailFile.exists()) { // 썸네일 이미지파일 존재하면
			thumbnailFile.delete(); // 썸네일 이미지파일 삭제하기
		}
	} // if
} // for

// DB 첨부파일 정보 삭제하기
attachDAO.deleteAttachesByBno(num);

// DB 게시글 정보 삭제하기
boardDAO.deleteBoardByNum(num);

// 글목록 boardList.jsp로 이동
response.sendRedirect("/board/boardList.jsp?pageNum=" + pageNum);
%>






