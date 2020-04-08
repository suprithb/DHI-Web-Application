package com.brillio.dhi.dao.impl;



import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brillio.dhi.constants.DHIConstants;
import com.brillio.dhi.dao.UserProfileDao;
import com.brillio.dhi.dao.entity.UserProfileEntity;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("usrProfileDao")
public class UserProfileDaoImpl implements UserProfileDao{
	
	
	private static final Logger LOGGER = Logger.getLogger(UserProfileDaoImpl.class);
	
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
	public boolean saveUserProfile(UserProfileEntity userProfile) {
		Session session =  sessionFactory.getCurrentSession();
		try{
			session.save(userProfile);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		
		return true;
	}
    
	@Override
	public boolean updateUserProfile(UserProfileEntity userProfile) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	@Transactional
	public List<UserProfileEntity> getAllUserProfiles() {
		Session session =  sessionFactory.getCurrentSession();
		 List<UserProfileEntity> userProfileList = null;
	     Criteria queryCriteria = session.createCriteria(UserProfileEntity.class);
	     queryCriteria.addOrder( Order.asc(DHIConstants.USER_PROFILE_USER_NAME) );
	     try{
	    	 userProfileList =  queryCriteria.list();
	     }catch(HibernateException e ){
	    	 e.printStackTrace();
	     }
	     
	     //session.close();
		return userProfileList;
	}

	@Override
	@Transactional
	public UserProfileEntity getUserProfileByUserName(String userName) throws NoRecordFoundException {
		LOGGER.debug("Entering getUserProfileByUserName of UserProfileDaoImpl class");
		UserProfileEntity userProfile=null;
		Session session =  sessionFactory.getCurrentSession();
		userProfile = (UserProfileEntity) session.get(UserProfileEntity.class, userName);
		
		if(null == userProfile){
			//TODO
			throw new NoRecordFoundException("User profile not found for user-name : " + userName,DHIConstants.ERROR,DHIConstants.DHI_USER_NOT_FOUND_ERROR);
		}
		LOGGER.debug("Leaving getUserProfileByUserName of UserProfileDaoImpl class");
		return userProfile;
	}
	
	
	
	@Override
    @Transactional
	public UserProfileEntity getUserProfileByPhoneNumber(String phoneNumber) throws NoRecordFoundException   {
		LOGGER.debug("Entering getUserProfileByPhoneNumber of UserProfileDaoImpl class");
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserProfileEntity.class);
		criteria.add(Restrictions.eq(DHIConstants.USER_PROFILE_PHONE_NUMBER, phoneNumber));
		
		UserProfileEntity userProfile = (UserProfileEntity) criteria.uniqueResult();
		if(null == userProfile){
			//TODO
			throw new NoRecordFoundException("User Profile not found with the provided PhoneNumber : " + phoneNumber,DHIConstants.ERROR,DHIConstants.DHI_BAD_REQUEST);
		}
		LOGGER.debug("Leaving getUserProfileByPhoneNumber of UserProfileDaoImpl class");
		return userProfile;
	}
	

	@Override
	public boolean deleteUserProfileByUserName(String userName) {
		LOGGER.debug("Entering deleteUserProfileByUserName of UserProfileDaoImpl class");
		Session session = sessionFactory.getCurrentSession();
		UserProfileEntity userProfile = (UserProfileEntity ) session.createCriteria(UserProfileEntity.class)
                .add(Restrictions.eq(DHIConstants.USER_PROFILE_USER_NAME, userName)).uniqueResult();
		session.delete(userProfile);
		LOGGER.debug("Leaving deleteUserProfileByUserName of UserProfileDaoImpl class with respnose as : true");
		return true;
	}

    
    

}
