package com.brillio.dhi.dao;

import com.brillio.dhi.dao.entity.UserAuth;
import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface UserAuthDao {

	public boolean saveUserAuth(UserAuth userAuth);

	public UserAuth getUserAuthByUserName(String userName) throws NoRecordFoundException;

	public boolean deleteUserAuthByUserName(String userName);

	UserAuth getUserNameByPhoneNumber(String phoneNumber)
			throws NoRecordFoundException, MissingMandatoryParameterException;

}
