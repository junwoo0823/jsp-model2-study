<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>선 그래프 연습</h1>
<hr>

<canvas id="myChart" width="400" height="300"></canvas>


<script src="/resources/js/chart.min.js"></script>
<script>
// 차트를 그릴 캔버스 객체 가져오기
var canvas = document.getElementById('myChart');
// 캔버스 객체에 그림을 그릴 컨텍스트 객체 가져오기
var context = canvas.getContext('2d');

// 라인 차트 그리기
var myChart = new Chart(context, {
	type: 'line',
	data: {
		labels: ['국어', '영어', '수학'],
		datasets: [
			{
				label: '철수',
				data: [80, 72, 66],
				backgroundColor: ['red'],
				borderColor: ['black'],
				borderWidth: 2
			},
			{
				label: '영희',
				data: [77, 88, 92],
				backgroundColor: ['blue'],
				borderColor: ['black'],
				borderWidth: 2
			}
		]
	},
	options: {
		responsive: false,
		scales: {
			y: {
				type: 'linear',
				min: 0,
				//max: 200
			}
		}
	}
});
</script>
</body>
</html>




