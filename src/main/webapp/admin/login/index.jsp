<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<%@ include file="../base/taglibs.jsp"%>
<head>  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
    <meta name="keywords" content="jquery,ui,easy,easyui,web">  
    <meta name="description" content="easyui help you build your web page easily!">  
    <title>Ajax Form - jQuery EasyUI Demo</title>  
    <link rel="stylesheet" type="text/css" href="http://www.java1234.com/jquery-easyui-1.3.3/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="http://www.java1234.com/jquery-easyui-1.3.3/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="http://www.java1234.com/jquery-easyui-1.3.3/demo/demo.css">
	<script type="text/javascript" src="http://www.java1234.com/jquery-easyui-1.3.3/jquery.min.js"></script>
	<script type="text/javascript" src="http://www.java1234.com/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="http://www.java1234.com/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
</head>  
<body>   

      
    <div class="easyui-panel" title="Ajax表单" style="width:230px;padding:10px;">  
        <form id="ff" action="form" method="post">  
            <table>  
                <tr>  
                    <td>用户名:</td>  
                    <td><input name="name" type="text"></input></td>  
                </tr>  
                <tr>  
                    <td>密码:</td>  
                    <td><input name="email" type="text"></input></td>  
                </tr>  
                <tr>  
                    <td></td>  
                    <td><input type="submit" value="提交"></input></td>  
                </tr>  
            </table>  
        </form>  
    </div>  
    <script type="text/javascript">  
        $(function(){  
            $('#ff').form({  
            	type : 'post',
				async : false,
				url : '/thyx/j_spring_security_check',
				data : {
						j_username : 'admin',
						j_password: '000000'

				},
                success:function(data){  
                    $.messager.alert('系统提示', data, 'info');  
                }  
            });  
        });  
    </script>  
</body>  
</html>  