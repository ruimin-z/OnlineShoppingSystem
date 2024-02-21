package com.qiuzhitech.onlineshopping.db.dao;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import java.util.List;

public interface OnlineShoppingCommodityDao {

    List<OnlineShoppingCommodity> listCommodityByUserId(long userId); // 卖家/买家可以看到指定商家上架的商品列表
    List<OnlineShoppingCommodity> listCommodities(); // 卖家/买家可以看到所有的商品列表

    int deleteCommodityById(long commodityId);

    int insertCommodity(OnlineShoppingCommodity record);

    int updateCommodity(OnlineShoppingCommodity record);
    OnlineShoppingCommodity getCommodityDetail(long commodityId); // 卖家/买家可以查看商品详情

    int deductStock(long commodityId); // 尝试对指定id的commodity stock - 1

    int deductStockSP(long commodityId);

    List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword);


    void revertStock(Long commodityId);
}