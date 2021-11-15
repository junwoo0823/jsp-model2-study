<%@page import="com.example.domain.BoardVO"%>
<%@page import="net.coobird.thumbnailator.Thumbnailator"%>
<%@page import="java.util.UUID"%>
<%@page import="com.example.domain.AttachVO"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.example.repository.BoardDAO"%>
<%@page import="com.example.repository.AttachDAO"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
<%@page import="java.io.IOException"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%! // 선언부

// 년/월/일 폴더명 생성하는 메소드
String getFolder() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // "yyyy-MM-dd"
	Date date = new Date();
	String str = sdf.format(date);
	//str = str.replace("-", File.separator);
	return str;
}

// 이미지 파일인지 여부 리턴하는 메소드
boolean checkImageType(File file) {
	boolean isImage = false;
	try {
		String contentType = Files.probeContentType(file.toPath());
		System.out.println("contentType : " + contentType); // "image/jpg"
		
		isImage = contentType.startsWith("image");
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	return isImage;
}
%>


<%
String uploadFolder = "C:/ksw/upload"; // 업로드 기준경로

File uploadPath = new File(uploadFolder, getFolder()); // "C:/ksw/upload/2021/08/03"
System.out.println("uploadPath : " + uploadPath.getPath());

if (uploadPath.exists() == false) {
	uploadPath.mkdirs();
}


// MultipartRequest 인자값
// 1. request
// 2. 업로드할 물리적 경로.  "C:/ksw/upload"
// 3. 업로드 최대크기 바이트 단위로 제한. 1024Byte * 1024Byte = 1MB 
// 4. request의 텍스트 데이터, 파일명 인코딩 "utf-8"
// 5. 파일명 변경 정책. 파일명 중복시 이름변경규칙 가진 객체를 전달

// 파일 업로드하기
MultipartRequest multi = new MultipartRequest(
		request
		, uploadPath.getPath()
		, 1024 * 1024 * 50
		, "utf-8"
		, new DefaultFileRenamePolicy());
// ===== 파일 업로드 완료됨. =====





// AttachDAO 객체 준비
AttachDAO attachDAO = AttachDAO.getInstance();
// BoardDAO 객체 준비
BoardDAO boardDAO = BoardDAO.getInstance();

//수정할 게시글 번호 파라미터 가져오기
int num = Integer.parseInt(multi.getParameter("num"));




// =============== 신규 첨부파일 정보를 테이블에 insert하기 ===============

// input type="file" 태그들의 name 속성들을 가져오기
Enumeration<String> enu = multi.getFileNames(); // Iterator, Enumeration 반복자 객체

while (enu.hasMoreElements()) { // 파일이 있으면
	String fname = enu.nextElement(); // name 속성값 : file0  file1  file2 ...
	
	// 저장된 파일명 가져오기
	String filename = multi.getFilesystemName(fname); // fname이 file0일때
	System.out.println("FilesystemName : " + filename);
	
	// 원본 파일명 가져오기
	String original = multi.getOriginalFileName(fname);
	System.out.println("OriginalFileName : " + original);
	
	if (filename == null) { // 파일정보가 없으면
		continue; // 그다음 반복으로 건너뛰기
	}
	
	// AttachVO 객체 준비
	AttachVO attachVO = new AttachVO();
	
	attachVO.setFilename(filename);
	attachVO.setUploadpath(getFolder());
	attachVO.setBno(num); // 첨부파일이 포함될 게시글 번호 저장
	
	UUID uuid = UUID.randomUUID();
	attachVO.setUuid(uuid.toString()); // 기본키 uuid 저장
	
	File file = new File(uploadPath, filename); // 년월일 경로에 실제 파일명의 파일객체
	
	boolean isImage = checkImageType(file); // 이미지 파일 여부 확인
	attachVO.setFiletype( (isImage == true) ? "I" : "O" ); // Image 또는 Other
	
	// 이미지 파일이면 썸네일 이미지 생성하기
	if (isImage == true) { 
		File outFile = new File(uploadPath, "s_" + filename); // 생성(출력)할 썸네일 파일정보
		
		Thumbnailator.createThumbnail(file, outFile, 100, 100); // 썸네일 생성하기
	}
	
	
	// 첨부파일 attach 테이블에 attachVO를 insert하기
	attachDAO.addAttach(attachVO);
} // while

//=============== 신규 첨부파일 정보를 테이블에 insert하기 완료 ===============
	
	
	

//=============== 삭제할 첨부파일 정보를 삭제하기 ===============

String[] delFileUuids = multi.getParameterValues("delfile");

for (String uuid : delFileUuids) {
	// 첨부파일 uuid에 해당하는 첨부파일객체를 VO로 가져오기
	AttachVO attach = attachDAO.getAttachByUuid(uuid);
	
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
	
	// DB에서 uuid에 해당하는 첨부파일정보를 삭제하기
	attachDAO.deleteAttachByUuid(uuid);
} // for
//=============== 삭제할 첨부파일 정보를 삭제하기 완료 ===============





//=============== 게시글 수정하기 ===============
// 수정에 사용할 게시글 VO 객체 준비
BoardVO boardVO = new BoardVO();
		
// 파라미터값 가져와서 VO에 저장
boardVO.setNum(num);
boardVO.setSubject(multi.getParameter("subject"));
boardVO.setContent(multi.getParameter("content"));
boardVO.setIpaddr(request.getRemoteAddr());

// DB에 게시글 수정하기
boardDAO.updateBoard(boardVO);
//=============== 게시글 수정하기 완료 ===============





String pageNum = multi.getParameter("pageNum");


// 글목록 화면으로 이동
//response.sendRedirect("/board/boardList.jsp?pageNum=" + pageNum);

// 상세보기 화면으로 이동
//response.sendRedirect("/board/boardContent.jsp?num=" + num + "&pageNum=" + pageNum);
%>
<script>
	alert('글 수정 성공');
	location.href = '/board/boardContent.jsp?num=<%=num %>&pageNum=<%=pageNum %>';
</script>








