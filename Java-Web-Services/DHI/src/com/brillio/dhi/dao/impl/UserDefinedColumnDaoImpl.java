package com.brillio.dhi.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brillio.dhi.dao.UserDefinedColumnDao;
import com.brillio.dhi.dao.entity.UserDefinedColumns;
import com.brillio.dhi.dao.entity.UserQuery;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("userDefinedColumnDao")
public class UserDefinedColumnDaoImpl implements UserDefinedColumnDao{
	
	private static final Logger LOGGER = Logger.getLogger(UserDefinedColumnDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveUserDefinedColumn(UserDefinedColumns userDefinedColumn) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(userDefinedColumn);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public boolean updateUserDefinedColumn(UserDefinedColumns userDefinedColumn) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.update(userDefinedColumn);
			
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
}
