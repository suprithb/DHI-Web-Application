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

import com.brillio.dhi.dao.UserTokenDao;
import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("userTokenDao")
public class UserTokenImpl implements UserTokenDao{
	
	private static final Logger LOGGER = Logger.getLogger(UserTokenImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveUserToken(UserToken userToken) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.save(userToken);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		
		return true;
	}

	
	@Override
	@Transactional
	public boolean saveOrUpdateUserToken(UserToken userToken) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(userToken);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	@Override
	@Transactional
	public UserToken getUserTokenByName(String name)throws NoRecordFoundException {
		String hqlQuery = "FROM UserToken";
		if(name != null) {
			hqlQuery = "FROM UserToken WHERE userName = \'" + name +"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<UserToken> userTokenList = query.list();
		
		if(userTokenList == null || userTokenList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + name,"error","dhi_token_not_found_error");
		}
		return userTokenList.get(0);
	}
	
	@Override
	@Transactional
	public UserToken getUserTokenByChatRoomId(String chatRoomId)throws NoRecordFoundException, MissingMandatoryParameterException {
		String hqlQuery = "FROM UserToken";
		if(chatRoomId != null) {
			hqlQuery = "FROM UserToken WHERE chatRoomId = \'" + chatRoomId +"\'";
		}else {
			throw new MissingMandatoryParameterException("Missing required paramter(s) : chat-room-id","error","dhi_missing_required_parameter");
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<UserToken> userTokenList = query.list();
		
		if(userTokenList == null || userTokenList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided chat-room-id : " + chatRoomId,"error","dhi_token_not_found_error");
		}
		return userTokenList.get(0);
	}

}
