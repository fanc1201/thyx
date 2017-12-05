package com.allcom.security.s2;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.intercept.web.FilterInvocationDefinitionSource;
import org.springframework.security.intercept.web.FilterSecurityInterceptor;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springside.modules.utils.ReflectionUtils;
import org.springside.modules.web.struts2.Struts2Utils;

import com.allcom.cg.entity.BeanDef.PropertyType;
import com.allcom.commons.AppContext;
import com.allcom.commons.json.JsonConvert;
import com.allcom.commons.json.JsonFormResult;
import com.allcom.commons.json.JsonResult;
import com.allcom.commons.util.ArrayUtils;
import com.allcom.commons.util.tree.TreeNode;
import com.allcom.commons.web.struts.CrudActionEx;
import com.allcom.security.entity.Authority;
import com.allcom.security.entity.Resource;
import com.allcom.security.entity.ResourceTree;
import com.allcom.security.service.ResourceManager;
import com.allcom.security.service.UserManager;

/**
 * Resource Action.
 * 
 * @author 自动生成   XX修改
 */
@SuppressWarnings("serial")
public class ResourceAction extends CrudActionEx<Resource> {
	@Autowired
	private ResourceManager resourceManager;
	//实体对象
	private Resource entity;
	
	public ResourceAction() {
		isAuthorizeInAction = false;
		isWatch = false;
	}

	// 准备函数 数据模型 //
	/**
	 * 设置数据实体.
	 */		
	@Override
	public Resource getModel() {
		return entity;
	}

	public Resource getEntity() {
		return entity;
	}

	public void setEntity(Resource entity) {
		this.entity = entity;
	}

