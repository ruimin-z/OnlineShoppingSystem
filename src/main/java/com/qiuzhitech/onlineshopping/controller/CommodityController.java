package com.qiuzhitech.onlineshopping.controller;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.service.EsService;
import com.qiuzhitech.onlineshopping.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class CommodityController {

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    SearchService searchService;

    @Resource
    EsService esService;

    @GetMapping("/addItem")
    public String addItem(){
        return "add_commodity";
    }

    @GetMapping({"/commodities", "/"})
    public String getCommodities(Map<String, Object> res){
        List<OnlineShoppingCommodity> commodityList = commodityDao.listCommodities();
        res.put("itemList", commodityList);
        return "list_items";
    }


    @PostMapping("/commodities")
    public String addItemAction(@RequestParam("commodityId") long commodityId,
                                @RequestParam("commodityName") String commodityName,
                                @RequestParam("commodityDesc") String commodityDesc,
                                @RequestParam("price") int price,
                                @RequestParam("availableStock") int availableStock,
                                @RequestParam("creatorUserId") long creatorUserId,
                                Map<String, Object> resultMap) throws IOException {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .lockStock(0)
                .totalStock(availableStock)
                .availableStock(availableStock)
                .commodityDesc(commodityDesc)
                .commodityName(commodityName)
                .creatorUserId(creatorUserId)
                .price(price)
                .commodityId(commodityId)
                .build();
        resultMap.put("Item", commodity);
        commodityDao.insertCommodity(commodity);
        esService.insertCommodity(commodity);
        return "add_commodity_success";
    }

    @GetMapping("/item/{commodityId}")
    public String itemDetail(@PathVariable("commodityId") String commodityId,
                             Map<String, Object> resultMap){
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(Long.parseLong(commodityId));
        resultMap.put("commodity", commodityDetail);
        return "item_detail";
    }

    @RequestMapping("/staticItem/{commodityId}")
    public String staticItemPage(@PathVariable("commodityId") long commodityId) {
        // 静态页面
        return "item_detail_" + commodityId;
    }

    @GetMapping("/listItems/{sellerId}")
    public String getCommoditiesByUserId(@PathVariable("sellerId") String sellerId,
                                         Map<String, Object> resultMap){
        // 被限流器控制，制定API范围。IN代表request进来，对不同的sellerId进行保护，通过参数动态设置。店铺忙或不忙。
        // batchCount – the amount of calls within the invocation (e.g. batchCount=2 means request for 2 tokens)
        try(Entry entry = SphU.entry("listItemsRule", EntryType.IN, 1, sellerId)){
            List<OnlineShoppingCommodity> commodityList = commodityDao.listCommodityByUserId(Long.parseLong(sellerId));
            resultMap.put("itemList", commodityList);
            return "list_items";
        } catch (BlockException e) { // 如果超过规则设置上限，进入等待页面
            log.error("ListItems API get throttled with sellerId {}", sellerId);
            return "wait";
        }
    }
    
    @RequestMapping("/searchAction")
    public String search(@RequestParam("keyWord") String keyWord,
                         Map<String, Object> resultMap) throws IOException {
        // 搜索，通过search service依赖注入
        List<OnlineShoppingCommodity> onlineShoppingCommodities = searchService.searchCommodityByES(keyWord);
        resultMap.put("itemList", onlineShoppingCommodities);
        return "search_items";
    }

    @PostConstruct  // 在构造函数调用后立即使用
    // 限流规则
    public void rateLimit(){
        FlowRule flowRule = new FlowRule();
        flowRule.setResource("listItemsRule"); //设置/listItems的规则
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS); //设置限流衡量标准为QPS
        flowRule.setCount(1); //每秒最大访问值
        // 制定规则组
        List<FlowRule> rules = new ArrayList<>();
        rules.add(flowRule);
        FlowRuleManager.loadRules(rules); // 放在manager静态类里
    }

}
