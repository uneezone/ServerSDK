//'use strict';

/*
 * Constant values of resolution
 */
const BaseInfo = Object.freeze({
	AUTO : "auto",
	FHD : "fhd",
	FHD_SQUARE : "fhd_square",
	HD: "hd",
	HD_SQUARE: "hd_square",
	VGA : "vga",
	fhdW : 1920,
	fhdH : 1080,
	hdW : 1280,
	hdH : 720,
	vgaW : 640,
	vgaH : 480,
	cardFHDWidth : 960,
	cardFHDHeight : 604
});

const ScanType = Object.freeze({
    // CREDIT_CARD : 0,
    ID_AUTO : 1,
//    ID_NATIONAL : 2,
//    ID_DRIVER : 3,
    ALIEN_REG : 4,
    ALIEN_BACK: 11,
    PASSPORT : 5,
    properties : {
        1 : {name : "id-auto", value: 1},
        4 : {name : "id-alien", value: 4},
        5 : {name : "passport", value: 5},
        11 : {name : "id-alien-back", value: 11},
    }
});

const OsType = Object.freeze({
	MOBILE : {name : "Mobile", regex : /Mobi/},
	WINDOWS : {name : "Windows", regex : /Windows/i},
	MAC : {name : "Mac OS", regex : /Mac OS/i},
	ANDROID : {name : "Android", regex : /Android/i},
	IOS_IPAD : {name : "iPad", regex : /iPad/i},
	IOS_IPHONE : {name : "iPhone", regex : /iPhone OS/i}
});

const BrowserType = Object.freeze({
	SAMSUNG : {name : "SamsungBrowser", regex: /SamsungBrowser/i},
	CHROME : {name : "Chrome", regex: /Chrome/i},
	SAFARI : {name : "Safari", regex: /Safari/i},
	FIREFOX : {name : "Firefox", regex: /Firefox/i},
	OPERA : {name : "Opera", regex: / OPR/i},
	EDGE : {name : "Edge", regex: /Edge/i}
});

/*
 * This object provides options for CCR
 * maxFrames : number of frames to capture
 * intervalTime : time interval of each frame capture
 * quality : quality of captured JPG image ( 0 ~ 100 )
 * timeout : deprecated
 * detectEdge : if support WebGL2, detect card edges before capture frame
 * resMode : resolution mode (BaseInfo.VGA, BaseInfo.FHD, default value is BaseInfo.FHD)
 */
const Params = {
	maxFrames: 1,
	intervalTime: 100,
	quality: 90,
	timeout: 100000, // millisecond
	detectEdge: false,
	resMode: BaseInfo.AUTO,
	debug: false,
};

/*
 * browser information object
 * type : browser type (chrome, safari, samsung browser)
 * version : version of browser
 * supportWebRTC : true, false // legacy state, ('no' : cannot support webrtc, 'partial' : partially support webrtc, 'full' : fully support webrtc)
 */
