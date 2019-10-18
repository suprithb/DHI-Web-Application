package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.FileUploadDataSource;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface FileUploadDataSourceDao {

	boolean saveFileDataSource(FileUploadDataSource fileUploadDataSource);

	List<FileUploadDataSource> getAllFileDataSource(String userName) throws NoRecordFoundException;

	FileUploadDataSource getDefaultFileDataSourceByUserName(String userName) throws NoRecordFoundException;

	boolean updateDefaultFlagByUserName(String userName, String dataModelFilename);

	FileUploadDataSource getFileDataSource(String userName, String dataModelFileName) throws NoRecordFoundException;

	boolean updateFileDataSource(FileUploadDataSource fileUploadDataSource);

}
