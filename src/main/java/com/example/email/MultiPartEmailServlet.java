package com.example.email;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/email/multipart-mail")
public class MultiPartEmailServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
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
		
		
		// 요청 파라미터 값 가져오기
		String receiver = multi.getParameter("receiver"); // "aa@a.com, bb@b.com, ..."
		String[] receivers = receiver.split(","); // 받는사람 배열타입(여러명일수 있음)
		
		String subject = multi.getParameter("subject"); // 메일 제목
		String msg = multi.getParameter("msg"); // 메일 내용
		String filename = multi.getFilesystemName("file"); // 업로드된 파일명


		// MultiPartEmail 클래스 : 텍스트 메시지와 파일을 함께 전송 용도
		//   EmailAttachment 클래스 : 첨부파일 정보 표현
		
		long beginTime = System.currentTimeMillis(); // 시작시간
		
		// 첨부파일 EmailAttachment 객체 생성
		EmailAttachment attach = new EmailAttachment();
		// 경로상에 한글이 있으면 에러가 발생하므로 유의
		attach.setPath(uploadPath.getPath() + "/" + filename);
		attach.setDescription("파일 설명글");
		attach.setName("");
		
		
		// MultiPartEmail 객체 생성
		MultiPartEmail email = new MultiPartEmail();
		
		// SMTP 서버 연결설정
		email.setHostName("smtp.naver.com");
		email.setSmtpPort(465); // 기본포트  465(SSL)  587(TLS)
		email.setAuthentication("zencoding", "******");
		
		// SMTP  SSL, TLS 활성화 설정
		email.setSSLOnConnect(true);
		email.setStartTLSEnabled(true);
		
		String message = "fail";
		
		try {
			// 보내는 사람 설정. 제약사항: 보내는사람은 로그인한 아이디와 동일한 계정이 되어야 함.
			email.setFrom("zencoding@naver.com", "관리자", "utf-8");
			
			// 받는사람 설정
//			email.addTo("zencoding@daum.net", "김상우", "utf-8");
//			email.addTo("zencoding@daum.net", "김상우", "utf-8");
//			email.addTo("zencoding@daum.net", "김상우", "utf-8");
			for (String emailAddr : receivers) {
				email.addTo(emailAddr.trim());
			} // for
			
			// 받는사람(참조인) 설정
			//email.addCc("zencoding@hanmail.net", "김상우", "utf-8");
			
			// 받는사람(숨은참조인) 설정
			//email.addBcc("zencoding@hanmail.net", "김상우", "utf-8");
			
			
			// 제목 설정
			email.setSubject(subject);
			// 본문 설정
			email.setMsg(msg);
			
			// 첨부파일 정보 추가
			email.attach(attach);
			
			// 메일 전송
			message = email.send();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis(); // 종료시간
		
		long execTime = endTime - beginTime;
		System.out.println("execTime : " + execTime);
		
		System.out.println("message : " + message);
		
		
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<script>");
		sb.append("    alert('메일 전송 성공! 전송시간: " + execTime + "ms message : " + message + "');");
		sb.append("    location.href = '/email/multiPartEmail.jsp';");
		sb.append("</script>");
		
		out.print(sb.toString());
		out.flush();
	} // doPost
	
	
	
	
	// 년/월/일 폴더명 생성하는 메소드
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // "yyyy-MM-dd"
		Date date = new Date();
		String str = sdf.format(date);
		//str = str.replace("-", File.separator);
		return str;
	} // getFolder
	
}







