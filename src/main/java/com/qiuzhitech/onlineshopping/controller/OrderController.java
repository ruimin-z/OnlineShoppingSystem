package com.qiuzhitech.onlineshopping.controller;


import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    OrderService orderService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(@PathVariable("commodityId") String commodityId,
                               @PathVariable("userId") String userId,
                               Map<String, Object> resultMap) throws Exception {
//        OnlineShoppingOrder order = orderService.processOrderStoredProcedure(Long.valueOf(commodityId), Long.valueOf(userId));
//        OnlineShoppingOrder order = orderService.processOrderRedis(Long.valueOf(commodityId), Long.valueOf(userId));
//        OnlineShoppingOrder order = orderService.processOrderRedis(Long.valueOf(commodityId), Long.valueOf(userId));
//        OnlineShoppingOrder order = orderService.processOrderDistributedLock(Long.valueOf(commodityId), Long.valueOf(userId));
        OnlineShoppingOrder order = orderService.processOrderRocketMQ(Long.valueOf(commodityId), Long.valueOf(userId));
        String resultInfo = "";
        if(order!=null){
            resultInfo="Order successful! Order Num: "+ order.getOrderNo();
            resultMap.put("orderNo", order.getOrderNo());
        } else{
            resultInfo="Commodity is out of stock";
        }
        resultMap.put("resultInfo", resultInfo);
        return "order_result";
    }

    @RequestMapping("/commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable("orderNum") String orderNo,
                             Map<String, Object> resultMap){
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNo);
        OnlineShoppingCommodity commodity = commodityDao.getCommodityDetail(order.getCommodityId());
        resultMap.put("order", order);
        resultMap.put("commodity", commodity);
        return "order_check";
    }


    @RequestMapping("/commodity/payOrder/{orderNum}")
    public String payOrder(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> resultMap){
        orderService.payOrder(orderNum);
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNum);
        OnlineShoppingCommodity commodity = commodityDao.getCommodityDetail(order.getCommodityId());
        resultMap.put("order", order);
        resultMap.put("commodity", commodity);
        return "order_check";
    }

}