const BrowserInfo = (function () {
	const agent = navigator.userAgent;
	let supportWebRTC = false;

	return {
		// parse browser version
		getVersion: (browser) => {
			let version = '';
			let idx = agent.indexOf(browser);
			if (idx !== -1) {
				for (let i = idx + browser.length + 1; i < agent.length; i++) {
					let char = agent[i];
					if (char !== ' ') {
						version += char;
					} else {
						break;
					}
				}
				let verSplit = version.split(".");
				if (verSplit.length === 1) {
					version = Number(verSplit[0]);
				} else if (verSplit.length >= 2) {
					version = Number(verSplit[0] + "." + verSplit[1])
				}
			} else {
				version = -1;
			}
			return version;
		},
		// make simple browser information
		getInfo: () => {
			//alert(agent);
			if (agent.match(OsType.IOS_IPHONE.regex) /* || agent.match(OsType.IOS_IPAD)*/) {
				if (agent.match(/Version/)) {
					return {
						platform: OsType.IOS_IPHONE.name,
						type: BrowserType.SAFARI.name,
						version: BrowserInfo.getVersion("Version")
					}
					
				}
				else if (agent.match(/CriOS/)) {
					return {
						platform: OsType.IOS_IPHONE.name,
						type: BrowserType.CHROME.name,
						version: BrowserInfo.getVersion("CriOS")
					}
				}
				else {
					return null;
				}
			} else if (agent.match(OsType.ANDROID.regex)) {
				if (agent.indexOf(BrowserType.SAMSUNG.name) !== -1) { // is samsung browser?
					return {
						platform: OsType.ANDROID.name,
						type: BrowserType.SAMSUNG.name,
						version: BrowserInfo.getVersion(BrowserType.SAMSUNG.name)
					}
				} else if (typeof window.chrome !== 'undefined' && !!window.chrome
					&& agent.indexOf(BrowserType.CHROME.name) !== -1) { // check google chrome
					return {
						platform: OsType.ANDROID.name,
						type: BrowserType.CHROME.name,
						version: BrowserInfo.getVersion(BrowserType.CHROME.name)
					}
				} else if (agent.indexOf(BrowserType.FIREFOX.name) !== -1) { // Firefox
					return {
						platform: OsType.ANDROID.name,
						type: BrowserType.FIREFOX.name,
						version: BrowserInfo.getVersion(BrowserType.FIREFOX.name)
					}
				} else {
					return null;
				}
			} else if (agent.match(OsType.WINDOWS.regex)) {
				if (agent.indexOf(BrowserType.EDGE.name) !== -1) { // Edge
					return {
						platform: OsType.WINDOWS.name,
						type: BrowserType.EDGE.name,
						version: BrowserInfo.getVersion(BrowserType.EDGE.name)
					}
				} else if (typeof window.chrome !== 'undefined' && !!window.chrome
					&& agent.indexOf(BrowserType.CHROME.name) !== -1) { // Chrome
					return {
						platform: OsType.WINDOWS.name,
						type: BrowserType.CHROME.name,
						version: BrowserInfo.getVersion(BrowserType.CHROME.name)
					}
				} else if (agent.indexOf(BrowserType.FIREFOX.name) !== -1) { // Firefox
					return {
						platform: OsType.WINDOWS.name,
						type: BrowserType.FIREFOX.name,
						version: BrowserInfo.getVersion(BrowserType.FIREFOX.name)
					}
				} else {
					return null;
				}
			} else if (agent.match(OsType.MAC.regex)) {
				if (agent.match(BrowserType.SAFARI.regex)) {
					return {
						platform: OsType.MAC.name,
						type: BrowserType.SAFARI.name,
						version: BrowserInfo.getVersion(BrowserType.SAFARI.name)
					}
				} else {
					return null;
				}
			}
		},
		// check WebRTC supported version
		checkWebRTCSupport: (browserInfo) => {
			const chromeVer = 53;
			const samsungVer = 7; // versions between 4 and 6.2 has a bug : video resolution is forced to a square, the bug is fixed at version 7.
			const safariVer = 11;
			const firefoxVer = 60;

			if (browserInfo) {
				switch (browserInfo.type) {
					case BrowserType.SAFARI.name:
						supportWebRTC = browserInfo.version >= safariVer;
						break;
					case BrowserType.SAMSUNG.name:
						supportWebRTC = browserInfo.version >= samsungVer;
						break;
					case BrowserType.CHROME.name:
						supportWebRTC = browserInfo.version >= chromeVer;
						break;
					case BrowserType.FIREFOX.name:
						supportWebRTC = browserInfo.version >= firefoxVer;
						break;
					default:
						supportWebRTC = false;
				}
			}

			return supportWebRTC;
		},
		isSupportWebRTC: () => {
			return supportWebRTC;
		},
	}
})();

