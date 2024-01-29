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
    <script src="/js/capture.js" type="text/javascript"></script>


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
        <!--  통합 인식일 시 메뉴 d-none -->
        <div id="scan_mode_layout" class="d-none">
            <span class="mode_text btn_recog_mode">인식 모드</span>
            <!-- <button id="modeButton" class="btn btn-primary btn-sm ">인식 모드</button> -->
            <div id="recog_mode_selector" class="btn-group btn-group-toggle" data-toggle="buttons">
                <label class="btn btn-primary btn-sm btn_recog_mode active">
                    <input type="radio"
                           name="recog_mode" id="recog_id" value="1" autocomplete="off" checked> 신분증 </label>
                <label class="btn btn-primary btn-sm btn_recog_mode">
                    <input type="radio"
                           name="recog_mode" id="recog_passport" value="5" autocomplete="off"> 여권 </label>
                <label class="btn btn-primary btn-sm btn_recog_mode">
                    <input type="radio"
                           name="recog_mode" id="recog_alien" value="4" autocomplete="off"> 외국인등록증 </label>
                <label class="btn btn-primary btn-sm btn_recog_mode">
                    <input type="radio"
                           name="recog_mode" id="recog_alien_back" value="11" autocomplete="off"> 외국인 뒷면 </label>
            </div>
        </div>

        <%--			<div id="devices_selector_layout">--%>
        <%--				<span class="mode_text">장치 선택</span>--%>
        <%--				<select class="btn btn-primary btn-lg btn_recog_mode" id="videoSource"></select>--%>
        <%--			</div>--%>

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
        <div id="btn_overlay_gui"> <!-- layout of control buttons -->
            <%--				<button id="reset_focus" class="btn btn-success btn-circle"><b>포커스</b><br><b>초기화</b></button>--%>
            <button id="chk_info" class="btn btn-success btn-circle"><b>정보</b><br><b>확인</b></button>
            <button id="auto_snap" class="btn btn-success btn-circle"><b>인식 시작</b></button>
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