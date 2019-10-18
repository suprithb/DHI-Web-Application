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

import com.brillio.dhi.dao.StoryModelMapDao;
import com.brillio.dhi.dao.entity.StoryModelMap;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("storyModelMapDao")
public class StoryModelMapDaoImpl implements StoryModelMapDao{
	
	private static final Logger LOGGER = Logger.getLogger(StoryModelMapDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveStoryModelMap(StoryModelMap storyModelMap) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(storyModelMap);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public boolean updateStoryModelMap(StoryModelMap storyModelMap) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.update(storyModelMap);
			
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public List<StoryModelMap> getAllStoryUserName(String userName)throws NoRecordFoundException {
		String hqlQuery = "FROM StoryModelMap";
		if(userName != null) {
			hqlQuery = "FROM StoryModelMap WHERE userProfile = \'" + userName +"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<StoryModelMap> storyModelMapList = query.list();
		
		if(storyModelMapList == null || storyModelMapList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return storyModelMapList;
	}
	
	
	@Override
	@Transactional
	public StoryModelMap getStoryUserNameAndStoryName(String userName, String storyTitle)throws NoRecordFoundException {
		String hqlQuery = "FROM StoryModelMap";
		if(userName != null) {
			hqlQuery = "FROM StoryModelMap WHERE userProfile = \'" + userName +"\'" + "AND storyTitle = \'"+ storyTitle+"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<StoryModelMap> storyModelMapList = query.list();
		
		if(storyModelMapList == null || storyModelMapList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return storyModelMapList.get(0);
	}
	
	
	

}
