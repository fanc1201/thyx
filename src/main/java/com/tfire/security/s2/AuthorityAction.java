package com.allcom.security.s2;


import java.util.ArrayList;
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
import com.allcom.security.entity.Resource;
import com.allcom.security.entity.ResourceTree;
import com.allcom.security.service.AuthorityManager;
import com.allcom.security.service.ResourceManager;

/**
 * 授权 Action.
 * 
 * @author dw 
 */
@SuppressWarnings("serial")
public class AuthorityAction extends CrudActionEx<Authority> {
	@Autowired
	private ResourceManager resourceManager;
	@Autowired
	private AuthorityManager authorityManager;
	Authority entity;

	public AuthorityAction() {
		isAuthorizeInAction = false;
		isWatch = false;
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
				String hql = "from Authority as authority";//hql查询语句
				authorityManager.search(page, hql, filterParamMap);//执行查询
			} else {//Criteria查询方式			
				List<PropertyFilter> filters = HibernateWebUtils.buildPropertyFilters(Struts2Utils.getRequest());//得到查询条件
				// 引用对象别名以及关联方式
				AliasParameter[] aliases = {};
				authorityManager.search(page, filters, aliases);//执行查询
			}

			if (isUseJson()) {// JSON数据协议
				JsonStoreResult jsonStoreResult = new JsonStoreResult(page.getResult(), page.getTotalCount());
				// 排除的属性名
				String[] excludeProperties = { "metaData", "resourceList", "resourceNames", "resourceIds" };
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
				String data = Struts2Utils.getRequest().getParameter("data");

				JSONObject jsonObject = JSONObject.fromObject(data);
				entity = (Authority) JSONObject.toBean(jsonObject, Authority.class);
				entity.setId(null);
				authorityManager.save(entity);

				JsonFormResult jsonFormResult = new JsonFormResult(true, "新建成功!", entity);
				// 排除的属性名
				String[] excludeProperties = { "metaData", "resourceList" };
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
			if (isUseJson()) {// JSON数据协议
				String data = Struts2Utils.getRequest().getParameter("data");
				//修改变动过的数据
				JSONObject jsonObject = JSONObject.fromObject(data);
				entity = (Authority) JSONObject.toBean(jsonObject, Authority.class);
				authorityManager.save(entity, PropertyType.Simple);
				//获得数据库中最新数据
				entity = authorityManager.get(entity.getId());
				JsonFormResult jsonFormResult = new JsonFormResult(true, "修改成功!", entity);

				// 排除的属性名
				String[] excludeProperties = { "metaData", "resourceList" };
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY,
						JsonConvert.toStringByExclude(jsonFormResult, excludeProperties));
				return JSON;
			} else {//JSP页面
				addActionMessage("修改成功");
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
				authorityManager.delete(Long.parseLong(selectId));

				JsonResult jsonResult = new JsonResult(true, "删除成功!");
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
				return JSON;
			} else {//JSP页面
				addActionMessage("删除成功");
				return RELOAD;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * 得到已经分配的资源或者未分配的资源(树形结构)
	 * @return json格式的数据
	 */
	public String preSetResource() {
		try {
			Authority authority = authorityManager.get(id);
			List<Resource> ownerResources = authority.getResourceList();
			List<Resource> allResources = resourceManager.getDao().getAllResource();
			for (Resource allResource : allResources) {
				for (Resource ownerResource : ownerResources) {
					if (allResource.getId() == ownerResource.getId()) {
						allResource.setChecked(true);
						//break;
					}
				}
			}

			ResourceTree tree = new ResourceTree(allResources, true);
			String jsonString = tree.getRootNode().getChildren().toString();
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
			return JSON;// JSON方式			
		} catch (Exception e) {
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, getJsonError(e.getMessage()));
			logger.error(e.getMessage());
			return JSON;
		}
	}

	/**
	 * 设置授权对象的资源.
	 * @return json格式的数据
	 */
	public String setResource() {
		try {
			String selectedResources = Struts2Utils.getRequest().getParameter("selectedResources");
			String selectedAuthIds = Struts2Utils.getRequest().getParameter("selectedAuthIds");

			List<Long> authIds = ArrayUtils.toList(selectedAuthIds.split(","));
			List<Long> resourceIds = new ArrayList<Long>();
			if (!selectedResources.equals("")) {
				resourceIds = ArrayUtils.toList(selectedResources.split(","));
			}
			List<Authority> authorities = authorityManager.getDao().findByIds(authIds);

			resourceIds.add(1L);//增加根目录
			for (Authority authority : authorities) {
				HibernateWebUtils.mergeByCheckedIds(authority.getResourceList(), resourceIds, Resource.class);
				authorityManager.getDao().save(authority);
			}

			JsonResult jsonResult = new JsonResult(true, "资源设置成功!");
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, JsonConvert.toString(jsonResult));
			return JSON;
		} catch (Exception e) {
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, getJsonError(e.getMessage()));
			logger.error(e.getMessage());
			return JSON;
		}
	}
}

