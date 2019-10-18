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

import com.brillio.dhi.dao.UserFavouriteDao;
import com.brillio.dhi.dao.entity.FavouriteEntity;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("userCalculatorInformationDao")
public class UserFavouriteInformationDaoImpl implements UserFavouriteDao{
	
	private static final Logger LOGGER = Logger.getLogger(UserFavouriteInformationDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveUserFavouriteInformation(FavouriteEntity userFavouriteInformation) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.save(userFavouriteInformation);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		
		return true;
	}

/*	@Override
	public List<FavouriteEntity> getAllUserCalculatorInformation() {
		String hqlQuery = "FROM UserCalculatorInformationEntity";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FavouriteEntity> userCalculatorInformationList = query.list();
		return userCalculatorInformationList;
	}*/

	@Override
	@Transactional
	public List<FavouriteEntity> getUserFavouriteInformationByName(String name)
			throws NoRecordFoundException {
		String hqlQuery = "FROM FavouriteEntity";
		if(name != null) {
			hqlQuery = "FROM FavouriteEntity WHERE userName = \'" + name +"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FavouriteEntity> userFavouriteInformationList = query.list();
		
		if(userFavouriteInformationList == null || userFavouriteInformationList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + name,"error","dhi_data_not_found_error");
		}
		return userFavouriteInformationList;
	}
	
	@Override
	@Transactional
	public boolean deleteUserFavouriteInformationByName(String name, String fullUrl) throws MissingMandatoryParameterException{
		String hqlQuery = "";
		if(name != null &&  !(name.trim().equalsIgnoreCase("")) && fullUrl != null &&  !(fullUrl.trim().equalsIgnoreCase(""))) {
			hqlQuery = "DELETE FavouriteEntity WHERE userName = \'" + name +"\' AND fileName = \'" + fullUrl +"\'" ;
		}else {
			throw new MissingMandatoryParameterException("Missing mandatory parameter(s) : fullUrl, userName","error","dhi_missing_required_parameter");
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		int numberOfRecordDeleted  = query.executeUpdate();
		
		if(numberOfRecordDeleted  < 1 ) {
			return false;
		}
		return true;
	}
	

}
