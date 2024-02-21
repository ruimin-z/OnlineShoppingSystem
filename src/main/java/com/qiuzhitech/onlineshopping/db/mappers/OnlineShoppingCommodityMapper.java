package com.qiuzhitech.onlineshopping.db.mappers;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;

import java.util.List;
import java.util.Map;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    List<OnlineShoppingCommodity> listCommodityByUserId(long userId);

    List<OnlineShoppingCommodity> listCommodities();

    int deductStock(long commodityId);

    void deductStockSP(Map<String, Object> params);

    List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword);

    void revertStock(Long commodityId);
}