package com.allcom.security.service;

import com.allcom.commons.service.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allcom.security.entity.Authority;
import com.allcom.security.dao.AuthorityDao;

import org.apache.log4j.Logger;

/**
 * Authority实体的管理类.
 * 
 * @author 
 */
//Spring Service Bean.
@Service
public class AuthorityManager extends ServiceManager<AuthorityDao,Authority>{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AuthorityManager.class);

	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public AuthorityDao getDao() {
		return authorityDao;
	}	
}

