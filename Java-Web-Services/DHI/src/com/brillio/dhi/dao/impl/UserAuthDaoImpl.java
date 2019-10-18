package com.brillio.dhi.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.dao.UserAuthDao;
import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;


@Repository
public class UserAuthDaoImpl implements UserAuthDao{

	private static final Logger LOGGER = Logger.getLogger(UserAuthDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

    
    /**
     * This method is used to save ther userProfile entity in the database
     */
    @Override
	@Transactional
	public boolean saveUserAuth(UserAuth userAuth) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.save(userAuth);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		
		return true;
	}
    
    
    @Override
    @Transactional
	public UserAuth getUserAuthByUserName(String userName) throws NoRecordFoundException   {
		LOGGER.debug("Entering getUserAuthByUserName of AuthenAuthorDaoImpl class");
		UserAuth userAuth=null;
		Session session = sessionFactory.getCurrentSession();
		userAuth = (UserAuth) session.get(UserAuth.class, userName);
		
		if(null == userAuth){
			//TODO
			throw new NoRecordFoundException("User profile not found with the provided userName : " + userName,DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
		}
		LOGGER.debug("Leaving getUserAuthByUserName of AuthenAuthorDAOimpl class");
		return userAuth;
	}
    
    
    
    @Override
	public boolean deleteUserAuthByUserName(String userName) {
		LOGGER.debug("Entering deleteUserAuthByUserName of UserAuthDaoImpl class");
		Session session = sessionFactory.getCurrentSession();
		UserAuth userAuth = (UserAuth ) session.createCriteria(UserAuth.class)
                .add(Restrictions.eq(DHIConstants.USER_AUTH_USER_NAME, userName)).uniqueResult();
		session.delete(userAuth);
		LOGGER.debug("Leaving deleteUserAuthByUserName of UserAuthDaoImpl class with respnose as : true");
		return true;
	}
    
    @Override
	@Transactional
	public UserAuth getUserNameByPhoneNumber(String phoneNumber)throws NoRecordFoundException, MissingMandatoryParameterException {
		String hqlQuery = "FROM UserAuth";
		if(phoneNumber != null) {
			hqlQuery = "FROM UserAuth WHERE phoneNumber = \'" + phoneNumber +"\'";
		}else {
			throw new MissingMandatoryParameterException("Missing required paramter(s) : phoneNumber","error","dhi_missing_required_parameter");
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<UserAuth> userAuthList = query.list();
		
		if(userAuthList == null || userAuthList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided phone-number : " + phoneNumber,"error","dhi_token_not_found_error");
		}
		return userAuthList.get(0);
	}
    
	
}
