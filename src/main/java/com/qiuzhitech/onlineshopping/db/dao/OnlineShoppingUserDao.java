package com.qiuzhitech.onlineshopping.db.dao;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingUser;

public interface OnlineShoppingUserDao {
    int deleteUserById(Long userId);
    int insertUser(OnlineShoppingUser user);
    OnlineShoppingUser queryUserById(Long userId);int updateUser(OnlineShoppingUser user);
}