package com.example.email;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.SimpleEmail;

@WebServlet("/email/simple-mail")
public class SimpleEmailServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 요청 파라미터 값 가져오기
		String receiver = request.getParameter("receiver"); // "aa@a.com, bb@b.com, ..."
		String[] receivers = receiver.split(","); // 받는사람 배열타입(여러명일수 있음)
		
		String subject = request.getParameter("subject"); // 메일 제목
		String msg = request.getParameter("msg"); // 메일 내용
		
		
		// 메일전송기능 라이브러리 : commons-email 라이브러리
		
		// SimpleEmail 클래스 : 텍스트 메일 전송 용도

		// MultiPartEmail 클래스 : 텍스트 메시지와 파일을 함께 전송 용도
		//   EmailAttachment 클래스 : 첨부파일 정보 표현
		
		// HtmlEmail 클래스 : 이메일 내용을 HTML 문서 형식으로 전송 용도
		
		
		long beginTime = System.currentTimeMillis(); // 시작시간
		
		// SimpleEmail 객체 생성
		SimpleEmail email = new SimpleEmail();
		
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
		sb.append("    location.href = '/email/simpleEmail.jsp';");
		sb.append("</script>");
		
		out.print(sb.toString());
		out.flush();
	} // doPost
}







