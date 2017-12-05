package com.allcom.security.s2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import ognl.SetPropertyAccessor;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.concurrent.ConcurrentLoginException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springside.modules.security.springsecurity.SpringSecurityUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.commons.AppContext;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.json.JsonStoreResult;
import com.allcom.commons.util.TimeUtils;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.employee.service.EmployeeManager;
import com.allcom.log.entity.LoginLog;
import com.allcom.log.service.LoginLogManager;
import com.allcom.security.CurrentUser;
import com.allcom.security.OnlineUserBindingListener;
import com.allcom.security.entity.Role;
import com.allcom.security.entity.User;
import com.allcom.security.service.SecurityManager;
import com.allcom.security.service.UserManager;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 登录成功或者失败后的处理类.
 * 
 * @author dw
 */
@SuppressWarnings("serial")
public class LoginHandlerAction extends ActionSupport {
	@Autowired
	private UserManager userManager;
	@Autowired
	private EmployeeManager employeeManager;
	@Autowired
	private LoginLogManager loginLogManager;

	@Autowired
	private SecurityManager securityManager;

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 登录成功后的处理.
	 * 
	 * @return json格式的数据
	 */
	public String success() {
		
		CurrentUser currentUser = (CurrentUser) SpringSecurityUtils
				.getCurrentUser();

		// 保存登录时间和登录IP
		User user = currentUser.getUser();
		if (user.getEmployee() != null) {
			user.setEmployee(employeeManager.get(user.getEmployee().getId()));
		}
		userManager.logIpAndTime(user.getId(), Struts2Utils.getRequest()
				.getRemoteAddr());

		// 设置部门直接领导名字
		currentUser.setManagerNames(employeeManager.getManagerName(user));
		boolean isManager = employeeManager.isManager(user);
		// 设置是否为部门领导
		currentUser.setManager(isManager);
		// 设置可管理的人员
		currentUser.setManagedEmployees(employeeManager.getManagedName(
				user.getEmployee(), isManager));

		// 注册监听事件
		ActionContext
				.getContext()
				.getSession()
				.put("onlineUserBindingListener",
						new OnlineUserBindingListener(currentUser,
								ServletActionContext.getRequest().getSession()));

		boolean isProManager = false; // 是否为部门经理以上
		String userRoles = user.getRoleNames();
		if (userRoles.indexOf("MANAGER") != -1
				|| userRoles.indexOf("DIRECTOR") != -1
				|| userRoles.indexOf("CHIEF") != -1) {
			isProManager = true;
		}

		// 返回授权
		GrantedAuthority[] authes = currentUser.getAuthorities();
		StringBuffer jsonString = new StringBuffer();
		jsonString.append("{success:true,message:'ok',data:{user:{userId:"
				+ currentUser.getUser().getId() + ",userName:'"
				+ currentUser.getUsername());

		if (currentUser.getUser().getEmployee() != null) {
			jsonString.append("',employeeId:"
					+ currentUser.getUser().getEmployee().getId());
			jsonString.append(",employeeName:'"
					+ currentUser.getUser().getEmployee().getName());
			jsonString.append("',orgId:"
					+ currentUser.getUser().getEmployee().getOrg().getId());
			jsonString.append(",orgName:'"
					+ currentUser.getUser().getEmployee().getOrg().getName()
					+ "'}");
		} else {
			jsonString
					.append("',employeeId:0,employeeName:'',orgId:0,orgName:''}");
		}
		jsonString.append(",isProManager:" + isProManager + " ");

		// 资源
		jsonString.append(",resources:',");
		jsonString.append(StringUtils.join(currentUser.getResourceIds(), ","));
		jsonString.append(",'");

		// 角色
		jsonString.append(",roles:[");
		List<Role> roles = user.getRoleList();
		for (Role role : roles) {
			jsonString.append("'");
			jsonString.append(role.getName());
			jsonString.append("',");
		}
		jsonString.append("]");

		// 授权
		jsonString.append(",authes:[");
		for (GrantedAuthority grantedAuthority : authes) {
			jsonString.append("'");
			jsonString.append(grantedAuthority.getAuthority());
			jsonString.append("',");
		}
		jsonString.append("]");

		// 获取用户配置信息
		Map<String, String> userConfigs = ((CurrentUser) SpringSecurityUtils
				.getCurrentUser()).getUserConfigs();
		jsonString.append(",configs:{");
		if (userConfigs != null) {
			Iterator it = userConfigs.keySet().iterator();
			while (it.hasNext()) {
				String configKey = (String) it.next();
				jsonString.append("" + configKey + ":'"
						+ String.valueOf(userConfigs.get(configKey)));
				jsonString.append("'");
				if (it.hasNext()) {
					jsonString.append(",");
				}
			}
			jsonString.append("}");
		}

		jsonString.append("}}");

		ServletActionContext.getRequest().setAttribute(
				CrudActionEx.JSON_RESULT_KEY, jsonString.toString());
		ServletActionContext.getRequest().getSession()
				.setAttribute("wpmUser", user);

		// 记录登录日志
		LoginLog loginLog = loginLogManager.get(currentUser.getLoginLogId());
		loginLog.setResult("true");
		long duration = TimeUtils.getNowDate().getTimeInMillis()
				- TimeUtils.toDate(loginLog.getLoginTime()).getTime();
		loginLog.setLoginDuration(duration);
		loginLogManager.save(loginLog);

		return CrudActionEx.JSON;
	}

