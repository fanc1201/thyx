package com.allcom.security.s2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.hibernate.HibernateWebUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.commons.hibernate.AliasParameter;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonFormResult;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.json.JsonStoreResult;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.security.entity.UserAssigne;
import com.allcom.security.service.UserAssigneManager;


/**
 * UserAssigne Action.
 * 
 * @author 段卫 
 */
@SuppressWarnings("serial")
public class UserAssigneAction extends CrudActionEx<UserAssigne> {
	@Autowired
	private UserAssigneManager userAssigneManager;
	//实体对象
	private UserAssigne userAssigne;
	//包含的属性名
	private String[] includeProperties = { "id", "assigner", "assignee" };
	//排除的属性名
	private String[] excludeProperties = {  };
	// 引用对象别名以及关联方式
	private AliasParameter[] aliases = { 
	};		
	
	public UserAssigneAction() {
		isAuthorizeInAction = false;
		isWatch = false;
	}		
			
	// 准备函数 数据模型 //
	/**
	 * 设置数据实体.
	 */		
	@Override
	public UserAssigne getModel() {
		return userAssigne;
	}	 
	public UserAssigne getUserAssigne() {
		return userAssigne;
	}	
	public void setUserAssigne(UserAssigne userAssigne) {
		this.userAssigne = userAssigne;
	}		
	
	/**
	 * 供prepardMethodName()函数调用. 
	 */	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			userAssigne = userAssigneManager.get(id, aliases);
			
		} else {
			userAssigne = new UserAssigne();
		}
	}
		
	// CRUD Action 函数 //
	/**
	 * Action函数,显示新增或修改UserAssigne界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String input() throws Exception {
		String result = prepareInput();//预先处理
		if (!"".equals(result))
			return result;

		prepareModel();
		
		try {
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "", "userAssigne.", includeProperties, userAssigne);
				String jsonString = JsonConvert.toStringByExclude(jsonFormResult, excludeProperties, nullProperties,valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("userAssigne", userAssigne);
				return INPUT;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}	
	}
		
	/**
	 * Action函数,显示UserAssigne列表界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String list() throws Exception {
		String result = prepareList();//预先处理
		if (!"".equals(result))
			return result;
		
		//初始化分页Page
		initPage();
		
		try{
			//查询结果
			if (isUseHql()) {//Hql查询方式
				@SuppressWarnings("unchecked")
				Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(Struts2Utils.getRequest(),"filter_");//得到查询参数
				String hql = "from UserAssigne as userAssigne";//hql查询语句
				userAssigneManager.search(page, hql, filterParamMap);//执行查询
			} else {//Criteria查询方式			
				List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(Struts2Utils.getRequest());//得到查询条件
				userAssigneManager.search(page, filters, aliases);//执行查询
			}
			
			if (isUseJson()) {// JSON数据协议
				JsonStoreResult jsonStoreResult = new JsonStoreResult(includeProperties, page.getResult(), page.getTotalCount());
				//转为json字符
				String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties , nullProperties , valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				return JSON;//JSON方式
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("page", page);
				return SUCCESS;//JSP方式
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}
	}
	
	/**
	 * Action函数,新增UserAssigne. 
	 * return json or struts jsp.
	 */
	@Override	 
	public String save() throws Exception {
		String result = prepareSave();//预先处理
		if (!"".equals(result))
			return result;
		
		try {
			String[] assignees = userAssigne.getAssignee().split(",");
			for (String assignee : assignees) {
				UserAssigne _userAssigne = new UserAssigne();
				_userAssigne.setAssigner(userAssigne.getAssigner());
				_userAssigne.setAssignee(assignee);
				userAssigneManager.save(_userAssigne);
			}
			
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "添加成功!");
				jsonFormResult.setData("{\"id\":" + userAssigne.getId() + "}");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);

				return JSON;
			} else {//JSP页面
				addActionMessage("添加成功!");
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			return outParam;
		}		
	}

	/*
	 * Action函数,修改UserAssigne. 
	 * return json or struts jsp.
	 */	
	@Override
	public String update() throws Exception {
		return null;
	}

			
	/**
	 * Action函数,删除UserAssigne.
	 * return json or struts jsp.
	 */
	@Override	
	public String delete() throws Exception {
		String result = prepareDelete();//预先处理
		if (!"".equals(result))
			return result;
					
		try {
			String selectIdString = Struts2Utils.getRequest().getParameter("selectIDs");
			String[] selectedIds = selectIdString.split(",");
			StringBuffer preValues = new StringBuffer();
			preValues.append("[");
			for (String selectId : selectedIds) {
				UserAssigne dbUserAssigne = userAssigneManager.get(Long.parseLong(selectId),aliases);
				userAssigneManager.delete(dbUserAssigne);
			}
			if (isUseJson()) {// JSON数据协议
				JsonResult jsonResult = new JsonFormResult(true, "删除成功!");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
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
}
