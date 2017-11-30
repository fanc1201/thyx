function login(){
	var uname = $.trim($("#uname").val());
	var pword = $.trim($("#pword").val());
	$.ajax({
		type : "GET",
		url :  baseService + "admin/login",
		//jsonp : "jsoncallback",
		dataType : "jsonp",
		timeout : timeout,
		data : {
			"uname":uname,"pword":pword
		},
		success : function(result) {
			if(result.record!=null){
				setCookie("id",result.record.id);
				setCookie("uname",result.record.uname);
				window.location.href = "index.html"; 
			}else{
				$("#tip").show();
			}
		}
	});
}

$(function(){
	$("#tip").hide();
	
	$("#loginbox input").focus(function(){
		$("#tip").hide();
	});
	
	$("#uname").keydown(function(e) {
		if (e.keyCode == 13){
			$("#pword").focus();
		}
	});
	$("#pword").keydown(function(e) {
		if (e.keyCode == 13){
			$("#login").click();
		}
	});
});