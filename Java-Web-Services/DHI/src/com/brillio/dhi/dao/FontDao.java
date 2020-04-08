package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.FontEntity;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface FontDao {

	public boolean saveFont(FontEntity fontEntity);

	public List<FontEntity> getAllFonts() throws NoRecordFoundException;
	
	public boolean deleteFontByCategoryAndFamilyName(String fontCategory, String fontFamily);
	

}
