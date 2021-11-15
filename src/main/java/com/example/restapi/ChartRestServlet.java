package com.example.restapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.repository.MemberDAO;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/chart/*")
public class ChartRestServlet extends HttpServlet {
	
	private static final String BASE_URI = "/api/chart";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		System.out.println("requestURI : " + requestURI);
		
		String str = requestURI.substring(BASE_URI.length());
		str = str.substring(1); // 맨 앞에 슬래시(/) 제거
		System.out.println("str = " + str);
		
		if (str.equals("gender-per-count")) {
			printGenderPerCount(request, response);
		}
		
	} // doGet
	
	
	private void printGenderPerCount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// http://localhost:8090/api/chart/gender-per-count
		
		MemberDAO memberDAO = MemberDAO.getInstance();

		List<Map<String, Object>> list = memberDAO.getGenderPerCount();

		// chart.js에서 사용될수 있도록 데이터를 가공하기

		List<String> labelList = new ArrayList<>(); // 레이블을 담을 리스트 준비
		List<Integer> dataList = new ArrayList<>(); // 데이터를 담을 리스트 준비

		for (Map<String, Object> map : list) {
			
			labelList.add((String) map.get("genderName"));
			dataList.add((Integer) map.get("cnt"));
		} // for

		// Gson 객체 준비
		Gson gson = new Gson();
		
		Map<String, Object> map = new HashMap<>(); // { labelList: ['남성','여성'], dataList: [2,1] }
		map.put("labelList", labelList);
		map.put("dataList", dataList);
		
		String strJson = gson.toJson(map);
		System.out.println("strJson : " + strJson);
		
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(strJson);
		out.flush();
	} // printGenderPerCount
}





