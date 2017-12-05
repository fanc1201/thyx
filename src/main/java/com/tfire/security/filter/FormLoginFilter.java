package com.allcom.security.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

/**
 * form loing提交的filter，用于额外获取表单参数.
 */
public class FormLoginFilter extends AuthenticationProcessingFilter {
	@SuppressWarnings("unchecked")
	private static ThreadLocal<Map> params = new ThreadLocal<Map>();

	private static ThreadLocal<String> clientIp = new ThreadLocal<String>();

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
		//把参数保存到线程变量中
		params.set(request.getParameterMap());
		clientIp.set(request.getRemoteAddr());

		Authentication authentication = super.attemptAuthentication(request);

		return authentication;
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException {
		// TODO Auto-generated method stub
		super.onSuccessfulAuthentication(request, response, authResult);
	}

	@SuppressWarnings("unchecked")
	public static Map getParams() {
		return params.get();
	}

	public static String getClientIp() {
		return clientIp.get();
	}

	@Override
	public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
		super.setAlwaysUseDefaultTargetUrl(alwaysUseDefaultTargetUrl);
	}
}
