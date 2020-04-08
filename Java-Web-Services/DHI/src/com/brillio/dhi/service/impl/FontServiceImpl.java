package com.brillio.dhi.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brillio.dhi.dao.FontDao;
import com.brillio.dhi.dao.entity.FontEntity;
import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.service.FontService;

@Service
public class FontServiceImpl implements FontService{
	
	@Autowired
	FontDao fontDao;

	
	@Override
	public GenericResponse saveFont(List<Map<String,String>> fontMap) {
		
		for(Map<String,String> map : fontMap) {
			
			String category = null;
			String family = null;
			
			for(Entry<String, String> entry : map.entrySet()) {
				if(entry.getKey() != null && !(entry.getKey().trim().equalsIgnoreCase("")) 
						&& entry.getValue() != null && !(entry.getValue().trim().equalsIgnoreCase("")) && entry.getKey().trim().equalsIgnoreCase("family")) {
					family = entry.getValue().trim();
				}
				
				if(entry.getKey() != null && !(entry.getKey().trim().equalsIgnoreCase("")) 
						&& entry.getValue() != null && !(entry.getValue().trim().equalsIgnoreCase("")) && entry.getKey().trim().equalsIgnoreCase("category")) {
					category = entry.getValue().trim();
				}	
			}
			
			if(category != null && family != null)
				fontDao.saveFont(new FontEntity(family, category));
		}
		
		
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setDescription("Successfully saved the fonts ");
		return genericResponse;
	}
	
	
	@Override
	public List<Map<String,String>> getAllAvailableFonts() throws NoRecordFoundException {
		
		List<FontEntity> fontEntityList = fontDao.getAllFonts();
		
		List<Map<String,String>> fontCategoryList = new ArrayList<Map<String,String>>();
		for(FontEntity fontEntity : fontEntityList) {
			Map<String,String> map = new HashMap<String,String>();
			map.put("category", fontEntity.getCategory());
			map.put("family", fontEntity.getFamily());
			fontCategoryList.add(map);
			
		}
		
		return fontCategoryList;
	}
	
	
	@Override
	public GenericResponse deleteFontByCategoryAndFamilyName(String category, String family) {
		
		boolean isDeleted = fontDao.deleteFontByCategoryAndFamilyName(category, family);
		
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setStatus("success");
		genericResponse.setDescription("Successfully deleted the fonts ");
		return genericResponse;
	}
	

	
	
}
