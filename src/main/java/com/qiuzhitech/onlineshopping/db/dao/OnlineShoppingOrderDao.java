package com.qiuzhitech.onlineshopping.db.dao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;


public interface OnlineShoppingOrderDao {
    int deleteOrderById(long orderId); // 删除订单

    int insertOrder(OnlineShoppingOrder record); // 创建订单

    int updateOrder(OnlineShoppingOrder record);  // 买家可以对订单进行付款

    OnlineShoppingOrder queryOrderById(long orderId); // 查看自己的订单

    OnlineShoppingOrder queryOrderByOrderNo(String orderNo); // 买家可以按订单号进行搜索

}