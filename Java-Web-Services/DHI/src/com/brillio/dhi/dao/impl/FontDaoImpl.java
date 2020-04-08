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
import com.brillio.dhi.dao.FontDao;
import com.brillio.dhi.dao.entity.FontEntity;
import com.brillio.dhi.exception.NoRecordFoundException;


@Repository("fontDao")
public class FontDaoImpl implements FontDao{

	
	private static final Logger LOGGER = Logger.getLogger(FontDaoImpl.class);
	/*
	 * This gets the session factory object for working with hibernate
	 */
	@Autowired
    private SessionFactory sessionFactory;

	
	
	@Override
	@Transactional
	public boolean saveFont(FontEntity fontEntity) {
		Session session = sessionFactory.getCurrentSession();
		try{
			session.saveOrUpdate(fontEntity);
		}catch(HibernateException e){
			e.printStackTrace();
		}
		return true;
	}

	@Override
	@Transactional
	public List<FontEntity> getAllFonts() throws NoRecordFoundException {
		String hqlQuery = "FROM FontEntity";
		
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hqlQuery);
		List<FontEntity> fontEntityList = query.list();
		
		if(fontEntityList == null || fontEntityList.size() <= 0) {
			throw new NoRecordFoundException("No record found in the database","error","dhi_data_not_found_error");
		}
		return fontEntityList;
	}

	@Override
	@Transactional
	public boolean deleteFontByCategoryAndFamilyName(String fontCategory, String fontFamily) {
		LOGGER.debug("Entering deleteFontByCategoryAndFamilyName of FontDaoImpl class");
		Session session = sessionFactory.getCurrentSession();
		FontEntity fontEntity = (FontEntity ) session.createCriteria(FontEntity.class)
                .add(Restrictions.eq(DHIConstants.FONT_CATEGORY, fontCategory))
                .add(Restrictions.eq(DHIConstants.FONT_FAMILY, fontFamily)).uniqueResult();
		session.delete(fontEntity);
		LOGGER.debug("Leaving deleteFontByCategoryAndFamilyName of FontDaoImpl class");
		return true;
	}

}
