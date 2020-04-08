package com.brillio.dhi.service;

import java.util.List;
import java.util.Map;

import com.brillio.dhi.exception.NoRecordFoundException;
import com.brillio.dhi.model.GenericResponse;

public interface FontService {

	GenericResponse saveFont(List<Map<String, String>> fontMap);

	List<Map<String, String>> getAllAvailableFonts() throws NoRecordFoundException;

	GenericResponse deleteFontByCategoryAndFamilyName(String category, String family);

}
