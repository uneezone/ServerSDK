/*
 *
 * Support functions for WebRTC
 * 
 */
window.RES_LIST = {
    FHD         : {width : 1920, height: 1080},
    FHD_SQUARE  : {width : 1920, height: 1920},
    HD          : {width : 1280, height : 720},
    HD_SQUARE   : {width : 1280, height : 1280},
    VGA         : {width : 640,	 height : 480},
};

async function detectSupportedResolutions(/*videoSource*/) {
	
	let detectResST = new Date().getTime();

	window.video = document.querySelector('video');
	
	let camMode = "environment";
	let videoDevice = await getDevices();
	let const_gen = getGenerator(videoDevice, camMode, RES_LIST)();

	let next_val = const_gen.next();
	while( ! next_val.done ) {
		let constraints = next_val.value;
		let result = await checkResolution(constraints);
		// console.log('resolution :', key, ', result :', result);
		await sleep(10);
		if( result ) {
			return constraints;
		}

		next_val = const_gen.next();
	}
	
	let detectResET = new Date().getTime();
	console.log('detect resolution time :',detectResET - detectResST);
}

function isBrowserCheck(){ 
	const agt = navigator.userAgent.toLowerCase(); 
	if (agt.indexOf("chrome") != -1) return 'Chrome'
	if (agt.indexOf("opera") != -1) return 'Opera'
	if (agt.indexOf("staroffice") != -1) return 'Star Office'
	if (agt.indexOf("webtv") != -1) return 'WebTV'
	if (agt.indexOf("beonex") != -1) return 'Beonex'
	if (agt.indexOf("chimera") != -1) return 'Chimera'
	if (agt.indexOf("netpositive") != -1) return 'NetPositive'
	if (agt.indexOf("phoenix") != -1) return 'Phoenix'
	if (agt.indexOf("firefox") != -1) return 'Firefox'
	if (agt.indexOf("safari") != -1) return 'Safari'
	if (agt.indexOf("skipstone") != -1) return 'SkipStone'
	if (agt.indexOf("netscape") != -1) return 'Netscape'
	if (agt.indexOf("mozilla/5.0") != -1) return 'Mozilla'
	if (agt.indexOf("msie") != -1) { 
		let rv = -1; 
		if (navigator.appName == 'Microsoft Internet Explorer') { 
			let ua = navigator.userAgent; var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})")
		if (re.exec(ua) != null) 
			rv = parseFloat(RegExp.$1)
		} 
		return 'Internet Explorer '+rv
	} 
}
function getDevices() {
	return navigator.mediaDevices.enumerateDevices().then(devices => {	

	var videoDevices = [0, 0];
	var videoDeviceIndex = 0;
	devices.forEach(function (device) {
		console.log(device.kind + ": " + device.label + " id = " + device.deviceId);
		if (device.kind == "videoinput") {
			videoDevices[videoDeviceIndex++] = device.deviceId;
			console.log("setupVideo  device.deviceId: " + device.deviceId);
		}
	});
	
	if(isBrowserCheck() ==='Firefox')
	{
		if (videoDeviceIndex === 1) {
			cid = 1;
		}
		else if (videoDeviceIndex === 2) {
			cid = 2;
		}
		else if (videoDeviceIndex === 3) {
			cid = 3;
		}
		else if (videoDeviceIndex === 4) {
			cid = 4;
		}
		else if (videoDeviceIndex === 5) {
			cid = 5;
		}
		else if (videoDeviceIndex === 6) {
			cid = 6;
		}
		else {
			cid = 0;
		}
	}
	else
	{
		if (videoDeviceIndex === 1) {
			cid = 0;
		}
		else if (videoDeviceIndex === 2) {
			cid = 1;
		}
		else if (videoDeviceIndex === 3) {
			cid = 2;
		}
		else if (videoDeviceIndex === 4) {
			cid = 3;
		}
		else if (videoDeviceIndex === 5) {
			cid = 4;
		}
		else if (videoDeviceIndex === 6) {
			cid = 5;
		}
		else {
			cid = 0;
		}
	}

	return videoDevices[cid];

	}, reject => {return null;});
}



function getGenerator(videoDevice, camMode, resolution_list) {

	if( navigator.mediaDevices && navigator.mediaDevices.getUserMedia ) {
		// if( (camMode === "environment" || camMode === "user" ) && resolution_list )
		if(resolution_list)
		{
			return function *constraintsGen() {
				for(let key in resolution_list ) {
					yield {
						video : {
							facingMode: camMode,
							focusMode: 'continuous',
							// deviceId: videoSource ? {exact: videoSource} : undefined,
							width: {exact: resolution_list[key].width},
							height: {exact: resolution_list[key].height},
							deviceId: videoDevice
						}
					}
				}
			}
		}
	}

}

function checkResolution(constraints) {
	return navigator.mediaDevices.getUserMedia(constraints)
		.then(resolve => {
		        let stream = resolve;
		        if(stream) {
                    stream.getTracks().forEach(function (track) {
                        track.stop();
                    });
                }

		        return true;
		    }, reject => {return false;});
}

function sleep(ms) {
    return new Promise(resolve=>setTimeout(resolve, ms));
}
