package com.allcom.security.dao;

import org.springframework.stereotype.Repository;

import com.allcom.commons.hibernate.HibernateDaoEx;
import com.allcom.security.entity.User;

/**
 * 用户对象的泛型DAO类.
 * 
 * @author calvin
 */
@Repository
public class UserDao extends HibernateDaoEx<User, Long> {
}
