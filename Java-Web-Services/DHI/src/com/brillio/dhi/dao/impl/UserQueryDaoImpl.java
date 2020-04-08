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

import com.brillio.dhi.dao.UserQueryDao;
import com.brillio.dhi.dao.entity.FileUploadDataSource;
import com.brillio.dhi.dao.entity.UserQuery;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("userQueryDao")
public class UserQueryDaoImpl implements UserQueryDao{
	
	private static final Logger LOGGER = Logger.getLogger(UserQueryDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveUserQuery(UserQuery userQuery) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(userQuery);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public boolean updateUserQuery(UserQuery userQuery) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.update(userQuery);
			
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public List<UserQuery> getAllQueryByUserName(String userName)throws NoRecordFoundException {
		String hqlQuery = "FROM UserQuery";
		if(userName != null) {
			hqlQuery = "FROM UserQuery WHERE userProfile = \'" + userName +"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<UserQuery> userQueryList = query.list();
		
		if(userQueryList == null || userQueryList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return userQueryList;
	}

	@Override
	@Transactional
	public UserQuery getUserQueryByUserNameAndQuery(String userName, String userQuery)throws NoRecordFoundException {
		String hqlQuery = "FROM UserQuery";
		if(userName != null) {
			hqlQuery = "FROM UserQuery WHERE userProfile = \'" + userName +"\'" + "AND query = \'"+ userQuery+"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<UserQuery> userQueryList = query.list();
		
		if(userQueryList == null || userQueryList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return userQueryList.get(0);
	}
}
