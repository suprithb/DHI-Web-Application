package com.brillio.dhi.dao;

import com.brillio.dhi.dao.entity.UserDefinedColumns;

public interface UserDefinedColumnDao {

	boolean saveUserDefinedColumn(UserDefinedColumns userDefinedColumn);

	boolean updateUserDefinedColumn(UserDefinedColumns userDefinedColumn);

}
