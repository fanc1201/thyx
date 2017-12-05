package com.allcom.security.service;

import com.allcom.commons.service.ServiceManager;
import com.allcom.security.dao.UserConsigneDao;
import com.allcom.security.entity.UserConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.log4j.Logger;






/**
 * UserConsigne实体的管理类.
 * 
 * @author 段卫
 */
//Spring Service Bean.
@Service
public class UserConsigneManager extends ServiceManager<UserConsigneDao,UserConsigne> {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UserConsigneManager.class);

	@Autowired
	private UserConsigneDao userConsigneDao;
	
	@Override
	public UserConsigneDao getDao() {
		return userConsigneDao;
	}	

}

