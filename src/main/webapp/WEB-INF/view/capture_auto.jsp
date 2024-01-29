<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
    <META HTTP-EQUIV="EXPIRES" CONTENT="Mon, 22 Jul 2002 00:00:00 GMT">
    <title>Card Scanner</title>

    <script src="webjars/jquery/3.3.1-1/jquery.min.js" type="text/javascript"></script>
    <script src="webjars/bootstrap/4.0.0-1/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="https://webrtc.github.io/adapter/adapter-latest.js" type="text/javascript"></script>
    <script src="/js/utils/support.js" type="text/javascript"></script>
    <script src="/js/capture_auto.js" type="text/javascript"></script>


    <link href="webjars/bootstrap/4.0.0-1/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <link href="/css/main.css" rel="stylesheet" type="text/css" />
    <link href="/css/anim.css" rel="stylesheet" type="text/css" />

</head>
<body>
<div id="unsupported_container" hidden="true">

</div>
<div id="container">
    <div class="video_wrapper"> <!-- video container -->
        <video autoplay></video>
    </div>
    <div class="overlay"> <!-- overlay container -->
        <canvas id="overlay_canvas" class="overlay"></canvas>


        <div id="progress_overlay">
            <span id="process_text" class="guide_text"></span>
        </div>
        <div id="wait_container" hidden>
            <div id="progress_graphic" class="loader">
                Processing
            </div>
        </div>
        <div id="help_overlay">
            <span id="help_text" class="guide_text"><b>가이드 선에 카드를 맞춰주세요.</b></span>
        </div>

        <div id="center_text_overlay" hidden="true">
            <span id="center_text" class="big_guide_text">처리 중</span>
        </div>
    </div>

</div>

<input type="hidden" id="maxFrame" type="number" value="${maxFrame}">
<input type="hidden" id="capInterval" type="number" value="${captureInterval}">
<input type="hidden" id="quality" type="number" value="${qp}">
<input type="hidden" id="timeout" type="number" value="${timeout}">
<input type="hidden" id="detectEdge" type="number" value="${detectEdge}">
<input type="hidden" id="resolutionMode" value="${resolution}">
<input type="hidden" id="debug" type="number" value="${debug}">

<div id="dpi"></div>
<script>
    // 토글 버튼 요소 선택
    // var modeButton = document.getElementById('modeButton');
    // modeButton.style.opacity = '0.7';
    var recog_id = document.getElementById('recog_id');
    var recog_passport = document.getElementById('recog_passport');
    var recog_alien = document.getElementById('recog_alien');
    var recog_alien_back = document.getElementById('recog_alien_back');
    // 버튼 클릭 시 상태 전환 함수
    // 			modeButton.addEventListener('click', function() {
    // 			// 버튼의 상태 확인
    // 			var isActive = modeButton.getAttribute('data-active') === 'true';

    // 			// // 상태에 따른 처리
    // 			// if (isActive) {
    // 			// 	// 비활성 상태로 전환
    // 			// 	modeButton.removeAttribute('data-active');
    // 			// 	modeButton.style.opacity = '0.7';
    // 			// 	$("#recog_mode_selector").addClass("d-none");

    // 			// 	// 추가적인 처리 가능
    // 			// } else {
    // 			// 	// 활성 상태로 전환
    // 			// 	modeButton.setAttribute('data-active', 'true');
    // 			// 	modeButton.style.opacity = '1';
    // 			// 	$("#recog_mode_selector").removeClass("d-none");

    // 			// 	// 추가적인 처리 가능
    // 			// }
    // });
</script>
</body>
</html>