<%@page import="org.apache.commons.io.IOUtils"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
// ===== display.jsp 페이지는 이미지 파일 요청에서 실행됨. =====

// http://localhost:8090/board/display.jsp?fileName=2021/08/03/s_apple.jpg

// 파일경로가 포함된 파일명을 파라미터로 가져오기
String fileName = request.getParameter("fileName"); // "2021/08/03/s_apple.jpg"

File file = new File("C:/ksw/upload", fileName); // "C:/ksw/upload/2021/08/03/s_apple.jpg"

if (file.exists() == false) { // 파일이 없으면 종료
	return;
}

String contentType = Files.probeContentType(file.toPath());
System.out.println("contentType : " + contentType); // "image/jpeg"  "image/png"  "image/gif"

// 응답객체에 컨텐트타입 설정
response.setContentType(contentType);

// 읽어들일 이미지 파일을 파일입력스트림 객체로 준비
FileInputStream is = new FileInputStream(file);

// 파일입력스트림을 바이트 배열로 변환
byte[] image = IOUtils.toByteArray(is);

// 요청 클라이언트 쪽으로 보낼 출력(응답) 스트림
ServletOutputStream os = response.getOutputStream();
// 사용자 출력스트림으로 이미지파일 내보내기
os.write(image);

//입출력 스트림 닫기
is.close();
os.close();
%>



