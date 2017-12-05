package com.allcom.security.s2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.hibernate.HibernateWebUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.commons.AppContext;
import com.allcom.commons.hibernate.AliasParameter;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonFormResult;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.json.JsonStoreResult;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.log.entity.OperLog;
import com.allcom.security.entity.UserConsigne;
import com.allcom.security.service.UserConsigneManager;


/**
 * UserConsigne Action.
 * 
 * @author 段卫 
 */
@SuppressWarnings("serial")
public class UserConsigneAction extends CrudActionEx<UserConsigne> {
	@Autowired
	private UserConsigneManager userConsigneManager;
	//实体对象
	private UserConsigne userConsigne;
	//包含的属性名
	private String[] includeProperties = { "id", "consigner", "consignee" };
	//排除的属性名
	private String[] excludeProperties = {  };
	// 引用对象别名以及关联方式
	private AliasParameter[] aliases = { 
	};		
	
	public UserConsigneAction() {
		isAuthorizeInAction = false;
		isWatch = true;
	}		
			
	// 准备函数 数据模型 //
	/**
	 * 设置数据实体.
	 */		
	@Override
	public UserConsigne getModel() {
		return userConsigne;
	}	 
	public UserConsigne getUserConsigne() {
		return userConsigne;
	}	
	public void setUserConsigne(UserConsigne userConsigne) {
		this.userConsigne = userConsigne;
	}		
	
	/**
	 * 供prepardMethodName()函数调用. 
	 */	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			userConsigne = userConsigneManager.get(id, aliases);
			
		} else {
			userConsigne = new UserConsigne();
		}
	}
		
	// CRUD Action 函数 //
	/**
	 * Action函数,显示新增或修改UserConsigne界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String input() throws Exception {
		String result = prepareInput();//预先处理
		if (!"".equals(result))
			return result;

		OperLog operLog = startWatch("UserConsigneAction.input()");//启动计时

		prepareModel();
		
		try {
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "", "userConsigne.", includeProperties, userConsigne);
				String jsonString = JsonConvert.toStringByExclude(jsonFormResult, excludeProperties, nullProperties,valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				endWatch(true, operLog, jsonString);//结束计时					
				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("userConsigne", userConsigne);
				//logger.debug(userConsigne.toString());
				endWatch(true, operLog, userConsigne.toString());//结束计时					
				return INPUT;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			endWatch(false, operLog, outParam, e.getMessage());//结束计时			
			return outParam;
		}	
	}
		
	/**
	 * Action函数,显示UserConsigne列表界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String list() throws Exception {
		String result = prepareList();//预先处理
		if (!"".equals(result))
			return result;
		
		OperLog operLog = startWatch("UserConsigneAction.list()");//启动计时
		
		//初始化分页Page
		initPage();
		
		try{
			//查询结果
			if (isUseHql()) {//Hql查询方式
				@SuppressWarnings("unchecked")
				Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(Struts2Utils.getRequest(),"filter_");//得到查询参数
				String hql = "from UserConsigne as userConsigne";//hql查询语句
				userConsigneManager.search(page, hql, filterParamMap);//执行查询
			} else {//Criteria查询方式			
				List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(Struts2Utils.getRequest());//得到查询条件
				userConsigneManager.search(page, filters, aliases);//执行查询
			}
			
			if (isUseJson()) {// JSON数据协议
				JsonStoreResult jsonStoreResult = new JsonStoreResult(includeProperties, page.getResult(), page.getTotalCount());
				//转为json字符
				String jsonString = JsonConvert.toStringByExclude(jsonStoreResult, excludeProperties , nullProperties , valueProperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				endWatch(true, operLog, jsonString);//结束计时		
				return JSON;//JSON方式
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("page", page);
				//logger.debug(page.toString());
				endWatch(true, operLog, userConsigne.toString());//结束计时				
				return SUCCESS;//JSP方式
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			endWatch(false, operLog, outParam, e.getMessage());//结束计时			
			return outParam;
		}
	}
	
	/**
	 * Action函数,新增UserConsigne. 
	 * return json or struts jsp.
	 */
	@Override	 
	public String save() throws Exception {
		String result = prepareSave();//预先处理
		if (!"".equals(result))
			return result;

		OperLog operLog = startWatch("UserConsigneAction.save()");//启动计时
		
		try {
			String[] consignees = userConsigne.getConsignee().split(",");
			for (String consignee : consignees) {
				UserConsigne _userConsigne = new UserConsigne();
				_userConsigne.setConsigner(AppContext.getCurrentUser().getUsername());
				_userConsigne.setConsignee(consignee);
				userConsigneManager.save(_userConsigne);
			}
				
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "添加成功!");
				jsonFormResult.setData("{\"id\":" + userConsigne.getId() + "}");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				if (isWatch) {
					//记录操作后的entity
					operLog.setAfterEntity(JsonConvert.toStringByExclude(userConsigne, excludeProperties, nullProperties,
							valueProperties));
					endWatch(true, operLog, jsonString);//结束计时
				}
				return JSON;
			} else {//JSP页面
				addActionMessage("添加成功!");
				//logger.debug(userConsigne.toString());
				endWatch(true, operLog, userConsigne.toString());//结束计时			
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			endWatch(false, operLog, outParam, e.getMessage());//结束计时			
			return outParam;
		}		
	}

	/*
	 * Action函数,修改UserConsigne. 
	 * return json or struts jsp.
	 */	
	@Override
	public String update() throws Exception {
		return null;
	}

			
	/**
	 * Action函数,删除UserConsigne.
	 * return json or struts jsp.
	 */
	@Override	
	public String delete() throws Exception {
		String result = prepareDelete();//预先处理
		if (!"".equals(result))
			return result;

		OperLog operLog = startWatch("UserConsigneAction.delete()");//启动计时
					
		try {
			String selectIdString = Struts2Utils.getRequest().getParameter("selectIDs");
			String[] selectedIds = selectIdString.split(",");
			StringBuffer preValues = new StringBuffer();
			preValues.append("[");
			for (String selectId : selectedIds) {
				UserConsigne dbUserConsigne = userConsigneManager.get(Long.parseLong(selectId),aliases);
				if (isWatch) {
					preValues.append(JsonConvert.toStringByExclude(dbUserConsigne, excludeProperties, nullProperties,
							valueProperties));
					preValues.append(",");
				}
				userConsigneManager.delete(dbUserConsigne);
			}
			if (isWatch) {//记录操作前的userConsigne
				preValues.append("]");
				operLog.setBeforeEntity(preValues.toString());
			}			
			if (isUseJson()) {// JSON数据协议
				JsonResult jsonResult = new JsonFormResult(true, "删除成功!");
				//转为json字符
				String jsonString = JsonConvert.toString(jsonResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				//logger.debug(jsonString);
				endWatch(true, operLog, jsonString);//结束计时					
				return JSON;
			} else {//JSP页面
				addActionMessage("删除成功!");
				endWatch(true, operLog, userConsigne.toString());//结束计时					
				return RELOAD;
			}
		} catch (Exception e) {
			String outParam = handleException(e, e.getMessage());
			endWatch(false, operLog, outParam, e.getMessage());//结束计时			
			return outParam;
		}			
	}	
}
