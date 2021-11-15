<%@page import="net.coobird.thumbnailator.Thumbnailator"%>
<%@page import="java.io.IOException"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.util.UUID"%>
<%@page import="com.example.domain.AttachVO"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.example.repository.AttachDAO"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.example.repository.BoardDAO"%>
<%@page import="com.example.domain.BoardVO"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
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


// enctype="multipart/form-data" 로 전송받으면
// 기본내장객체인 request로부터 파라미터값을 바로 가져올수 없음! -> null이 리턴됨.
// MultipartRequest 객체로부터 파라미터값을 가져와야 함. 사용방법은 request 와 동일함.


// AttachDAO 객체 준비
AttachDAO attachDAO = AttachDAO.getInstance();
// BoardDAO 객체 준비
BoardDAO boardDAO = BoardDAO.getInstance();

// insert할 새 게시글 번호 가져오기
int num = boardDAO.getNextnum(); // attach 레코드의 bno 컬럼값에 해당함


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


// BoardVO 객체 준비
BoardVO boardVO = new BoardVO();

// 파라미터값 가져와서 VO에 저장. MultipartRequest 로부터 가져옴.
boardVO.setMid(multi.getParameter("id"));
boardVO.setSubject(multi.getParameter("subject"));
boardVO.setContent(multi.getParameter("content"));

// 글번호 설정
boardVO.setNum(num);
// ipaddr  regDate  readcount
boardVO.setIpaddr(request.getRemoteAddr()); // 127.0.0.1  localhost
boardVO.setRegDate(new Timestamp(System.currentTimeMillis()));
boardVO.setReadcount(0); // 조회수

// 주글에서  re_ref  re_lev  re_seq  설정하기
boardVO.setReRef(num);  // 주글일때는 글번호와 글그룹번호는 동일함
boardVO.setReLev(0);  // 주글은 들여쓰기 레벨이 0 (들여쓰기 없음)
boardVO.setReSeq(0);  // 주글은 글그룹 안에서 순번이 0 (re_ref 오름차순 정렬시 첫번째)

// 주글 등록하기
boardDAO.addBoard(boardVO);

// 글목록 화면으로 이동
//response.sendRedirect("/board/boardList.jsp");


// 요청 페이지번호 파라미터 가져오기
String pageNum = multi.getParameter("pageNum"); 

// 글상세보기 화면으로 이동
response.sendRedirect("/board/boardContent.jsp?num=" + boardVO.getNum() + "&pageNum=" + pageNum);
%>






