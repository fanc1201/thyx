package com.allcom.security.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.allcom.commons.hibernate.HibernateDaoEx;
import com.allcom.commons.util.ArrayUtils;
import com.allcom.security.entity.Resource;

/**
 * 受保护资源对象的泛型DAO.
 * 
 * @author dw
 */
@Repository
public class ResourceDao extends HibernateDaoEx<Resource, Long> {
	public static final String QUERY_BY_RESOURCE_WITH_AUTHORITY = "from Resource r left join fetch r.authorityList WHERE r.url is not null ORDER BY r.nodePosition ASC";

	/**
	 * 查询URL不为空的资源并预加载可访问该资源的授权信息.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getUrlResourceWithAuthorities() {
		Query query = createQuery(QUERY_BY_RESOURCE_WITH_AUTHORITY);
		return distinct(query).list();
	}

	/**
	 * 预加载授权的资源信息.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getResourceWithAuthorities(String authorityIds) {
		String queryString = "from Resource r left join fetch r.authorityList a WHERE a.id in("
				+ authorityIds + ") ORDER BY r.nodePosition ASC";
		Query query = createQuery(queryString);
		return distinct(query).list();
	}

	/**
	 * 得到可访问授权的模块资源.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getModuleResourceWithAuthorities(String authorityIds) {
		String queryString = "from Resource r left join fetch r.authorityList a WHERE (r.parentResource.id=1 or r.id=1) and a.id in("
				+ authorityIds + ") ORDER BY r.nodePosition ASC";
		Query query = createQuery(queryString);
		return distinct(query).list();
	}

	/**
	 * 得到可访问授权的模块资源.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getResourceWithAuthorities(String authorityIds, String type) {
		String queryString = "from Resource r left join fetch r.authorityList a WHERE r.resourceType=? and a.id in("
				+ authorityIds + ") ORDER BY r.nodePosition ASC";
		Query query = createQuery(queryString);
		query.setString(0, type);
		return distinct(query).list();
	}

	/**
	 * 查询所有资源.
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getAllResource() {
		String queryString = "from Resource r ORDER BY r.nodePosition ASC";
		Query query = createQuery(queryString);
		return distinct(query).list();
	}

	/**
	 * 查询所有资源，除开排除的资源.
	 * excludeResourceIds 排除的资源
	 */
	@SuppressWarnings("unchecked")
	public List<Resource> getAllResource(Long[] excludeResourceIds) {
		String ids = ArrayUtils.toString(excludeResourceIds, ",");
		String queryString = "from Resource r ORDER BY r.nodePosition ASC where r.resourceId in(" + ids + ")";
		Query query = createQuery(queryString);
		return distinct(query).list();
	}
}