const Layout = (function () {
	const backColor = "rgba(200, 200, 200, 0.3)";
	const strokeColor = "rgba(0, 0, 0, 0.8)";
	const strokeColorB = "rgba(64, 255, 64, 0.8)";
	let strokeLen = 50;
	let strokeWidth = 15;

	let cardLX;
	let cardTY;
	let cardRX;
	let cardBY;
	let cardW;
	let cardH;
	let w;
	let h;

	let guideRectMarginPercentPortrait = 0.1;
	let guideRectMarginPercentLandscape = 0.6;
	return {
		// call this function first !
		init: (screenWidth, screenHeight) => {
			w = screenWidth;
			h = screenHeight;

			let cardRatio = BaseInfo.cardFHDHeight / BaseInfo.cardFHDWidth;
			let cardW = 0;
			if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
				cardW = w * (1-guideRectMarginPercentLandscape);
			}
			else
			{
				cardW = w * (1-guideRectMarginPercentPortrait);
			}
			let cardH = cardW * cardRatio;

			cardLX = (w - cardW) / 2;
			cardRX = cardLX + cardW;
			cardTY = (h - cardH) / 2;
			cardBY = cardTY + cardH;
		},
		/*
		 * Draw guide for card recognition region
		 */
		drawCardGuide: (showGuide, state) => {
			let canvas = document.getElementById('overlay_canvas');
			if (canvas && canvas.getContext) {
				canvas.width = w;
				canvas.height = h;
				if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
					strokeLen = 15;
					strokeWidth = 5;
				}
				else{
					 strokeLen = 50;
					 strokeWidth = 15;
				}
				let ctx = canvas.getContext('2d');

				// clear before draw
				ctx.clearRect(0, 0, w, h);

				// draw translucent region
				ctx.fillStyle = backColor;
				ctx.fillRect(0, 0, w, h);

				ctx.clearRect(cardLX, cardTY, cardW, cardH);

				// draw card recognition frame
				if (showGuide)
					ctx.strokeStyle = strokeColor;
				else
					ctx.strokeStyle = strokeColorB;

				ctx.lineWidth = strokeWidth;
				ctx.beginPath();

				// draw Left-Top
				ctx.moveTo(cardLX + strokeLen, cardTY);
				ctx.lineTo(cardLX, cardTY); // horizontal
				ctx.lineTo(cardLX, cardTY + strokeLen); // vertical

				// draw Right-Top
				ctx.moveTo(cardRX - strokeLen, cardTY);
				ctx.lineTo(cardRX, cardTY); // horizontal
				ctx.lineTo(cardRX, cardTY + strokeLen); // vertical

				// draw Left-Bottom
				ctx.moveTo(cardLX + strokeLen, cardBY);
				ctx.lineTo(cardLX, cardBY); // horizontal
				ctx.lineTo(cardLX, cardBY - strokeLen); // vertical

				// draw Right-Bottom
				ctx.moveTo(cardRX - strokeLen, cardBY);
				ctx.lineTo(cardRX, cardBY); // horizontal
				ctx.lineTo(cardRX, cardBY - strokeLen); // vertical
				ctx.stroke();
				if (state === 1) {
					ctx.beginPath();
					ctx.strokeStyle = "rgba(64,255,64,0.8)";

					ctx.lineWidth = strokeWidth;
					//ctx.beginPath();

					// draw top
					ctx.moveTo(cardLX + strokeLen, cardTY);
					ctx.lineTo(cardRX - strokeLen, cardTY);

					// draw left
					ctx.moveTo(cardLX, cardTY + strokeLen);
					ctx.lineTo(cardLX, cardBY - strokeLen);

					// draw top
					ctx.moveTo(cardLX + strokeLen, cardBY);
					ctx.lineTo(cardRX - strokeLen, cardBY);

					// draw right
					ctx.moveTo(cardRX, cardTY + strokeLen);
					ctx.lineTo(cardRX, cardBY - strokeLen);
					ctx.stroke();
				}
			}
		},
		/*
		 * Positioning button and text layout
		 */
		positionLayout: () => {
			if (cardLX && cardTY && cardRX && cardBY) {
				// upper text layout
				let textLayout = document.getElementById('progress_overlay');
				let textLayoutTop = cardTY - textLayout.offsetHeight;
				textLayout.style.top = textLayoutTop + "px";
				textLayout.hidden = true;
				if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
					// scan mode layout


					let scanModeLayout = document.getElementById('scan_mode_layout');
					// scanModeLayout.style.top = (textLayoutTop - scanModeLayout.offsetHeight - 50) + "px";
					scanModeLayout.style.top = (cardTY - scanModeLayout.offsetHeight) + "px";
					let btn_recog_mode = document.querySelectorAll('.btn_recog_mode');

					for (var i = 0; i < btn_recog_mode.length; i++) {
						btn_recog_mode[i].style.fontSize = "0.8em";
						btn_recog_mode[i].style.padding = "0.8em";
						btn_recog_mode[i].style.opacity = '0.8';
					}
					// device selector layout
					// let deviceSelectorLayout = document.getElementById('devices_selector_layout');
					// deviceSelectorLayout.style.top = (textLayoutTop - deviceSelectorLayout.offsetHeight - 150) + "px";

					// center text layout
					
					let guide_text = document.getElementById('help_text');
					guide_text.style.fontSize = "1em";
					let procTextLayout = document.getElementById('center_text_overlay');
					procTextLayout.style.top = cardTY + "px";
					procTextLayout.style.left = cardLX + "px";
					procTextLayout.style.width = (cardRX - cardLX) + "px";
					procTextLayout.style.height = (cardBY - cardTY) + "px";
					procTextLayout.style.lineHeight = (cardBY - cardTY) + "px";
					procTextLayout.style.verticalAlign = "middle";

					// help text layout
					let helpTextLayout = document.getElementById('help_overlay');
					helpTextLayout.style.top = (cardTY + 50) + "px";


					let chk_info = document.getElementById('chk_info');
					chk_info.style.fontSize = "1em";
					chk_info.style.width = "100px";
					chk_info.style.height = "100px";
					chk_info.style.opacity = '0.4';
					let auto_snap = document.getElementById('auto_snap');
					auto_snap.style.fontSize = "1em";
					auto_snap.style.width = "100px";
					auto_snap.style.height = "100px";
					auto_snap.style.opacity = '0.4';
					// button layout
					let btnLayout = document.getElementById('btn_overlay_gui');
					btnLayout.style.top = (cardBY - 150) + "px";

					// processing circle layout
					let waitContainer = document.getElementById('wait_container');
					waitContainer.style.top = cardTY + "px";
					waitContainer.style.left = cardLX + "px";
					waitContainer.style.width = (cardRX - cardLX) + "px";
					waitContainer.style.height = (cardBY - cardTY) + "px";
				}
				else{
				// scan mode layout


				let scanModeLayout = document.getElementById('scan_mode_layout');
				// scanModeLayout.style.top = (textLayoutTop - scanModeLayout.offsetHeight - 50) + "px";
				scanModeLayout.style.top = (cardTY - scanModeLayout.offsetHeight - 50) + "px";
					let btn_recog_mode = document.querySelectorAll('.btn_recog_mode');

					for (var i = 0; i < btn_recog_mode.length; i++) {
						btn_recog_mode[i].style.fontSize = "2em";
						btn_recog_mode[i].style.padding = "1em";
						btn_recog_mode[i].style.opacity = '0.8';
					}
				// device selector layout
				// let deviceSelectorLayout = document.getElementById('devices_selector_layout');
				// deviceSelectorLayout.style.top = (textLayoutTop - deviceSelectorLayout.offsetHeight - 150) + "px";

				// center text layout
					let guide_text = document.getElementById('help_text');
					guide_text.style.fontSize = "2.5em";
				let procTextLayout = document.getElementById('center_text_overlay');
				procTextLayout.style.top = cardTY + "px";
				procTextLayout.style.left = cardLX + "px";
				procTextLayout.style.width = (cardRX - cardLX) + "px";
				procTextLayout.style.height = (cardBY - cardTY) + "px";
				procTextLayout.style.lineHeight = (cardBY - cardTY) + "px";
				procTextLayout.style.verticalAlign = "middle";

				// help text layout
				let helpTextLayout = document.getElementById('help_overlay');
				helpTextLayout.style.top = (cardBY + 10) + "px";

				// button layout
					let chk_info = document.getElementById('chk_info');
					chk_info.style.fontSize = "2em";
					chk_info.style.width = "200px";
					chk_info.style.height = "200px";
					chk_info.style.opacity = '0.4';
					let auto_snap = document.getElementById('auto_snap');
					auto_snap.style.fontSize = "2em";
					auto_snap.style.width = "200px";
					auto_snap.style.height = "200px";
					auto_snap.style.opacity = '0.4';
				let btnLayout = document.getElementById('btn_overlay_gui');
				btnLayout.style.top = (cardBY + 10) + "px";
					

				// processing circle layout
				let waitContainer = document.getElementById('wait_container');
				waitContainer.style.top = cardTY + "px";
				waitContainer.style.left = cardLX + "px";
				waitContainer.style.width = (cardRX - cardLX) + "px";
				waitContainer.style.height = (cardBY - cardTY) + "px";
			}
			}
		},
		showCenterText: (show) => {
			let procTextLayout = document.getElementById('center_text_overlay');
			let guidTextLayout = document.getElementById('help_overlay');
			if (show === true) {
				procTextLayout.hidden = false;
				guidTextLayout.hidden = true;
			} else {
				procTextLayout.hidden = true;
				guidTextLayout.hidden = false;
			}
		},
		changeProcessText: (state) => {
			let text = document.getElementById('process_text');
			switch (state) {
				case "start":
					text.innerHTML = "준비 중...";
					break;
				case "recog":
					text.innerHTML = "인식 중...";
					break;
				case "idle":
					text.innerHTML = "대기 중...";
					break;
				default:
					text.innerHTML = state;
			}
		},
		changeRunningState: (isRunning) => {
			if (typeof isRunning === 'boolean') {
				$('#recog_mode_selector').prop("disabled", isRunning);
				$('#auto_snap').text(isRunning ? "인식 중지" : "인식 시작");
			}
		},
		showProcessing: (show) => {
			let waitContainer = document.getElementById('wait_container');
			waitContainer.style.opacity = '0.4';
			waitContainer.hidden = !show;
		}
	}
})();

