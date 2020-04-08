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

import com.brillio.dhi.dao.FileUploadDataSourceDao;
import com.brillio.dhi.dao.entity.FileUploadDataSource;
import com.brillio.dhi.exception.NoRecordFoundException;

@Repository("fileUploadDataSourceDao")
public class FileUploadDataSourceDaoImpl implements FileUploadDataSourceDao{
	
	private static final Logger LOGGER = Logger.getLogger(FileUploadDataSourceDaoImpl.class);
	
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean saveFileDataSource(FileUploadDataSource fileUploadDataSource) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(fileUploadDataSource);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public boolean updateFileDataSource(FileUploadDataSource fileUploadDataSource) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.update(fileUploadDataSource);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	@Transactional
	public List<FileUploadDataSource> getAllFileDataSource(String userName)throws NoRecordFoundException {
		String hqlQuery = "FROM FileUploadDataSource";
		if(userName != null) {
			hqlQuery = "FROM FileUploadDataSource WHERE userProfile = \'" + userName +"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FileUploadDataSource> fileDataSourceList = query.list();
		
		if(fileDataSourceList == null || fileDataSourceList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return fileDataSourceList;
	}
	
	
	
	
	@Override
	@Transactional
	public FileUploadDataSource getFileDataSource(String userName, String dataModelFileName)throws NoRecordFoundException {
		String hqlQuery = "FROM FileUploadDataSource";
		if(userName != null) {
			hqlQuery = "FROM FileUploadDataSource WHERE userProfile = \'" + userName +"\'" + "AND dataModelFileName = \'"+ dataModelFileName+"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FileUploadDataSource> fileDataSourceList = query.list();
		
		if(fileDataSourceList == null || fileDataSourceList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return fileDataSourceList.get(0);
	}
	
	@Override
	@Transactional
	public FileUploadDataSource getDefaultFileDataSourceByUserName(String userName)throws NoRecordFoundException {
		String hqlQuery = "FROM FileUploadDataSource";
		if(userName != null) {
			hqlQuery = "FROM FileUploadDataSource WHERE userProfile = \'" + userName +"\'" + "AND isActive = \'true\' AND defaultFlag = \'true\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FileUploadDataSource> fileDataSourceList = query.list();
		
		if(fileDataSourceList == null || fileDataSourceList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database by the provided user-name : " + userName,"error","dhi_data_not_found_error");
		}
		return fileDataSourceList.get(0);
	}
	
	
	@Override
	@Transactional
	public boolean updateDefaultFlagByUserName(String userName, String dataModelFilename) {
		String hqlQuery = "";
		if(userName != null) {
			hqlQuery = "UPDATE FileUploadDataSource SET defaultFlag = \'false\' " + " WHERE userProfile = \'" + userName +"\'" + "AND dataModelFileName != \'"+dataModelFilename+"\'";
		}
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		query.executeUpdate();
	
		return true;
	}
	
	

}
