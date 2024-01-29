<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="/webapp/css/style.css">
<link rel="stylesheet" href="/webapp/css/anim.css.css">
<link rel="stylesheet" href="/webapp/css/main.css.css">

<script src="/webapp/js/capture.js"></script>
<script src="/webapp/js/capture_auto.js.js"></script>
<script src="/webapp/js/utils/support.js"></script>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <link href="/css/style.css" rel="stylesheet" type="text/css" />
    <title>Quram WebOCR for ID</title>
</head>
<body>
<div class="wrapper">
    <h2 class="title">Quram WebOCR for ID</h2>
    <form class="container" action="scan/id_auto" enctype="multipart/form-data" method="post">
        <div class="select-file-wrap">
            <label for="select-file">Select images to scan<br>
                <input id="select-file" type="file" name="files" multiple accept="image/*" />
            </label>
        </div>

        <button class="btn-upload" type="submit" id="inputField"
            value="Upload">Upload</button>
    </form>
</div>
<p>
<p>
<div class="wrapper">
    <h2 class="title">Quram WebOCR for Passport</h2>
    <form class="container" action="scan/passport" enctype="multipart/form-data" method="post">
        <div class="select-file-wrap">
            <label for="select-file">Select images to scan<br>
                <input id="select-file" type="file" name="files" multiple accept="image/*" />
            </label>
        </div>

        <button class="btn-upload" type="submit" id="inputField"
                value="Upload">Upload</button>
    </form>
</div>
</body>
</html>
