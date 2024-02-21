package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {
    @Resource
    OnlineShoppingOrderMapper mapper;

    @Override
    public int deleteOrderById(long orderId) {
        return mapper.deleteByPrimaryKey(orderId);
    }

    @Override
    public int insertOrder(OnlineShoppingOrder record) {
        return mapper.insert(record);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public OnlineShoppingOrder queryOrderById(long orderId) {
        return mapper.selectByPrimaryKey(orderId);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNo(String orderNo) {
        return mapper.queryOrderByOrderNo(orderNo);
    }
}
