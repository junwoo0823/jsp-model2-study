package com.example.restapi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.domain.AttachVO;
import com.example.domain.BoardVO;
import com.example.domain.Criteria;
import com.example.repository.AttachDAO;
import com.example.repository.BoardDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import net.coobird.thumbnailator.Thumbnailator;

@WebServlet(
		urlPatterns = {"/api/boards/*"}, 
		loadOnStartup = 1)
public class BoardRestServlet extends HttpServlet {
	
	private static final String BASE_URI = "/api/boards";
	
	private BoardDAO boardDAO = BoardDAO.getInstance();
	private AttachDAO attachDAO = AttachDAO.getInstance();
	
	private Gson gson;
	
	public BoardRestServlet() {
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// GET 조회
		// "/api/boards/{bno}" -> 상세글보기
		// "/api/boards/pages/{page}" -> 페이지번호에 해당하는 글목록 가져오기
		
		String requestURI = request.getRequestURI();
		String str = requestURI.substring(BASE_URI.length() + 1);
		
		String strJson = "";
		
		if (str.startsWith("pages")) { // 페이징 글목록 조회
			
			int beginIndex = str.indexOf("/") + 1;
			String strPage = str.substring(beginIndex); // "2"
			int page = Integer.parseInt(strPage);  // 2
			
			
			// 글목록 가져오기 조건객체 준비
			Criteria cri = new Criteria(); // 기본값 1페이지 10개
			cri.setPageNum(page); // 요청 페이지번호 설정
			
			List<BoardVO> boardList = boardDAO.getBoards(cri);
			
			strJson = gson.toJson(boardList); // [{}, {}, {}, ...]
			
		} else { // 글번호 해당하는 상세글보기
			
			int bno = Integer.parseInt(str);
			
			BoardVO boardVO = boardDAO.getBoard(bno);
			List<AttachVO> attachList = attachDAO.getAttachesByBno(bno);
			
			Map<String, Object> map = new HashMap<>();
			map.put("board", boardVO);
			map.put("attachList", attachList);
			
			strJson = gson.toJson(map);
		}
		
		System.out.println(strJson);
		
		sendResponse(response, strJson);
	} // doGet

	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// POST 등록
		// "/api/boards/new" -> 주글 등록
		// "/api/boards/reply" -> 답글 등록
		
		String requestURI = request.getRequestURI();
		String type = requestURI.substring(BASE_URI.length() + 1);
		
		if (type.equals("new")) {
			writeNewBoard(request, response); // 새로운 주글쓰기
		} else if (type.equals("reply")) {
			//writeReplyBoard(request, response); // 새로운 답글쓰기
		}
	} // doPost

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// PUT 수정
		// "/api/boards/{bno}" + 수정내용 -> 특정 글 수정하기
		
		String requestURI = request.getRequestURI();
		String bno = requestURI.substring(BASE_URI.length() + 1);
		int num = Integer.parseInt(bno); // 수정할 게시글 번호
		
		
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
		
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		
		String strJson = gson.toJson(map);
		System.out.println(strJson);
		
		sendResponse(response, strJson);
	} // doPut

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// DELETE 삭제
		// "/api/boards/{bno}" -> 특정 글 삭제하기
		
		String requestURI = request.getRequestURI();
		String bno = requestURI.substring(BASE_URI.length() + 1);
		int num = Integer.parseInt(bno);
		
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
		boardDAO.deleteBoardByNum(num);
		// DB 게시글 정보 삭제하기
		attachDAO.deleteAttachesByBno(num);
		
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		
		String strJson = gson.toJson(map);
		System.out.println(strJson);
		
		sendResponse(response, strJson);
	} // doDelete
	
	
	private void sendResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	} // sendResponse
	
	
	
	// 새로운 주글쓰기 메소드
	private void writeNewBoard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
		
		// =======================================================
		
		BoardVO dbBoard = boardDAO.getBoard(num);
		List<AttachVO> attachList = attachDAO.getAttachesByBno(num);
		
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		map.put("board", dbBoard); // JSON에서 {}
		map.put("attachList", attachList); // JSON에서 []
		
		String strJson = gson.toJson(map);
		System.out.println(strJson);
		
		sendResponse(response, strJson);
		
	} // writeNewBoard
	
	
	
	// 년/월/일 폴더명 생성하는 메소드
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // "yyyy-MM-dd"
		Date date = new Date();
		String str = sdf.format(date);
		//str = str.replace("-", File.separator);
		return str;
	} // getFolder

	// 이미지 파일인지 여부 리턴하는 메소드
	private boolean checkImageType(File file) {
		boolean isImage = false;
		try {
			String contentType = Files.probeContentType(file.toPath());
			System.out.println("contentType : " + contentType); // "image/jpg"
			
			isImage = contentType.startsWith("image");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isImage;
	} // checkImageType
	
}





