package com.allcom.security.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allcom.commons.service.ServiceManager;
import com.allcom.security.dao.ResourceDao;
import com.allcom.security.entity.Resource;

/**
 * Resource实体的管理类.
 * 
 * @author 
 */
//Spring Service Bean.
@Service
public class ResourceManager extends ServiceManager<ResourceDao,Resource>{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ResourceManager.class);

	@Autowired
	private ResourceDao resourceDao;

	@Override
	public ResourceDao getDao() {
		return resourceDao;
	}	

	@Override
	/**
	 * 保存新增或修改的对象.
	 */
	public void save(Resource entity) {

		getDao().save(entity);
	}
}