const Capture = (function () {
	let cardEdgeRegion;
	let frameList;
	let frameByteSize = 0;
	let captureST;
	let captureET;
	let reqCaptureCnt = 0; // 캡쳐 요청 수
	let capturedCnt = 0; // 캡쳐된 개수
	let sendCnt = 0; // 전송한 프레임 수
	let browserInfo;
	let maxNumOfFrame; // 프레임 최대 개수

	const reqBaseUrl = "/scan";

	let captureAbort = true;

	let serverResponseFrameIndex = 0;

	return {
		initScreen: (bInfo, maxFrame) => {
			/*
			 * calculate card crop region and sobel region
			 * initialize variables
			 */

			if (typeof maxFrame !== "number" || maxFrame <= 0) {
				maxFrame = 1;
			}
			if (maxFrame > Params.maxFrames) {
				maxFrame = Params.maxFrames;
			}
			maxNumOfFrame = maxFrame;

			if (typeof cardEdgeRegion === "undefined" || !cardEdgeRegion) {
				let cardRatio = BaseInfo.cardFHDWidth / BaseInfo.cardFHDHeight;
				const padding = 1.1;
				const addPadding = 2; // additional padding
				const edgeW = 0.2;

				let w = BaseInfo.fhdW;
				let h = BaseInfo.fhdH;
				if(window.supportedConstraints) {
                    w = window.supportedConstraints.video.width.exact;
                    h = window.supportedConstraints.video.height.exact;
				} else {
				    return false;
				}

				let cropH = Math.ceil(h / cardRatio * padding) + (addPadding * 2);
				let thick = Math.ceil(h * edgeW);
				let yOffset = Math.ceil((w - cropH) / 2);

				let regionT = 0;
				let regionB = regionT + cropH;
				let regionL = 0;
				let regionR = h;
				const overlapped = thick * 2;

				cardEdgeRegion = {};

				cardEdgeRegion.Offset = {x: 0, y: yOffset};
				cardEdgeRegion.Left = {x: regionL, y: regionT + thick, width: thick, height: cropH - overlapped};
				cardEdgeRegion.Top = {x: regionL + thick, y: regionT, width: h - overlapped, height: thick};
				cardEdgeRegion.Right = {
					x: regionR - thick,
					y: regionT + thick,
					width: thick,
					height: cropH - overlapped
				};
				cardEdgeRegion.Bottom = {x: regionL + thick, y: regionB - thick, width: h - overlapped, height: thick};

				cardEdgeRegion.Entire = {x: regionL, y: regionT, width: h, height: cropH};
			}
			browserInfo = bInfo;
			frameList = [];
			frameByteSize = 0;
			captureST = 0;
			captureET = 0;
			reqCaptureCnt = 0;
			capturedCnt = 0;

			return true;
		},
		captureFrame: (width, height, captureCallback) => {
			let captST = new Date().getTime();
			let captResult = {};
			captResult.captured = false;

			let canvas = document.createElement('canvas');
			let video = document.querySelector('video');
			let ctx = canvas.getContext('2d');

			if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
				canvas.width = width;
				canvas.height = height;

				ctx.save();
				ctx.drawImage(video, 0, 0, width, height);
			}
			else
			{
				canvas.width = height;
				canvas.height = width;

				ctx.save();				
				ctx.drawImage(video, 0, 0, height, width);
			}

			// canvas.width = width;
			// canvas.height = height;

			// ctx.save();
			// if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
			// 	ctx.drawImage(video, 0, 0, width, height);
			// }
			// else
			// {
			// 	ctx.rotate(DegToRad(-90));

			// 	let track = stream.getVideoTracks()[0];

			// 	// let drawCanvasST = new Date().getTime();
			// 	if (typeof track.getSettings === 'undefined' && browserInfo.type === 'Chrome') { // old version
			// 		ctx.drawImage(video, -height, (width - height) / 2);
			// 	} else {
			// 		let setting = track.getSettings();
			// 		if (setting.width !== setting.height) {
			// 			ctx.drawImage(video, -height, 0);
			// 		} else {
			// 			ctx.drawImage(video, -height, (width - height) / 2);
			// 		}
			// 	}
			// }


			ctx.restore();

			// let drawCanvasTime = new Date().getTime() - drawCanvasST;

			Layout.showProcessing(true);

			// capture one frame
			let blobST = new Date().getTime();
			canvas.toBlob(function (blob) {
				const blobTime = new Date().getTime() - blobST;
				// console.log('save to blob time :', blobTime);
				window.snapTimeList.push(blobTime); // canvas to blob time test
				captureCallback(blob);
			}, 'image/jpeg', (Params.quality / 100));

			captResult.time = new Date().getTime() - captST;

			return captResult;
		},
		scanFrame: (frame) => {
		    if(!frame) {
		        Layout.showProcessing(false);
                Capture.recogDone();
                alert("failed to capture screen.");
                return;
		    }

			const reqUrl = reqBaseUrl + "/" + ScanType.properties[window.scanMode].name;

			let formData = new FormData();
			formData.append("files", frame);
			var startTime = new Date();
			$.ajax({
				type: "POST",
				enctype: "multipart/form-data",
				url: reqUrl,
				data: formData,
				processData: false,
				contentType: false,
				async: true,
				timeout: Params.timeout,
				success: function (data) {
					// Ajax 요청이 완료된 후 시간 측정
					var endTime = new Date();

					// 소요된 시간 계산 (밀리초 단위)
					var elapsedTime = endTime - startTime;

					let ret = JSON.stringify(data);
					//alert(ret);
					if(data != null && data.complete == true)
					{	
						if(data.result_scan_type === "RESIDENT_REGISTRATION")
						{
							var result = null;
							result = "SSA : " + data.id.id_truth  + "\n";
							result += "이름 : " + data.id.name  + "\n";
							result += "주민등록번호 : " + data.id.jumin  + "\n";
							result += "발행일 : " + data.id.issued_date  + "\n";
							result += "발급처 : " + data.id.region  + "\n";
							result += "인식시간 : " + elapsedTime  + "\n";
							alert(result);
						}

						else if(data.result_scan_type === "DRIVER_LICENSE")
						{
							var result = null;
							result = "SSA : " + data.id.id_truth  + "\n";
							result += "이름 : " + data.id.name  + "\n";
							result += "주민등록번호 : " + data.id.jumin  + "\n";
							result += "면허종류 : " + data.id.driver_license.driver_type  + "\n";
							result += "운전면허번호 : " + data.id.driver_license.driver_number  + "\n";
							result += "발행일 : " + data.id.issued_date  + "\n";
							result += "발급처 : " + data.id.region  + "\n";
							result += "시리얼 : " + data.id.driver_license.driver_serial + "\n";
							result += "인식시간 : " + elapsedTime  + "\n";
							alert(result);
						}
						else if(data.result_scan_type === "PASSPORT")
						{
							var result = null;
							result = "SSA : " + data.id.id_truth  + "\n";
							result += "한글이름 : " + data.id.passport.name_kor  + "\n";
							result += "주민등록번호 : " + data.id.jumin  + "\n";
							result += "이름 : " + data.id.name  + "\n";
							result += "영어성 : " + data.id.passport.sur_name  + "\n";
							result += "영어이름 : " + data.id.passport.given_name + "\n";
							result += "여권종류 : " + data.id.passport.passport_type + "\n";
							result += "발행국가 : " + data.id.passport.issuing_country  + "\n";
							result += "여권번호 : " + data.id.passport.passport_number  + "\n";	
							result += "국가 : " + data.id.passport.nationality  + "\n";	
							result += "생년월일 : " + data.id.passport.birthday + "\n";							
							result += "성별 : " + data.id.passport.sex  + "\n";
							result += "만료일 : " + data.id.passport.expiry_date  + "\n";
							result += "개인번호 : " + data.id.passport.personal_number + "\n";
							result += "발행일 : " + data.id.issued_date  + "\n";
							result += "MRZ1 : " + data.id.passport.mrz1  + "\n";
							result += "MRZ2 : " + data.id.passport.mrz2  + "\n";
							result += "인식시간 : " + elapsedTime  + "\n";
							alert(result);
						}
						else if(data.result_scan_type === "ALIEN_REGISTRATION")
						{
							var result = null;
							result = "SSA : " + data.id.id_truth  + "\n";
							result += "이름 : " + data.id.name  + "\n";
							result += "등록번호 : " + data.id.jumin  + "\n";
							result += "발행일 : " + data.id.issued_date  + "\n";
							result += "발급처 : " + data.id.region  + "\n";
							result += "인식시간 : " + elapsedTime  + "\n";
							alert(result);
						}
						
					}
					else
					{
						alert("complete : false");
					}

				},
				complete: function () {
					Layout.showProcessing(false);
					Capture.recogDone();
				},
				error: function (xhr, status, e) {
					alert("error: " + e);
				}
			});
		},
		isRunning: () => {
			return !captureAbort;
		},
		reset: () => {
			frameList = [];
			frameByteSize = 0;
			sendCnt = 0;
			reqCaptureCnt = 0;
			capturedCnt = 0;
			captureST = 0;
			captureET = 0;
			serverResponseFrameIndex = 0;
		},
		recogDone: (reason) => {
			captureAbort = true;
			Layout.showProcessing(false);
			Layout.changeProcessText("idle");
			Layout.changeRunningState(false);
			if (Params.debug) {
				let msg = "reason: " + (reason ? reason : "") + "time: "+window.snapTimeList;
				alert(msg);
			}
		}
	}
})();

