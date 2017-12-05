package com.allcom.security;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.allcom.commons.AppContext;
import com.allcom.commons.util.TimeUtils;
import com.allcom.log.entity.LoginLog;
import com.allcom.log.service.LoginLogManager;

/**
 * 在线用户监听器.
 * 监听用户的登录或者退出
 * @author dw
 */
public class OnlineUserBindingListener implements HttpSessionBindingListener {
	private CurrentUser currentUser;
	private HttpSession session;

	public OnlineUserBindingListener(CurrentUser currentUser, HttpSession session) {
		this.currentUser = currentUser;
		this.session = session;
	}

	/**
	 * 登录.
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		AppContext.getOnlineSessions().put(currentUser.getUsername(), session);
		AppContext.getOnlineUsers().put(currentUser.getUsername(), currentUser);
	}

	/**
	 * 注销.
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		AppContext.getOnlineSessions().remove(currentUser.getUsername());
		AppContext.getOnlineUsers().remove(currentUser.getUsername());

		//记录退出日志
		LoginLogManager loginLogManager = (LoginLogManager) AppContext.getApplicationContext().getBean(
				"loginLogManager");
		LoginLog loginLog = loginLogManager.get(currentUser.getLoginLogId());
		long duration = TimeUtils.getNowDate().getTimeInMillis() - TimeUtils.toDate(loginLog.getLoginTime()).getTime();
		loginLog.setLogoutTime(TimeUtils.getNowDateAsString());
		loginLog.setDuration(duration / 60000);
		loginLogManager.save(loginLog);
	}

}
