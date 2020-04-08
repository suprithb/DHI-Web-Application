package com.brillio.dhi.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.model.FontRequestModel;
import com.brillio.dhi.model.GenericResponse;
import com.brillio.dhi.service.FontService;

@Controller
public class FontController {
	
	private static final Logger LOGGER = Logger.getLogger(FontController.class);
	
	@Autowired
	FontService fontService;
	
	
	@RequestMapping(value="/assets/fonts",method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> getAvailableFonts() {
		
		List<Map<String, String>> list = null;
		try {
			list = fontService.getAllAvailableFonts();
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@RequestMapping(value="/assets/fonts",method = RequestMethod.POST)
	public @ResponseBody GenericResponse addFonts(@RequestBody List<Map<String, String>> fontMap) {
		return fontService.saveFont(fontMap);
		
	}
	
	@RequestMapping(value="/assets/fonts",method = RequestMethod.DELETE)
	public @ResponseBody GenericResponse deleteFontsByFamilyAndCategory(@RequestBody FontRequestModel fontRequestModel) {
		return fontService.deleteFontByCategoryAndFamilyName(fontRequestModel.getCategory(), fontRequestModel.getFamily());
	}
	
	

}
