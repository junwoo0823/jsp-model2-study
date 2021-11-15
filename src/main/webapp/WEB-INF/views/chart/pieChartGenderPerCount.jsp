<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.example.repository.MemberDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
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
		
String strLabel = gson.toJson(labelList);
String strData = gson.toJson(dataList);

System.out.println("strLabel : " + strLabel); // ["남성","여성"]
System.out.println("strData : " + strData);   // [2,1]
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>파이 그래프 - 성별 회원수</h1>
<hr>

<canvas id="myChart" width="400" height="300"></canvas>


<script src="/resources/js/chart.min.js"></script>
<script>
// 차트를 그릴 캔버스 객체 가져오기
var canvas = document.getElementById('myChart');
// 캔버스 객체에 그림을 그릴 컨텍스트 객체 가져오기
var context = canvas.getContext('2d');

// 파이 차트 그리기
var myChart = new Chart(context, {
	type: 'pie',
	data: {
		labels: JSON.parse('<%=strLabel %>'), // JSON.parse('["남성","여성"]')
		datasets: [
			{
				data: JSON.parse('<%=strData %>'), // JSON.parse('[2,1]')
				backgroundColor: ['green', 'yellow'],
				hoverBackgroundColor: ['lightgreen', 'lightyellow']
			}
		]
	},
	options: {
		responsive: false
	}
});
</script>
</body>
</html>




