package com.allcom.security.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allcom.commons.service.ServiceManager;
import com.allcom.security.dao.RoleDao;
import com.allcom.security.entity.Role;

/**
 * Role实体的管理类.
 * 
 * @author 
 */
//Spring Service Bean.
@Service
public class RoleManager extends ServiceManager<RoleDao,Role>{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(RoleManager.class);

	@Autowired
	private RoleDao roleDao;

	@Override
	public RoleDao getDao() {
		return roleDao;
	}	

	/**
	 * 查询所有角色名称
	 * @return
	 */
	public List<String> getAllRoleName(){
		List<Role> roles = this.getDao().getAll();
		List<String> roleNames = new ArrayList<String>(roles.size());
		for(Role role:roles){
			roleNames.add(role.getName());
		}
		return roleNames;
	}

}

