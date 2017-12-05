package com.allcom.security.dao;

import org.springframework.stereotype.Repository;

import com.allcom.commons.hibernate.HibernateDaoEx;
import com.allcom.security.entity.UserConsigne;
/**
 * UserConsigne对象的泛型DAO.
 * 
 * @author 段卫
 */
@Repository
public class UserConsigneDao extends HibernateDaoEx<UserConsigne, Long> {
}
