package com.qiuzhitech.onlineshopping.db.dao.impl;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository //和数据库相关的annotation
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {
    @Resource
    OnlineShoppingCommodityMapper mapper;
    @Override
    public int deleteCommodityById(long commodityId) {
        return mapper.deleteByPrimaryKey(commodityId);
    }
    @Override
    public int insertCommodity(OnlineShoppingCommodity record) {
        return mapper.insert(record);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodityByUserId(long userId) {
        return mapper.listCommodityByUserId(userId);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return mapper.listCommodities();
    }

    @Override
    public OnlineShoppingCommodity getCommodityDetail(long commodityId) {
        return mapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public int deductStock(long commodityId){
        return mapper.deductStock(commodityId);  // 返回受影响的行数（0或1）1为成功，0为失败
    }

    @Override
    public int deductStockSP(long commodityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("commodityId", commodityId);
        params.put("res", 0);
        mapper.deductStockSP(params);
        Object res = params.getOrDefault("res", 0);
        return (int) res;
    }

    @Override
    public List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword){
        String keywordPattern = "%" + keyword + "%";
        return mapper.searchCommodityByKeyword(keywordPattern);
    }

    @Override
    public void revertStock(Long commodityId) {
        mapper.revertStock(commodityId);
    }



}