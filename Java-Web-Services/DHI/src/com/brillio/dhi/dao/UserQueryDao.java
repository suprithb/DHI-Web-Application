package com.brillio.dhi.dao;

import java.util.List;

import com.brillio.dhi.dao.entity.UserQuery;
import com.brillio.dhi.exception.NoRecordFoundException;

public interface UserQueryDao {

	boolean saveUserQuery(UserQuery userQuery);

	boolean updateUserQuery(UserQuery userQuery);

	List<UserQuery> getAllQueryByUserName(String userName) throws NoRecordFoundException;

	UserQuery getUserQueryByUserNameAndQuery(String userName, String userQuery) throws NoRecordFoundException;

}
