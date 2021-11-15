<%@page import="java.io.BufferedInputStream"%>
<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
//===== download.jsp 페이지는 이미지를 제외한 다른 일반 파일 요청에서 실행됨. =====

// http://localhost:8090/board/download.jsp?fileName=2021/08/03/수업세부일정표.xlsx

// 파일경로가 포함된 파일명을 파라미터로 가져오기
String fileName = request.getParameter("fileName"); // "2021/08/03/수업세부일정표.xlsx"

File file = new File("C:/ksw/upload", fileName); // "C:/ksw/upload/2021/08/03/수업세부일정표.xlsx"

if (file.exists() == false) { // 파일이 없으면 종료
	return;
}

String contentType = Files.probeContentType(file.toPath());
//컨텐트타입(Mime타입) 정보가 없으면 application/octet-stream 로 설정하기
if (contentType == null) {
	contentType = "application/octet-stream";
}
System.out.println("contentType : " + contentType);
// 응답객체에 컨텐트타입 설정
response.setContentType(contentType);


int beginIndex = fileName.lastIndexOf("/") + 1;
String fname = fileName.substring(beginIndex); // 경로를 제외한 파일명 구하기
System.out.println("fname : " + fname);

// 다운로드 파일명의 문자셋을 "utf-8"에서 "iso-8859-1"로 변환하기
fname = new String(fname.getBytes("utf-8"), "iso-8859-1");
// 다운로드로 사용할 파일명을 응답헤더에 설정
response.setHeader("Content-Disposition", "attachment; filename=" + fname);


// 읽어들일(다운로드할) 파일을 파일입력스트림 객체로 준비
BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));

// 요청 클라이언트 쪽으로 보낼 출력(응답) 스트림 준비
ServletOutputStream os = response.getOutputStream();

// 사용자 출력스트림으로 파일 내보내기.
int data;
while ((data = is.read()) != -1) {
	os.write(data);
}

// 입출력 스트림 닫기
is.close();
os.close();
%>
    
    
    
    
    
    
    
    