function DegToRad(angle){
	return angle * Math.PI/180;
}

function resetAutoFocus() {
	let track = stream.getVideoTracks()[0];
	if(track && track.applyConstraints) {
		track.applyConstraints({advanced:[{focusMode:"manual"}]})
			.then( () => track.applyConstraints({advanced:[{focusMode:"continuous"}]}) )
			.then( () => console.log("complete focus reset") );
	} else {
		alert('해당 버전에서는 포커스 리셋 기능을 사용할 수 없습니다.');
	}
}

function initParams() {
	if( typeof Params == 'undefined' || ! Params ) {
		window.Params = {};
	}

	// try {
	// 	Params.maxFrames = Number(document.getElementById('maxFrame').value);
	// } catch(err) {
	// }
	// try {
	// 	Params.intervalTime = Number(document.getElementById('capInterval').value);
	// } catch(err) {
	// }
	// try {
	// 	Params.quality = Number(document.getElementById('quality').value);
	// } catch(err) {
	// }
	// try {
	// 	Params.timeout = Number(document.getElementById('timeout').value);
	// } catch(err) {
	// }
	// try {
	// 	Params.detectEdge = Number(document.getElementById('detectEdge').value) === 1 ? true : false;
	// } catch(err) {
	// }
	// try {
	// 	Params.resMode = document.getElementById('resolutionMode').value;
	// } catch(err) {
	// }
	// try {
	// 	Params.debug = Number(document.getElementById('debug').value) === 1 ? true : false;
	// } catch(err) {
	// }
}

