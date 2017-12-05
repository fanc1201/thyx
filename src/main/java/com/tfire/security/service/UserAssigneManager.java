package com.allcom.security.service;

import com.allcom.commons.service.ServiceManager;
import com.allcom.security.dao.UserAssigneDao;
import com.allcom.security.entity.UserAssigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;






/**
 * UserAssigne实体的管理类.
 * 
 * @author 段卫
 */
//Spring Service Bean.
@Service
public class UserAssigneManager extends ServiceManager<UserAssigneDao,UserAssigne> {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UserAssigneManager.class);

	@Autowired
	private UserAssigneDao userAssigneDao;
	
	@Override
	public UserAssigneDao getDao() {
		return userAssigneDao;
	}	

}

