package com.allcom.security.s2;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.hibernate.HibernateWebUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.cg.entity.BeanDef.PropertyType;
import com.allcom.commons.hibernate.AliasParameter;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonFormResult;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.json.JsonStoreResult;
import com.allcom.commons.util.ArrayUtils;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.security.entity.Authority;
import com.allcom.security.entity.Role;
import com.allcom.security.service.RoleManager;
import com.allcom.security.service.SecurityManager;

/**
 * Role Action.
 * 
 * @author dw
 */
@SuppressWarnings("serial")
public class RoleAction extends CrudActionEx<Role> {
	@Autowired
	private SecurityManager securityManager;
	@Autowired
	private RoleManager roleManager;
	//实体对象
	private Role entity;
	
	public RoleAction() {
		isAuthorizeInAction = false;
		isWatch = false;
	}

	// 准备函数 数据模型 //
	/**
	 * 设置数据实体.
	 */		
	@Override
	public Role getModel() {
		return entity;
	}

	/**
	 * 供prepardMethodName()函数调用. 
	 */	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			// 引用对象别名以及关联方式
			AliasParameter[] aliases = {};
			entity = roleManager.get(id, aliases);
		} else {
			entity = new Role();
		}
	}
		
	// CRUD Action 函数 //
	/**
	 * Action函数,显示Entity列表界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String list() throws Exception {
		//初始化分页Page
		initPage();
		
		try {
			// 查询结果
			if (isUseHql()) {//Hql查询方式
				@SuppressWarnings("unchecked")
				Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(Struts2Utils.getRequest(),
						"filter_");//得到查询参数
				String hql = "from Role as role";//hql查询语句
				roleManager.search(page, hql, filterParamMap);//执行查询
			} else {//Criteria查询方式			
				List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(Struts2Utils.getRequest());//得到查询条件
				// 引用对象别名以及关联方式
				AliasParameter[] aliases = {};
				roleManager.search(page, filters, aliases);//执行查询
			}

			if (isUseJson()) {// JSON数据协议
				JsonStoreResult jsonStoreResult = new JsonStoreResult(page.getResult(), page.getTotalCount());
				// 排除的属性名
				String[] excludeProperties = { "metaData", "userRoles", "authorityList" };
				String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("page", page);
				return SUCCESS;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * Action函数,新增Entity. 
	 * return json or struts jsp.
	 */
	@Override	 
	public String save() throws Exception {
		try {
			if (isUseJson()) {// JSON数据协议
				String items = Struts2Utils.getRequest().getParameter("data");

				JSONObject jsonObject = JSONObject.fromObject(items);
				entity = (Role) JSONObject.toBean(jsonObject, Role.class);
				entity.setId(null);
				roleManager.save(entity);

				JsonFormResult jsonFormResult = new JsonFormResult(true, "新建成功", entity);
				// 排除的属性名
				String[] excludeProperties = { "metaData", "userRoles", "authorityList" };
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY,
						JsonConvert.toStringByExclude(jsonFormResult, excludeProperties));

				return JSON;
			} else {//JSP页面
				addActionMessage("新建成功");
				return RELOAD;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/*
	 * Action函数,修改Entity. 
	 * return json or struts jsp.
	 */	
	@Override
	public String update() throws Exception {
		try {
			prepareModel();

			if (isUseJson()) {// JSON数据协议
				String data = Struts2Utils.getRequest().getParameter("data");

				//修改变动过的数据
				JSONObject jsonObject = JSONObject.fromObject(data);
				entity = (Role) JSONObject.toBean(jsonObject, Role.class);
				roleManager.save(entity, PropertyType.Simple);
				//获得数据库中最新数据
				entity = roleManager.get(entity.getId());
				JsonFormResult jsonFormResult = new JsonFormResult(true, "修改成功!", entity);

				// 排除的属性名
				String[] excludeProperties = { "metaData", "userRoles", "authorityList" };
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY,
						JsonConvert.toStringByExclude(jsonFormResult, excludeProperties));
				return JSON;
			} else {//JSP页面
				addActionMessage("修改成功!");
				return RELOAD;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * Action函数,删除Entity.
	 * return json or struts jsp.
	 */
	@Override	
	public String delete() throws Exception {
		try {
			if (isUseJson()) {// JSON数据协议
				String selectId = Struts2Utils.getRequest().getParameter("data");
				roleManager.delete(Long.parseLong(selectId));

				JsonResult jsonResult = new JsonResult(true, "删除成功!");
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
				return JSON;
			} else {//JSP页面
				addActionMessage("删除成功!");
				return RELOAD;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 得到已经分配的授权或者未分配的授权.
	 * @return json
	 * @throws Exception
	 */
	public String preSetAuth() throws Exception {
		String type = Struts2Utils.getRequest().getParameter("type");

		try {
			List<Authority> authes;
			if (type.equals("from")) {//未分配的角色
				Role role = roleManager.get(id);

				List<Authority> allAuthes = securityManager.getAuthorityDao().find(Collections.EMPTY_LIST);
				authes = new ArrayList<Authority>();
				List<Authority> ownerAuthes = role.getAuthorityList();
				for (Authority allAuth : allAuthes) {
					boolean flag = false;
					for (Authority ownerAuth : ownerAuthes) {
						if (allAuth.getId() == ownerAuth.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						authes.add(allAuth);
					}
				}
			} else if (type.equals("to")) {//已经分配的角色
				Role role = roleManager.get(id);
				authes = role.getAuthorityList();
			} else {
				authes = securityManager.getAuthorityDao().find(Collections.EMPTY_LIST);
			}
			JsonStoreResult jsonStoreResult = new JsonStoreResult(authes, authes.size());
			// 排除的属性名
			String[] excludeProperties = { "description", "memo", "metaData", "resourceList" };
			String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties);
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
			return JSON;// JSON方式			
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 设置授权 
	 * @return json
	 * @throws Exception
	 */
	public String setAuth() throws Exception {
		try {
			String selectedAuthes = Struts2Utils.getRequest().getParameter("selectedAuthes");
			String selectedRoleIds = Struts2Utils.getRequest().getParameter("selectedRoleIds");

			List<Long> roleIds = ArrayUtils.toList(selectedRoleIds.split(","));
			List<Long> authIds = Collections.EMPTY_LIST;
			if (!selectedAuthes.equals("")) {
				authIds = ArrayUtils.toList(selectedAuthes.split(","));
			}
			List<Role> roles = securityManager.getRoleDao().findByIds(roleIds);

			for (Role role : roles) {
				HibernateWebUtils.mergeByCheckedIds(role.getAuthorityList(), authIds, Authority.class);
				securityManager.saveRole(role);
			}

			JsonResult jsonResult = new JsonResult(true, "授权设置成功");
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}
}