	/**
	 * 供prepardMethodName()函数调用. 
	 */	
	@Override
	protected void prepareModel() throws Exception {
		if (id != null) {
			entity = resourceManager.get(id);

			Resource dbResurce = resourceManager.get(entity.getParentResource().getId());
			Resource parentResource = new Resource();
			parentResource.setId(dbResurce.getId());
			parentResource.setName(dbResurce.getName());
			entity.setParentResource(parentResource);
		} else {
			entity = new Resource();
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
		String result = prepareInput();//预先处理
		if (!"".equals(result))
			return result;

		try {
			if (isUseJson()) {// JSON数据协议
				String[] properties = { "id", "url", "postUrl", "properties", "name", "nodePosition", "nodeProperties",
						"memo", "resourceType",
						"parentResource.id", "parentResource.name" };
				JsonFormResult jsonFormResult = new JsonFormResult(true, "", NAME_PREFIX, properties, entity);
				//排除的属性名
				String[] excludeProperties = { "authorityList" };
				//对象为空返回{},而不是null
				Map<Class, Object> nullroperties = new HashMap<Class, Object>();
				nullroperties.put(Resource.class, "{}");
				String jsonString = JsonConvert.toStringByExclude(jsonFormResult, excludeProperties, nullroperties);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("entity", entity);
				return INPUT;
			}
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}

	/**
	 * Action函数,显示Entity列表界面.
	 * return json or struts jsp.
	 */	
	@Override
	public String list() throws Exception {
		try {
			String type = "1";
			if (Struts2Utils.getParameter("type") != null) {
				type = Struts2Utils.getParameter("type");
			}
			List<Resource> allResources;
			if (type.equals("2")) {//2-选择自己的模块资源
				Set<Authority> authSet = UserManager.obtainAuthority(AppContext.getCurrentUser().getUser());
				String authorityIds = ReflectionUtils.fetchElementPropertyToString(authSet, "id", ",");
				allResources = resourceManager.getDao().getModuleResourceWithAuthorities(authorityIds);
			} else if (type.equals("3")) {//选择自己的操作资源
				Set<Authority> authSet = UserManager.obtainAuthority(AppContext.getCurrentUser().getUser());
				String authorityIds = ReflectionUtils.fetchElementPropertyToString(authSet, "id", ",");
				allResources = resourceManager.getDao().getResourceWithAuthorities(authorityIds, "menu");
			} else {
				allResources = resourceManager.getDao().getAllResource();
			}

			boolean needCheckbox = false;//需要选择框
			if(Struts2Utils.getParameter("needCheckbox")!=null){
				needCheckbox = Boolean.valueOf(Struts2Utils.getParameter("needCheckbox"));
			}
			boolean needRoot = false;//需要根目录
			if (Struts2Utils.getParameter("needRoot") != null) {
				needRoot = Boolean.valueOf(Struts2Utils.getParameter("needRoot"));
			}

			ResourceTree tree;
			String excludeId = Struts2Utils.getParameter("excludeIds");
			if (excludeId != null) {
				String[] excludeIds = StringUtils.splitByWholeSeparator(excludeId, ",");
				tree = new ResourceTree(allResources, needCheckbox, ArrayUtils.toList(excludeIds));
			} else {
				tree = new ResourceTree(allResources, needCheckbox);
			}

			if (type.equals("3")) {//只有最后一级子节点，才可以选择，其余设置为无效
				tree.setRootNode(tree.getTreeNode(Struts2Utils.getParameter("parentResourceId")));
				List<TreeNode> treeNodes = tree.getRootNode().getAllChildren();
				for (TreeNode treeNode : treeNodes) {
					if (treeNode.isLeaf()) {
						treeNode.setDisabled(false);
					} else {
						treeNode.setDisabled(true);
					}
				}
			}

			if (isUseJson()) {// JSON数据协议
				String jsonString;
				if (needRoot) {
					jsonString = tree.toString();
				} else {
					jsonString = tree.getRootNode().getChildren().toString();
				}

				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);

				return JSON;
			} else {//JSP页面
				Struts2Utils.getRequest().setAttribute("tree", tree);
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
		String result = prepareSave();//预先处理
		if (!"".equals(result))
			return result;

		try {
			resourceManager.save(entity);
			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "添加成功!");
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
				return JSON;
			} else {//JSP页面
				addActionMessage("添加成功!");
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
			String result = prepareUpdate();//预先处理
			if (!"".equals(result))
				return result;

			//只保存简单属性和应用对象，不保存集合对象
			String[] excludePropertyNames = { "checked" };
			Map<String, Object> extraProperties = new HashMap<String, Object>();
			extraProperties.put("parentResource.id", entity.getParentResource().getId());
			resourceManager.save(entity, PropertyType.Simple, excludePropertyNames, extraProperties);

			if (isUseJson()) {// JSON数据协议
				JsonFormResult jsonFormResult = new JsonFormResult(true, "修改成功");
				String jsonString = JsonConvert.toString(jsonFormResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
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
			String result = prepareDelete();//预先处理
			if (!"".equals(result))
				return result;

			String selectIdString = Struts2Utils.getRequest().getParameter("selectIDs");
			String[] selectedIds = selectIdString.split(",");

			for (int i = selectedIds.length - 1; i >= 0; i--) {
				resourceManager.delete(Long.parseLong(selectedIds[i]));
			}
			if (isUseJson()) {// JSON数据协议
				JsonResult jsonResult = new JsonFormResult(true, "删除成功");
				String jsonString = JsonConvert.toString(jsonResult);
				Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, jsonString);
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
	 * 同步资源.从数据库中刷新到内存中.
	 * @return json.
	 */
	public String refresh() throws Exception{

		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
					.getServletContext());
			FactoryBean factoryBean = (FactoryBean) ctx.getBean("&databaseDefinitionSource");
			FilterInvocationDefinitionSource fids = (FilterInvocationDefinitionSource) factoryBean.getObject();
			FilterSecurityInterceptor filter = (FilterSecurityInterceptor) ctx.getBean("filterSecurityInterceptor");
			filter.setObjectDefinitionSource(fids);
			Struts2Utils.getRequest().setAttribute(JSON_RESULT_KEY, getJsonSuccess("同步资源成功！"));
			return JSON;
		} catch (Exception e) {
			return handleException(e, e.getMessage());
		}
	}
}

