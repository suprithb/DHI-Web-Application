package com.brillio.dhi.dao;

import com.brillio.dhi.dao.entity.UserToken;
import com.brillio.dhi.exception.MissingMandatoryParameterException;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface UserTokenDao {

	public boolean saveUserToken(UserToken userToken);

	public UserToken getUserTokenByName(String name) throws NoRecordFoundException;

	public boolean saveOrUpdateUserToken(UserToken userToken);

	UserToken getUserTokenByChatRoomId(String chatRoomId) throws NoRecordFoundException, MissingMandatoryParameterException;
}