var overlayObserver = new ResizeObserver(function(entries) {
	entries.forEach(function(entry) {
	  // offsetWidth 변경 시 처리
	  console.log("overlayObserver 값이 변경되었습니다.");
  	let overlay = document.querySelector('div.overlay');
	var w = overlay.offsetWidth;
	var h = overlay.offsetHeight;

	console.log('w: ' + w);
	console.log('h: ' + h);

	Layout.init(w, h);
	Layout.drawCardGuide(true, 0);
	Layout.positionLayout();
	Layout.changeProcessText("idle");

	});
  });

  var posionObserver = new ResizeObserver(function(entries) {
	entries.forEach(function(entry) {
	  // offsetWidth 변경 시 처리
	  console.log("posionObserver 값이 변경되었습니다.");
	let scanModeLayout = document.getElementById('scan_mode_layout');
	var w = scanModeLayout.offsetWidth;
	var h = scanModeLayout.offsetHeight;

	console.log('scanModeLayout w: ' + w);
	console.log('scanModeLayout h: ' + h);

	Layout.positionLayout();

	});
  });

async function initialize(callback) {
	initParams();

	let overlay = document.querySelector('div.overlay');
	let scanModeLayout = document.getElementById('scan_mode_layout');

	// MutationObserver 시작

	// if (window.orientation == -90 || window.orientation == 90 || window.orientation == undefined) {
	// 	let video = document.querySelector('video');
	// 	video.style.width = "70%"; // 1280
	// 	video.style.height = "70%"; // 720
	// 	video.style.verticalAlign = "middle";


	// 	overlay.style.width = "70%"; // 1280
	// 	overlay.style.height = "70%"; // 720
	// 	overlay.style.verticalAlign = "middle";
	// }
	// else{
	// 	let video = document.querySelector('video');
	// 	video.style.width = "100%"; // 1280
	// 	video.style.height = "100%"; // 720
	// 	video.style.verticalAlign = "middle";

	// 	overlay.style.width = "100%"; // 1280
	// 	overlay.style.height = "100%"; // 720
	// 	overlay.style.verticalAlign = "middle";
	// }
	
	overlayObserver.observe(overlay);
	posionObserver.observe(scanModeLayout);
	var w = overlay.offsetWidth;
	var h = overlay.offsetHeight;

	console.log('w: ' + w);
	console.log('h: ' + h);
	
	Layout.init(w, h);

	window.video = document.querySelector('video');

	$('#auto_snap').click(function() {
		toggleRecogState();
	});

	$('#reset_focus').click(function() {
		resetAutoFocus();
	});

	registerRecogModeSelector();

	let msg = null;

	let bInfo = BrowserInfo.getInfo();

	if( bInfo ) {
		/*
		 * safari 브라우저 video 초기화
		 */
		//if( bInfo && bInfo.type === 'Safari' ) 
		{
			window.video = document.querySelector('video');
			video.setAttribute("playsinline", "true");
			video.setAttribute("controls", "true");
			setTimeout(() => {
				video.removeAttribute("controls");
			});
		}

		let supportWebRTC = BrowserInfo.checkWebRTCSupport( bInfo );

		if( supportWebRTC ) {
			window.browserInfo = bInfo;

			// updateDeviceList(selectMainCamera);
		} else {
			msg = '지원되지 않는 버전의 브라우저입니다.\n최신 버전으로 업데이트 해주세요.';
		}
	} else {
		msg = '사용중인 기기에서 지원되지 않는 브라우저입니다.';
	}

	window.supportedConstraints = await detectSupportedResolutions();

	if(typeof msg !== "undefined" && msg) {
		alert(msg);
	}

	if(callback) {
	    callback();
	}
}

