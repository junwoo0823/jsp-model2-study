<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h1>파이 그래프 - 성별 회원수 (Ajax)</h1>
<hr>

<button type="button" id="btn">버튼</button>

<div>
	<canvas id="myChart" width="400" height="300"></canvas>
</div>


<script src="/resources/js/jquery-3.6.0.js"></script>
<script src="/resources/js/chart.min.js"></script>
<script>
// 버튼 클릭하면 파이차트 표시하기
$('#btn').on('click', function () {
	
	requestChartData();
});

function requestChartData() {
	$.ajax({
		url: '/api/chart/gender-per-count',
		method: 'GET',
		success: function (data) {
			console.log(typeof data); // object
			console.log(data);
			
			drawChart(data.labelList, data.dataList);
		}
	});
} // requestChartData


function drawChart(labelList, dataList) {
	// 차트를 그릴 캔버스 객체 가져오기
	var canvas = document.getElementById('myChart');
	// 캔버스 객체에 그림을 그릴 컨텍스트 객체 가져오기
	var context = canvas.getContext('2d');
	// 파이 차트 그리기
	var myChart = new Chart(context, {
		type: 'pie',
		data: {
			labels: labelList, // 차트 레이블 적용
			datasets: [
				{
					data: dataList, // 차트 데이터 적용
					backgroundColor: ['green', 'yellow'],
					hoverBackgroundColor: ['lightgreen', 'lightyellow']
				}
			]
		},
		options: {
			responsive: false
		}
	});
} // drawChart

</script>
</body>
</html>




