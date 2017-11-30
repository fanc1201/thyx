<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<%@ include file="../base/taglibs.jsp"%>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="viewport"
	content="width=device-width,initial-scale=1.0, maximum-scale=1.0,user-scalable=no">
<link rel="stylesheet" href="${ctx}/admin/base/css/bootstrap-3.3.7.css?v=d6090e0203" />
<link rel="stylesheet" href="${ctx}/admin/login/css/login.css?v=8aaefa19a1" />
<link rel="stylesheet" href="${ctx}/admin/base/css/font-awesome-3.2.1.css?v=815dca6185" />
<title>登录</title>
</head>
<body>
	<div id="loginbox">
			<div class="control-group normal_text">
				<h3>
					<img src="../static/img/logo.png" alt="登&nbsp;&nbsp;录" />
				</h3>
			</div>
			<div class="control-group">
				<div class="controls">
					<div class="main_input_box">
						<span class="add-on bg_lg"><i class="glyphicon glyphicon-user"></i></span><input
							type="text" id="uname" placeholder="用户名" />
					</div>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					<div class="main_input_box">
						<span class="add-on bg_ly"><i class="icon-lock"></i></span><input
							type="password" id="pword" placeholder="密码" />
					</div>
				</div>
			</div>
			<div style="margin-left:39%;color:red;" id="tip">账号或密码错误</div>
			<div class="form-actions">
			<button type="button" id="login" class="btn btn-success" onclick="login();">登&nbsp;&nbsp;&nbsp;&nbsp;录</button>
			</div>
	</div>
	<script type="text/javascript" src="${ctx }/admin/base/js/jquery-1.9.1.min.js?v=acc0adc6c1"></script>
	<script type="text/javascript" src="${ctx }/admin/login/js/login.js?v=25001b1f46"></script>
	
</body>
</html>