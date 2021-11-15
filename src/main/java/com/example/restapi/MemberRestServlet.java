package com.example.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.domain.MemberVO;
import com.example.repository.MemberDAO;
import com.example.util.MemberDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/api/members/*")
public class MemberRestServlet extends HttpServlet {
	
	private static final String BASE_URI = "/api/members";
	
	private MemberDAO memberDAO = MemberDAO.getInstance();
	
	private Gson gson;
	
	public MemberRestServlet() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(MemberVO.class, new MemberDeserializer());
		gson = builder.create();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// "/api/members"      -> member테이블 전체 레코드 정보 요청
		// "/api/members/aaa"  -> member테이블에 아이디가 aaa인 레코드 한개 정보 요청
		String requestURI = request.getRequestURI();
		System.out.println("requestURI : " + requestURI);
		
		String id = requestURI.substring(BASE_URI.length());
		System.out.println("id = " + id);
		
		String strJson = "";
		if (id.length() == 0) { // "/api/members"
			
			List<MemberVO> memberList = memberDAO.getMembers();

			// 자바객체 -> JSON 문자열로 변환 (직렬화)
			strJson = gson.toJson(memberList);
			
			// [ {}, {}, {} ]
			
		} else { // "/api/members/aaa"
			id = id.substring(1); // 맨앞에 "/" 문자 제거
			
			MemberVO memberVO = memberDAO.getMemberById(id); // null 리턴할수도 있음
			int count = memberDAO.getCountById(id); // 항상 숫자 리턴함
			
			System.out.println("memberVO : " + memberVO);
			System.out.println("count : " + count);
			
			Map<String, Object> map = new HashMap<>();
			map.put("member", memberVO);
			map.put("count", count);
			
			// 자바객체 -> JSON 문자열로 변환 (직렬화)
			strJson = gson.toJson(map);
			
			// {}
		}
		
		System.out.println(strJson);
		
		// 클라이언트 쪽으로 출력하기
		sendResponse(response, strJson);
	} // doGet

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// "/api/members"
		System.out.println("doPost 호출됨");
		
		// application/json 형식의 데이터를 받을때
		// HTTP 메시지 바디를 직접 읽어와야 함
		BufferedReader reader = request.getReader();
		
		// HTTP 메시지 바디 영역으로부터 JSON 문자열 읽어오기
		String strJson = readMessageBody(reader);
		System.out.println("JSON 문자열 : " + strJson);
		
		// JSON 문자열 -> 자바객체로 변환 (역직렬화)
		MemberVO memberVO = gson.fromJson(strJson, MemberVO.class);
		System.out.println(memberVO);
		
		// insert 회원등록하기
		memberDAO.insert(memberVO);
		
		// 응답 데이터 준비
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		map.put("member", memberVO);
		
		// 자바객체 -> JSON 문자열로 변환 (직렬화)
		String strResponse = gson.toJson(map); // {}
		System.out.println(strResponse);
		
		// 클라이언트 쪽으로 출력하기
		sendResponse(response, strResponse);
	} // doPost

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// "/api/members/aaa" + 수정할 데이터
		
		String requestURI = request.getRequestURI();
		String id = requestURI.substring(BASE_URI.length());
		id = id.substring(1); // 맨앞에 "/" 문자 제거
		System.out.println("id = " + id);
		
		// 수정할 데이터는 JSON 형식의 문자열로 가져오기
		BufferedReader reader = request.getReader();
		
		String strJson = readMessageBody(reader);
		System.out.println("JSON 문자열 : " + strJson);
		
		// JSON 문자열 -> 자바객체로 변환 (역직렬화)
		MemberVO memberVO = gson.fromJson(strJson, MemberVO.class);
		
		memberVO.setId(id); // 수정할 기준 아이디 설정
		
		// 회원정보 수정하기
		memberDAO.updateById(memberVO);
		// 응답정보로 보낼 수정된 회원정보 가져오기
		MemberVO updatedMember = memberDAO.getMemberById(id);
		
		// 응답 데이터 준비
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		map.put("member", updatedMember);
		
		// 자바객체 -> JSON 문자열로 변환 (직렬화)
		String strResponse = gson.toJson(map); // {}
		System.out.println(strResponse);
		
		// 클라이언트 쪽으로 출력하기
		sendResponse(response, strResponse);
	} // doPut
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// "/api/members/aaa"
		
		String requestURI = request.getRequestURI();
		String id = requestURI.substring(BASE_URI.length());
		id = id.substring(1); // 맨앞에 "/" 문자 제거
		System.out.println("id = " + id);
		
		MemberVO delMember = memberDAO.getMemberById(id);
		
		// 응답 데이터 준비
		Map<String, Object> map = new HashMap<>();
		
		if (delMember != null) {
			memberDAO.deleteById(id); // 회원 삭제하기
			map.put("isDeleted", true);
			map.put("member", delMember);
		} else { // delMember == null
			map.put("isDeleted", false);
		}
		
		// 자바객체 -> JSON 문자열로 변환 (직렬화)
		String strResponse = gson.toJson(map); // {}
		System.out.println(strResponse);
		
		// 클라이언트 쪽으로 출력하기
		sendResponse(response, strResponse);
	} // doDelete
	
	
	private String readMessageBody(BufferedReader reader) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		} // while
		
		return sb.toString();
	} // readMessageBody
	
	
	private void sendResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	} // sendResponse
	
}