	/**
	 * 登录失败后的处理.
	 * 
	 * @return json格式的数据
	 */
	public String failure() {
		AuthenticationException exception = (AuthenticationException) ActionContext
				.getContext()
				.getSession()
				.get(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

		String errors = "";
		String message = "";

		if (exception instanceof BadCredentialsException) {
			message = "用户名与密码不符";
			errors = errors + "j_username:'" + message + "'";
		} else if (exception instanceof ConcurrentLoginException) {
			message = "用户已在其它地方登录";
			errors = errors + "j_username:'" + message + "'";
		} else {
			message = exception.getMessage();
			errors = errors + "j_username:'" + message + "'";
		}

		String jsonResult = "{success:false,errors:{" + errors + "},message:'"
				+ message + "'}";
		ServletActionContext.getRequest().setAttribute(
				CrudActionEx.JSON_RESULT_KEY, jsonResult);
		return CrudActionEx.JSON;
	}

	/**
	 * 用户退出.
	 * 
	 * @return json格式的数据
	 */
	public String logout() {
		ServletActionContext.getRequest().getSession().invalidate();

		ServletActionContext.getRequest().setAttribute(
				CrudActionEx.JSON_RESULT_KEY, "{success:true}");
		return CrudActionEx.JSON;
	}

	/**
	 * 得到当前用户的菜单
	 * 
	 * @return json格式的数据
	 */
	public String getMenuJson() {
		try {
			CurrentUser currentUser = ((CurrentUser) SpringSecurityUtils
					.getCurrentUser());

			ServletActionContext.getRequest().setAttribute(
					CrudActionEx.JSON_RESULT_KEY, currentUser.getMenuJson());
			return CrudActionEx.JSON;
		} catch (Exception e) {
			Struts2Utils.getRequest().setAttribute(
					CrudActionEx.JSON_RESULT_KEY,
					CrudActionEx.getJsonError(e.getMessage()));
			logger.error(e.getMessage());
			return CrudActionEx.JSON;
		}
	}

	/**
	 * 得到在线用户
	 * 
	 * @return json格式的数据
	 */
	public String listOnlineUser() {
		Map<String, CurrentUser> onlineUsers = AppContext.getOnlineUsers();

		String[] properties = { "username", "user.lastLoginTime",
				"user.roleNames", "user.employeeId", "user.loginIp" };
		JsonStoreResult jsonStoreResult = new JsonStoreResult(properties,
				onlineUsers.values(), onlineUsers.size());
		// 排除的属性名
		String[] excludeProperties = { "menuJson", "userRoles", "roleList",
				"password", "employee", "managerNames", "isManager",
				"managedEmployees", "resourceIds", "userConfigs" };
		String jsonString = JsonConvert.toStringByExclude(jsonStoreResult,
				excludeProperties);
		Struts2Utils.getRequest().setAttribute(CrudActionEx.JSON_RESULT_KEY,
				jsonString);
		return CrudActionEx.JSON;// JSON方式
	}

	/**
	 * 注销用户
	 * 
	 * @return json格式的数据
	 */
	public String invalidate() {
		String username = Struts2Utils.getRequest().getParameter("username");

		HttpSession session = AppContext.getOnlineSessions().get(username);
		if (session != null) {
			session.invalidate();
		}

		JsonResult jsonResult = new JsonResult(true, "注销成功");
		Struts2Utils.getRequest().setAttribute(CrudActionEx.JSON_RESULT_KEY,
				JsonConvert.toString(jsonResult));
		return CrudActionEx.JSON;
	}

//	/**
//	 * 插入发送短信的数据表
//	 * 
//	 * @return
//	 */
//	public String insertSend() {
//
//		// 操作信息
//		String message = "验证码发送成功！";
//		String username = Struts2Utils.getRequest().getParameter("username");
//
//		// 创建原生sql
//		/*
//		 * Session session = userManager.getDao().getSessionFactory()
//		 * .getCurrentSession(); XmlWebApplicationContext ctx =
//		 * (XmlWebApplicationContext) WebApplicationContextUtils
//		 * .getWebApplicationContext(Struts2Utils.getRequest()
//		 * .getSession().getServletContext()); HibernateTransactionManager tran
//		 * = (HibernateTransactionManager) ctx .getBean("transactionManager");
//		 * DefaultTransactionDefinition def = new
//		 * DefaultTransactionDefinition();
//		 * def.setPropagationBehavior(TransactionDefinition
//		 * .PROPAGATION_REQUIRES_NEW); TransactionStatus status =
//		 * tran.getTransaction(def);
//		 */
//
//		Session session = userManager.getDao().getSessionFactory()
//				.openSession();
//		List userList = session.createQuery(" from User where loginName=? ")
//				.setParameter(0, username).list();
//		Transaction trans = session.beginTransaction();
//
//		try {
//			if (userList == null || userList.size() <= 0) {
//				message = "用户名不存在！";
//			} else {
//				User user = (User) userList.get(0);
//				if (!user.getState().equals("Y")) {
//					message = "无效用户";
//				} else {
//					// 判断3分钟内是否发送过验证码
//					String isSendSql = "select * from p_user_verify_code where phone=? and send_time>=sysdate-numtodsinterval(3,'minute') ";
//					List list = session.createSQLQuery(isSendSql)
//							.setParameter(0, username).list();
//					int count = 0;
//
//					if (list != null && list.size() > 0) {// 已经发送过验证码
//						message = "3分钟内已经发送过验证码，请稍后在试！";
//					} else {
//						trans.begin();// 开启事物
//						// 生成随机验证码
//						String code = "";
//						for (int i = 0; i < 5; i++) {
//							String itmp = new Random().nextInt(10) + "";
//							code += itmp;
//						}
//
//						// 要发送的短信内容
//						String content = "尊敬的客户，您本次登录的随机短信密码为" + code;
//						String sendVerifyCode = "insert into sms_send_cw@dblink_old_msm.us.oracle.com( MSG_ID,DESTTERM,CHARGETERM,SOURCETERM,CONTENT,STATE,LENGTH,TYPE,NEEDREPLY,MSGLEVEL,SERVICEID,FORMAT,FEETYPE,FEECODE,APP,OPTIME) values(HIBERNATE_SEQUENCE.nextval@dblink_old_msm.us.oracle.com,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//						count = session
//								.createSQLQuery(sendVerifyCode)
//								.setParameter(0, username)
//								.setParameter(1, username)
//								.setParameter(2, "1065975510009")
//								.setParameter(3, content)
//								.setParameter(4, "0")
//								.setParameter(5, content.length())
//								.setParameter(6, "6")
//								.setParameter(7, "1")
//								.setParameter(8, "1")
//								.setParameter(9, "p2cp")
//								.setParameter(10, "8")
//								.setParameter(11, "00")
//								.setParameter(12, "0")
//								.setParameter(13, "YZ_SXD")
//								.setParameter(
//										14,
//										new SimpleDateFormat("yyyyMMddHHmmss")
//												.format(new Date()))
//								.executeUpdate();
//
//						if (count > 0) {
//
//							// 保存验证码到数据库
//							String saveVerifyCode = "insert into p_user_verify_code(ID,PHONE,CODE) values(seq_verity.nextval,?,?)";
//							count = session.createSQLQuery(saveVerifyCode)
//									.setParameter(0, username)
//									.setParameter(1, code).executeUpdate();
//							/* tran.commit(status); */
//							trans.commit();
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			message = e.getMessage();
//			/* tran.rollback(status); */
//			trans.rollback();
//		}finally{
//			session.close();
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("success", false);
//		map.put("error", message);
//		map.put("message", message);
//		ServletActionContext.getRequest().setAttribute(
//				CrudActionEx.JSON_RESULT_KEY, JSONObject.fromObject(map));
//
//		return "json";
//	}
//
//	public String loginCheck() {
//
//		// 操作信息
//		String message = "登录成功！";
//		String username = Struts2Utils.getRequest().getParameter("username");
//		String pwd = Struts2Utils.getRequest().getParameter("password");
//		boolean success = false;
//		// 判断用户名是否存在
//		User user = securityManager.findUserByLoginName(username);
//
//		if (user == null || !user.getState().equals("Y")) {
//			message = "用户名不存在！";
//			throw new RuntimeException("用户名不存在！");
//		}
//		// 创建原生sql
//		Session session = userManager.getDao().getSessionFactory()
//				.getCurrentSession();
//
//		try {
//			// 判断3分钟内是否发送过验证码
//			String isSendSql = "select count(*) from p_user_verify_code where phone=? and send_time>=sysdate-numtodsinterval(3,'minute') ";
//			List list = session
//					.createSQLQuery(isSendSql)
//					.setParameter(0, username)
//					.setParameter(1, pwd)
//					.setParameter(
//							2,
//							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//									.format(new Date())).list();
//
//			if (list != null && list.size() > 0) {// 已经发送过验证码
//				success = true;
//			} else {
//				message = "验证码错误！";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			message = "系统繁忙，请稍后重试！";
//		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("success", success);
//		map.put("error", message);
//		map.put("message", message);
//		ServletActionContext.getRequest().setAttribute(
//				CrudActionEx.JSON_RESULT_KEY, JSONObject.fromObject(map));
//		return CrudActionEx.JSON;
//
//	}
}
