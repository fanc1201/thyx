package com.allcom.security.s2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.hibernate.HibernateWebUtils;
import org.springside.modules.security.springsecurity.SpringSecurityUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.cg.entity.BeanDef.PropertyType;
import com.allcom.commons.hibernate.AliasParameter;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonDateProcessor;
import com.allcom.commons.json.JsonFormResult;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.json.ViewFactory;
import com.allcom.commons.json.JsonStoreResult;
import com.allcom.commons.json.JsonStoreRowResult;
import com.allcom.commons.util.ArrayUtils;
import com.allcom.commons.util.ExcelUtils;
import com.allcom.commons.util.TimeUtils;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.security.CurrentUser;
import com.allcom.security.entity.Role;
import com.allcom.security.entity.User;
import com.allcom.security.service.SecurityManager;
import com.allcom.security.service.UserManager;

/**
 * User Action.
 * 
 * @author admin 
 */
@SuppressWarnings("serial")
public class UserAction extends CrudActionEx<User> {
	@Autowired
	private SecurityManager securityManager;
	@Autowired
	private UserManager userManager;
	//实体对象
	private User entity;
	//包含的属性名
	private String[] includeProperties = { "id", "loginName", "name", "state", "email", "memo", "createTime",
			"lastLoginTime", "loginIp", "type", "employee.id", "employee.name", "roleNames" };
	//排除的属性名
	private String[] excludeProperties = { "org", "userRoles", "roleList", "roleIds", "password",
			"employee.sex", "employee.birthTime", "employee.nation", "employee.nativePlace", "employee.polity",
			"employee.idCard", "employee.insureCard", "employee.dipolma", "employee.graduateSchool",
			"employee.speciality", "employee.graduateTime", "employee.jobCard", "employee.workStation",
			"employee.joinTime", "employee.officePhone", "employee.mobilePhone", "employee.email",
			"employee.workTitle", "employee.urgencyLinkName", "employee.urgencyLink", "employee.memo",
			"employee.createTime", "employee.lastUpdateTime" };
	// 引用对象别名以及关联方式
	private AliasParameter[] aliases = { new AliasParameter("employee", CriteriaSpecification.LEFT_JOIN) };
	
	public UserAction() {
		isAuthorizeInAction = false;
		isWatch = false;
		nullProperties.put(com.allcom.employee.entity.Employee.class, "{}");
		valueProperties.put(java.util.Date.class, new JsonDateProcessor());
	}

	// 准备函数 数据模型 //
	/**
	 * 设置数据实体.
	 */		
	@Override
	public User getModel() {
		return entity;
	}

	public User getEntity() {
		return entity;
	}

	public void setEntity(User entity) {
		this.entity = entity;
	}