function registerRecogModeSelector() {
	window.scanMode = ScanType.ID_AUTO;
    $('#recog_mode_selector :input').change(function() {
        let checkedVal = Number($(this).val());
        if( ! ScanType.properties[checkedVal] ) {
            checkedVal = ScanType.ID_AUTO;
        }
        window.scanMode = checkedVal;
    });
}

function toggleRecogState() {

	if(Capture.isRunning() === false) {
		window.snapTimeList = [];

		Layout.changeProcessText("recog");
		Layout.changeRunningState(true);

		let scanW, scanH;
		if(window.supportedConstraints && 'video' in window.supportedConstraints) {
		    scanW = window.supportedConstraints.video.width.exact;
		    scanH = window.supportedConstraints.video.height.exact;
		} else {
		    scanW = BaseInfo.fhdW;
		    scanH = BaseInfo.fhdH;
		}

		Capture.reset();

		Layout.changeRunningState(true);

		Capture.captureFrame(scanW, scanH, Capture.scanFrame);

	} else {
		Capture.recogDone("abort - already running");
	}
}

function startCamera() {
	const bInfo = window.browserInfo;

	if(typeof bInfo === "undefined" || !bInfo ||
		!('platform' in bInfo) || !('type' in bInfo)) {
			alert("브라우저 정보를 얻을 수 없어서 카메라 초기화에 실패했습니다.")
		return;
	}

    let constraints = window.supportedConstraints;

	// constraints for debugging
	if(Params.debug) {
		constraints = {
			video : {
				facingMode: "environment",
				width: {exact: 1280},
				height: {exact: 720}
			}
		}
	}

	if(typeof constraints === "undefined") {
		alert("카메라 초기화 실패.\n지원되지 않는 기기이거나 지원되지 않는 브라우저 입니다.");
		return;
	}

	Capture.initScreen(bInfo, Params.maxFrames); // calculate card edge region

	function gotStream(stream) {
		window.stream = stream;
		video.srcObject = stream;
		stream.oninactive = function() {
			console.log('Stream inactive');
		};

		if(Params.debug) {
			let track = stream.getVideoTracks()[0];
			alert('video width :'+track.getSettings().width+'\nvideo height :'+track.getSettings().height);
		}
	}

	function getMedia(constraints) {
		if( window.stream ) {
			window.stream.getTracks().forEach(function(track) {
				track.stop();
			});
		}

		if(typeof constraints === "undefined") {
			alert("카메라 초기화 실패.");
			return;
		}

		navigator.mediaDevices.getUserMedia(constraints)
			.then(gotStream)
			.catch(function(e) {
				console.warn('getUserMedia', e.name,', msg : '+e.message);
				//alert("error name : "+e.name +", message : "+e.message);
			});
	}

	try {
		getMedia(constraints);
	} catch(e) {
		alert("비디오 초기화 실패");
	}

	Layout.drawCardGuide(true, 0);
	Layout.positionLayout();
	Layout.changeProcessText("idle");
}

function stopStreamedVideo() {
	const stream = video.srcObject;
	if (stream !== null) {
		const tracks = stream.getTracks();
	
		tracks.forEach(function(track) {
		track.stop();
		});
	
		video.srcObject = null;
	}

}



window.onload = function() {
	initialize(startCamera).then(
	    () => {
            $('#chk_info').click(function() {
                let obj = {};
                obj.binfo = window.browserInfo;
                obj.agent = navigator.userAgent;

                alert(JSON.stringify(obj));
            });
        }
	);
}

window.addEventListener("orientationchange", function () {

	
}, false);

window.addEventListener('beforeunload', (event) => {
	stopStreamedVideo();
	event.preventDefault();
});


