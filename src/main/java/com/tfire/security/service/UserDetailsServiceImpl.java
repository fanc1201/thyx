package com.allcom.security.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.InsufficientAuthenticationException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springside.modules.utils.ReflectionUtils;

import com.allcom.commons.util.TimeUtils;
import com.allcom.config.entity.VariableConfig;
import com.allcom.config.s2.VariableConfigAction;
import com.allcom.config.service.VariableConfigManager;
import com.allcom.log.entity.LoginLog;
import com.allcom.log.service.LoginLogManager;
import com.allcom.security.CurrentUser;
import com.allcom.security.entity.Authority;
import com.allcom.security.entity.Resource;
import com.allcom.security.entity.ResourceTree;
import com.allcom.security.entity.Role;
import com.allcom.security.entity.User;
import com.allcom.security.filter.FormLoginFilter;

/**
 * 实现SpringSecurity的UserDetailsService接口,实现获取用户Detail信息的回调函数.
 * 
 * @author dw
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private SecurityManager securityManager;
	@Autowired
	private VariableConfigManager variableConfigManager;
	@Autowired
	private LoginLogManager loginLogManager;

	/**
	 * 获取用户Details信息的回调函数.
	 */
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		// 记录登录日志
		LoginLog loginLog = new LoginLog();
		loginLog.setLoginTime(TimeUtils.getNowDateAsString());
		loginLog.setResult("false");
		loginLog.setLoginName(userName);
		loginLog.setClientIp(FormLoginFilter.getClientIp());

		// 从线程变量中获取参数
		String loginType = "local";
		if (FormLoginFilter.getParams().get("j_logintype") != null) {
			loginType = ((String[]) FormLoginFilter.getParams().get(
					"j_logintype"))[0];
		}
		User user = null;
		if ("local".equalsIgnoreCase(loginType)) {
			user = securityManager.findUserByLoginName(userName);
		} else if ("eiac".equalsIgnoreCase(loginType)) {
			String password = String.valueOf(FormLoginFilter.getParams().get(
					"j_password"));

			// @todo 调用eiac远程服务
		}

		if (user == null || !user.getState().equals("Y")) {
			loginLog.setFailureReason("用户" + userName + " 不存在");
			loginLogManager.save(loginLog);

			throw new UsernameNotFoundException(loginLog.getFailureReason());
		}

		// 创建原生sql
		GrantedAuthority[] grantedAuths = obtainGrantedAuthorities(user);
		List<Resource> resources = obtainGrantedResources(user);

		if (grantedAuths.length <= 0) {
			loginLog.setFailureReason("授权权限不足");
			loginLogManager.save(loginLog);
			throw new InsufficientAuthenticationException(
					loginLog.getFailureReason());
		}

		String menuJson = createGrantedMenus(resources);
		if (menuJson.length() <= 2) {
			loginLog.setFailureReason("菜单权限不足");
			loginLogManager.save(loginLog);
			throw new InsufficientAuthenticationException(
					loginLog.getFailureReason());
		}

		// web-archetype中无以下属性,暂时全部设为true.
		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		CurrentUser userdetail = new CurrentUser(user.getLoginName(),
				user.getPassword(), enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, grantedAuths);

		userdetail.setMenuJson(menuJson);
		userdetail.setUser(user);
		userdetail.setResourceIds(createGrantedResourceIds(resources));

		// 查询用户个人配置
		Map<String, String> userConfigs = new HashMap<String, String>();
		long userId = userdetail.getUser().getId();
		Iterator<VariableConfig> configs = variableConfigManager.getDao()
				.findBy("userId", userId).iterator();
		while (configs.hasNext()) {
			VariableConfig config = configs.next();
			userConfigs.put(config.getCode(), config.getValue());
		}
		if (userConfigs.isEmpty()) {
			userConfigs.put("message_remind",
					VariableConfigAction.MESSAGE_REMIND_TIME);
			userConfigs.put("message_delay",
					VariableConfigAction.MESSAGE_DELAY_TIME);
			userConfigs.put("layout", VariableConfigAction.LAYOUT);
			userConfigs.put("theme", VariableConfigAction.THEME);
			userConfigs.put("desktop_refresh_interval",
					VariableConfigAction.DESKTOP_REFRESH_INTERVAL);
			userConfigs.put("max_tabpage", VariableConfigAction.MAX_TABPAGE);
		}
		userdetail.setUserConfigs(userConfigs);

		// 保存登录日志
		loginLogManager.save(loginLog);
		userdetail.setLoginLogId(loginLog.getId());
		return userdetail;
	}

	/**
	 * 获得用户所有角色的授权集合.
	 */
	private GrantedAuthority[] obtainGrantedAuthorities(User user) {
		Set<GrantedAuthority> authSet = new HashSet<GrantedAuthority>();
		for (Role role : user.getRoleList()) {
			for (Authority authority : role.getAuthorityList()) {
				authSet.add(new GrantedAuthorityImpl(authority.getName()));
			}
		}
		return authSet.toArray(new GrantedAuthority[authSet.size()]);
	}

	/**
	 * 获得用户所有角色的资源集合.
	 */
	private List<Resource> obtainGrantedResources(User user) {
		Set<Authority> authSet = new HashSet<Authority>();
		for (Role role : user.getRoleList()) {
			for (Authority authority : role.getAuthorityList()) {
				authSet.add(authority);
			}
		}

		String authorityIds = ReflectionUtils.fetchElementPropertyToString(
				authSet, "id", ",");
		List<Resource> resources = securityManager
				.getResourceWithAuthorities(authorityIds);

		return resources;
	}

	/**
	 * 生成用户所有角色的资源集合.
	 */
	private List<Long> createGrantedResourceIds(List<Resource> resources) {
		List<Long> resourceIds = new ArrayList<Long>();
		for (Resource resource : resources) {
			resourceIds.add(resource.getId());
		}

		return resourceIds;
	}

	/**
	 * 生成用户所有角色的菜单集合（树形结构）.
	 */
	public String createGrantedMenus(List<Resource> resources) {
		List<Resource> menuResources = new ArrayList<Resource>();
		for (Resource resource : resources) {
			if (resource.getResourceType().equals("menu")) {
				menuResources.add(resource);
			}
		}

		ResourceTree resourceTree = new ResourceTree(menuResources, false);
		return resourceTree.getRootNode().getChildren().toString();
	}
}
