package com.allcom.security.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.api.IdentityService;
import org.jbpm.api.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allcom.commons.AppContext;
import com.allcom.commons.service.ServiceManager;
import com.allcom.commons.util.TimeUtils;
import com.allcom.security.dao.UserDao;
import com.allcom.security.entity.Authority;
import com.allcom.security.entity.Role;
import com.allcom.security.entity.User;

/**
 * User实体的管理类.
 * 
 * @author
 */
//Spring Service Bean.
@Service
public class UserManager extends ServiceManager<UserDao,User>{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UserManager.class);

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private UserDao userDao;

	@Override
	public UserDao getDao() {
		return userDao;
	}	

	@Transactional
	public void resetPwd(Long[] userIds) {
		Query query = userDao.createQuery("update User set password = '000000' where id=:id");
		for (int i = 0; i < userIds.length; i++) {
			query.setLong("id", userIds[i]);
			query.executeUpdate();
		}
	}

	@Transactional
	public void modifyPwd(Long userId, String newPwd) {
		Query query = userDao.createQuery("update User set password = :pwd where id=:id");
		query.setString("pwd", newPwd);
		query.setLong("id", userId);
		query.executeUpdate();
	}

	@Transactional
	public void setEmployee(Long userId, Long employeeId) throws Exception {
		Query query = userDao.createQuery("update User set employee.id = :employeeId where id=:id");
		query.setLong("employeeId", employeeId);
		query.setLong("id", userId);
		query.executeUpdate();
	}

	@Transactional
	public void logIpAndTime(Long userId, String loginIp) {
		Query query = userDao
				.createQuery("update User set loginIp = :loginIp,lastLoginTime = :lastLoginTime where id=:id");
		query.setString("loginIp", loginIp);
		query.setLong("id", userId);
		query.setString("lastLoginTime", TimeUtils.getNowDateAsString());
		query.executeUpdate();
	}

	/**
	 * 得到角色下的所有用户
	 */
	public List<String> getUserNames(String roleNames) {
		Query query = userDao
				.createQuery("select u.loginName from User u left join u.userRoles r where r.role.name in(" + roleNames
						+ ")");
		List users = query.list();
		List<String> userNames = new ArrayList<String>();
		for (Object object : users) {
			userNames.add((String) object);
		}

		return userNames;
	}

	/**
	 * 得到登录用户能够代理的用户
	 */
	public List<String> getUserConsigners() {
		Query query = userDao.createQuery("select consigner from UserConsigne where consignee=?", AppContext
				.getCurrentUser().getUsername());
		List<String> consigners = query.list();
		return consigners;
	}

	/**
	 * 根据员工得到用户信息
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, String> getUserByEmployee(String employeeIds) {
		if (employeeIds == null || "".equals(employeeIds.trim()))
			return null;

		String queryString = "select u from User u where u.employee.id in("
				+ employeeIds + ") and u.state='Y'";
		Query query = getDao().createQuery(queryString);
		List<User> users = getDao().distinct(query).list();
		Map<String, String> userMap = new HashMap<String, String>();
		for (User s : users) {
			userMap.put(String.valueOf(s.getId()), s.getName());
		}
		return userMap;
	}

	/**
	 * 同步用户、角色到工作流的.
	 */
	public void refreshBpm() throws Exception {
		ProcessEngine processEngine = (ProcessEngine) AppContext.getApplicationContext().getBean("processEngine");
		IdentityService identityService = processEngine.getIdentityService();
		SessionFactory sessionFactory = (SessionFactory) AppContext.getApplicationContext().getBean("sessionFactory");

		Session session = sessionFactory.openSession();

		try {
			org.hibernate.Transaction transaction = session.beginTransaction();
			// 清除原有关系
			session.createQuery("delete from MembershipImpl").executeUpdate();
			session.createQuery("delete from GroupImpl").executeUpdate();
			session.createQuery("delete from UserImpl").executeUpdate();
			transaction.commit();
			session.close();

			// role 作为 group的一种
			List<Role> roles = securityManager.getRoleDao().getAll();
			for (Role role : roles) {
				identityService.createGroup(role.getName(), "role");
			}
			// user 作为 user
			List<User> users = securityManager.getUserDao().getAll();
			for (User user : users) {
				identityService.createUser(user.getLoginName(), user.getName(), user.getName());

				roles = user.getRoleList();
				for (Role role : roles) {
					identityService.createMembership(user.getLoginName(), "role." + role.getName());
				}
			}
		} finally {

		}
	}

	/**
	 * 获得用户所有角色的授权.
	 */
	public static Set<Authority> obtainAuthority(User user) {
		Set<Authority> authSet = new HashSet<Authority>();
		for (Role role : user.getRoleList()) {
			for (Authority authority : role.getAuthorityList()) {
				authSet.add(authority);
			}
		}
		return authSet;
	}
}