	/**
	 * 供prepardMethodName()函数调用. 
	 */	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = userManager.get(id, aliases);
		} else {
			entity = new User();
		}
	}
		
	// CRUD Action 函数 //
	/**
	 * Action函数,显示新增或修改Entity界面.
	 * return json or struts jsp.
	 */
	@Override
	public String input() throws Exception {
		prepareModel();

		try {
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "", NAME_PREFIX, includeProperties, entity);
				String jsonString = JsonConvert.toStringByExclude(jsonFormResult, excludeProperties, nullProperties,
						valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);			
				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("entity", entity);
				//logger.debug(entity.toString());
				return INPUT;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}

	/**
	 * Action函数,显示Entity列表界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String list() throws Exception {
		//初始化分页Page
		initPage();		
		try {
			//查询结果
			if (isUseHql()) {//Hql查询方式
				@SuppressWarnings("unchecked")
				Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(Struts2Utils.getRequest(),
						"filter_");//得到查询参数
				String hql = "from User as user";//hql查询语句
				userManager.search(page, hql, filterParamMap);//执行查询
			} else {//Criteria查询方式			
				List<PropertyFilter> filters = buildPropertyFilters();//得到查询条件				
				userManager.search(page, filters, aliases);//执行查询
			}

			if (isUseJson()) {// JSON数据协议
				Object jsonStoreResult =getJsonStroreResultObject(includeProperties);
				//转为json字符
				String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties, nullProperties,
						valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);		
				return JSON;//JSON方式
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("page", page);
				//logger.debug(page.toString());			
				return SUCCESS;//JSP方式
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}
	

	/**
	 * Action函数,新增Entity. 
	 * return json or struts jsp.
	 */
	@Override	 
	public String save() throws Exception {
		try {
			entity.setCreateTime(TimeUtils.getNowDateAsString());
			userManager.save(entity);
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "添加成功!");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				return JSON;
			} else {//JSP页面
				addActionMessage("添加成功");
				//logger.debug(entity.toString());		
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}

	/*
	 * Action函数,修改Entity. 
	 * return json or struts jsp.
	 */	
	@Override
	public String update() throws Exception {
		try {

			//只保存简单属性和应用对象，不保存集合对象
			String[] excludePropertyNames = { "createTime", "loginIp", "lastLoginTime" };
			Map<String, Object> extraProperties = new HashMap<String, Object>();
			extraProperties.put("employee.id", entity.getEmployee().getId());
			userManager.save(entity, PropertyType.Simple, excludePropertyNames, extraProperties);

			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "修改成功!");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				return JSON;
			} else {//JSP页面
				addActionMessage("修改成功!");
				//logger.debug(entity.toString());			
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}

	/**
	 * Action函数,删除Entity.
	 * return json or struts jsp.
	 */
	@Override	
	public String delete() throws Exception {
		try {
			String selectIdString = Struts2Utils.getRequest().getParameter("selectIDs");
			String[] selectedIds = selectIdString.split(",");
			StringBuffer preValues = new StringBuffer();
			preValues.append("[");
			for (String selectId : selectedIds) {
				securityManager.deleteUser(Long.parseLong(selectId));
			}
			if (isUseJson()) {// JSON数据协议
				JsonResult jsonResult = new JsonFormResult(true, "删除成功!");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);					
				return JSON;
			} else {//JSP页面
				addActionMessage("删除成功!");
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}

	/**
	 * Action函数,导出excel.
	 * return excel下载文件.
	 */
	public void exportExcel() throws Exception {
		String exportType = "filterPage";
		if (Struts2Utils.getRequest().getParameter("exportType") != null) {
			exportType = Struts2Utils.getRequest().getParameter("exportType");
		}
		try {
			//初始化分页Page
			initPage();

			if (exportType.equals("filterPage") || exportType.equals("filterAll")) {//当前页数据和当前数据，带过滤条件
				//查询结果
				if (isUseHql()) {//Hql查询方式
					@SuppressWarnings("unchecked")
					Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(Struts2Utils.getRequest(),
							"filter_");//得到查询参数
					String hql = "from User as user";//hql查询语句

					// 获取记录总数
					if (exportType.equals("filterAll")) {//当前数据
						String countHql = "select count(*) from User as user";//hql查询语句
						long totalCount = userManager.countResult(countHql, filterParamMap);
						page.setTotalCount(totalCount);
						page.setAutoCount(false);
						page.setPageSize((int) totalCount);
					}
					userManager.search(page, hql, filterParamMap);//执行查询
				} else {//Criteria查询方式			
					List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(Struts2Utils.getRequest());//得到查询条件

					// 获取记录总数
					Criterion[] criterions = userManager.getDao().buildPropertyFilterCriterions(filters);
					Criteria criteria = userManager.getDao().createCriteria(criterions);
					if (exportType.equals("filterAll")) {
						long totalCount = userManager.getDao().countCriteriaResult(criteria);
						page.setTotalCount(totalCount);
						page.setAutoCount(false);
						page.setPageSize((int) totalCount);
					}
					userManager.search(page, filters, aliases);//执行查询
				}
			} else if (exportType.equals("all")) {//全部数据，不带过滤条乿
				// 获取记录总数
				Criteria criteria = userManager.getDao().createCriteria();
				long totalCount = userManager.getDao().countCriteriaResult(criteria);
				page.setTotalCount(totalCount);
				page.setAutoCount(false);
				page.setPageSize((int) totalCount);

				userManager.search(page, new ArrayList<PropertyFilter>(), aliases);//执行查询
			}
			//excel的列
			String[] heads = { "id", "loginName", "name", "state", "email", "memo", "createTime",
					"lastLoginTime", "loginIp", "type", "employee.id" };

			//导出的属性名
			String[] properties = { "id", "loginName", "name", "state", "email", "memo", "createTime",
					"lastLoginTime", "loginIp", "type", "employee.id" };

			String fileName = new String("User.xls".getBytes("GB2312"), "ISO8859-1");
			Struts2Utils.getResponse().setHeader("Content-Disposition", "attachment;filename=" + fileName);
			Struts2Utils.getResponse().setContentType("application/vnd.ms-excel");
			ExcelUtils
					.export(Struts2Utils.getResponse().getOutputStream(), "User", heads, properties, page.getResult());

		} catch (Exception e) {
			handleException(e, e.getMessage());
			return;
		}
	}

	/**
	 * Action函数,下载批量导入模板.
	 * return excel下载文件.
	 */
	public void batchAddTemplate() throws Exception {
		try {
			//excel的列
			String[] heads = { "id", "loginName", "name", "password", "state", "email", "memo", "createTime",
					"lastLoginTime", "loginIp", "type", "employee.id" };
			heads[0] = "列头";

			//导出的属性名
			String[] properties = { "id", "loginName", "name", "password", "state", "email", "memo", "createTime",
					"lastLoginTime", "loginIp", "type", "employee.id" };
			properties[0] = "字段(不能修改)";

			String fileName = new String("User模板.xls".getBytes("GB2312"), "ISO8859-1");
			Struts2Utils.getResponse().setHeader("Content-Disposition", "attachment;filename=" + fileName);
			Struts2Utils.getResponse().setContentType("application/vnd.ms-excel");
			ExcelUtils.export(Struts2Utils.getResponse().getOutputStream(), "User", heads, properties);
		} catch (Exception e) {
			handleException(e, e.getMessage());
			return;
		}
	}

	/**
	 * Action函数,批量导入.
	 * return json or struts jsp.
	 */
	public String batchAdd() throws Exception {
		try {

			StringBuffer sb = new StringBuffer();

			List<User> beans = ExcelUtils.importBeans(User.class, this.getBatchAddFile(), 2, 1);
			int length = beans.size();

			boolean allSuccess = true;
			sb.append("批量导入结果:");
			for (int i = 0; i < length; i++) {
				try {
					User bean = beans.get(i);
					userManager.save(bean);
				} catch (Exception e) {
					e.printStackTrace();
					allSuccess = false;
					sb.append("记录行：" + (i + 1) + "保存失败!");
				}
			}
			if (allSuccess) {
				sb.append("总计" + length + "条，全部保存成功!");
			}

			if (isUseJson()) {// JSON数据协议
				String jsonString = getJsonSuccess(sb.toString());
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				return JSON;
			} else {//JSP页面
				addActionMessage("批量导入成功");
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}

	/**
	 * 得到已经分配的角色或者未分配的角色.
	 * @return json
	 * @throws Exception
	 */
	public String preSetRole() throws Exception {
		String type = Struts2Utils.getRequest().getParameter("type");

		try {
			List<Role> roles;
			if (type.equals("from")) {//未分配的角色
				//Long id = Long.parseLong(Struts2Utils.getRequest().getParameter("id"));
				User user = userManager.get(id);

				List<Role> allRoles = securityManager.getRoleDao().find(new ArrayList<PropertyFilter>());
				roles = new ArrayList<Role>();
				List<Role> ownerRoles = user.getRoleList();
				for (Role allRole : allRoles) {
					boolean flag = false;
					for (Role ownerRole : ownerRoles) {
						if (allRole.getId() == ownerRole.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						roles.add(allRole);
					}
				}
			} else if (type.equals("to")) {//已经分配的角色
				//Long id = Long.parseLong(Struts2Utils.getRequest().getParameter("id"));
				User user = userManager.get(id);
				roles = user.getRoleList();
			} else {
				roles = securityManager.getRoleDao().find(new ArrayList<PropertyFilter>());
			}
			JsonStoreResult jsonStoreResult = new JsonStoreResult(roles, roles.size());
			// 排除的属性名
			String[] excludeProperties = { "authNames", "authIds", "description", "memo", "metaData", "userRoles",
					"authorityList" };
			String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties);
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
			return JSON;// JSON方式			
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 设置角色.
	 * @return json
	 * @throws Exception
	 */
	public String setRole() throws Exception {
		try {
			String selectedRoles = Struts2Utils.getRequest().getParameter("selectedRoles");
			String selectedUserIds = Struts2Utils.getRequest().getParameter("selectedUserIds");

			List<Long> userIds = ArrayUtils.toList(selectedUserIds.split(","));
			List<Long> roleIds = new ArrayList<Long>();
			if (!selectedRoles.equals("")) {
				roleIds = ArrayUtils.toList(selectedRoles.split(","));
			}
			List<User> users = securityManager.getUserDao().findByIds(userIds);

			for (User user : users) {
				HibernateWebUtils.mergeByCheckedIds(user.getRoleList(), roleIds, Role.class);
				securityManager.saveUser(user);
			}

			JsonResult jsonResult = new JsonResult(true, "角色设置成功");
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 重置密码.
	 * @return json
	 * @throws Exception
	 */
	public String resetPassword() throws Exception {
		try {
			String selectedUserIds = Struts2Utils.getRequest().getParameter("selectedUserIds");
			Long[] userIds = ArrayUtils.castArray(selectedUserIds.split(","));
			//重置密码
			userManager.resetPwd(userIds);
			JsonResult jsonResult = new JsonResult(true, "重置密码成功");
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 修改密码.
	 * @return json
	 * @throws Exception
	 */
	public String modifyPassword() throws Exception {
		try {
			Long userId = ((CurrentUser) SpringSecurityUtils.getCurrentUser()).getUser().getId();
			User user = userManager.get(userId);
			String oldPwd = Struts2Utils.getRequest().getParameter("oldPwd");
			if (oldPwd.equals(user.getPassword())) {
				String newPwd = Struts2Utils.getRequest().getParameter("newPwd");
				//修改密码
				userManager.modifyPwd(userId, newPwd);
				JsonResult jsonResult = new JsonResult(true, "修改密码成功!");
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			} else {
				JsonResult jsonResult = new JsonResult(false, "原密码校验失败！");
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			}
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 同步用户、角色到工作流的.
	 * @return json
	 * @throws Exception
	 */
	public String refreshBpm() throws Exception {
		try {
			userManager.refreshBpm();

			JsonResult jsonResult = new JsonResult(true, "同步成功");
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}
}