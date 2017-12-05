package com.allcom.security.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.security.springsecurity.ResourceDetailsService;

import com.allcom.security.entity.Resource;

/**
 * 从数据库查询URL--授权定义Map的实现类.
 * 
 * @author dw
 */
@Transactional(readOnly = true)
public class ResourceDetailsServiceImpl implements ResourceDetailsService {
	@Autowired
	private SecurityManager securityManager;

	/**
	 * @see ResourceDetailsService#getRequestMap()
	 */
	public LinkedHashMap<String, String> getRequestMap() throws Exception {
		List<Resource> resourceList = securityManager.getUrlResourceWithAuthorities();

		LinkedHashMap<String, String> requestMap = new LinkedHashMap<String, String>(resourceList.size());
		for (Resource resource : resourceList) {
			if (resource.getUrl() != null && !resource.getUrl().equals("")) {
				requestMap.put(resource.getUrl(), resource.getAuthNames());
			}
			if (resource.getPostUrl() != null && !resource.getPostUrl().equals("")) {
				String[] postUrls = resource.getPostUrl().split(",");
				for (int i = 0; i < postUrls.length; i++) {
					if (requestMap.containsKey(postUrls[i])) {
						String value = requestMap.get(postUrls[i]);
						if (resource.getAuthNames() != null && !resource.getAuthNames().equals("")) {
							value = value + "," + resource.getAuthNames();
							requestMap.put(postUrls[i], value);
						}
					} else {
						if (resource.getAuthNames() != null && !resource.getAuthNames().equals("")) {
							requestMap.put(postUrls[i], resource.getAuthNames());
						}
					}
				}
			}
		}
		return requestMap;
	}
}
