<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>파이 그래프 연습</h1>
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
		labels: ['치킨', '피자', '햄버거'],
		datasets: [
			{
				data: [10, 5, 9],
				backgroundColor: ['blue', 'green', 'yellow'],
				hoverBackgroundColor: ['lightblue', 'lightgreen', 'lightyellow']
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




