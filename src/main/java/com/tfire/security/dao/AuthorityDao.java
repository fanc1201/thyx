package com.tfire.security.dao;

import org.springframework.stereotype.Repository;

import com.allcom.commons.hibernate.HibernateDaoEx;
import com.allcom.security.entity.Authority;

/**
 * 授权对象的泛型DAO.
 * 
 * @author dw
 */
@Repository
public class AuthorityDao extends HibernateDaoEx<Authority, Long> {
}
