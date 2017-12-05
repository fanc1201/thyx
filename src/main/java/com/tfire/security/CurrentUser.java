package com.allcom.security;

import java.util.List;
import java.util.Map;

import org.springframework.security.GrantedAuthority;

import com.allcom.security.entity.User;

/**
 * 登录后，保存到spring security中的当前用户对象.
 * @author dw
 */
@SuppressWarnings("serial")
public class CurrentUser extends org.springframework.security.userdetails.User {
	/**
	 * 数据库中的user对象.
	 */
	private User user;
	/**
	 * 用户能够操作的菜单(json格式).
	 */
	private String menuJson;
	/**
	 * 直接部门领导,多个逗号分开
	 */
	private String managerNames;
	/**
	 * 是否为部门的管理人员
	 */
	private boolean isManager;
	/**
	 * 可管理的员工
	 */
	private Map<Long, String> managedEmployees;
	/**
	 * 所有资源ID(json格式)
	 */
	private List<Long> resourceIds;
	/**
	 * 用户配置信息
	 */
	private Map<String, String> userConfigs;
	/**
	 * 登录日志ID
	 */
	private Long loginLogId;

	public CurrentUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, GrantedAuthority[] authorities)
			throws IllegalArgumentException {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMenuJson() {
		return menuJson;
	}

	public void setMenuJson(String menuJson) {
		this.menuJson = menuJson;
	}

	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public List<Long> getResourceIds() {
		return resourceIds;
	}

	public void setManagerNames(String managerNames) {
		this.managerNames = managerNames;
	}

	public String getManagerNames() {
		return managerNames;
	}

	public void setManager(boolean isManager) {
		this.isManager = isManager;
	}

	public boolean isManager() {
		return isManager;
	}

	public String getUserConfig(String key) {
		String value = userConfigs.get(key);
		if (value == null)
			return "";
		else
			return value;
	}

	public Map<String, String> getUserConfigs() {
		return userConfigs;
	}

	public void setUserConfigs(Map<String, String> userConfigs) {
		this.userConfigs = userConfigs;
	}

	public void setManagedEmployees(Map<Long, String> managedEmployees) {
		this.managedEmployees = managedEmployees;
	}

	public Map<Long, String> getManagedEmployees() {
		return managedEmployees;
	}

	public void setLoginLogId(Long loginLogId) {
		this.loginLogId = loginLogId;
	}

	public Long getLoginLogId() {
		return loginLogId;
	}
}
