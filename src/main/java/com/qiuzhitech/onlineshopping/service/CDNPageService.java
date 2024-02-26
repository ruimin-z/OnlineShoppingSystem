package com.qiuzhitech.onlineshopping.service;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
/* Thymeleaf 除了可以把渲染结果写入Response，也可以写到本地文件，从而实现静态化 */
public class CDNPageService {
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private OnlineShoppingCommodityDao commodityDao;
    public void createHtml(long commodityId) {
        PrintWriter writer = null;
        try {
            OnlineShoppingCommodity onlineShoppingCommodity = commodityDao.getCommodityDetail(commodityId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("commodity", onlineShoppingCommodity);
            Context context = new Context();
            context.setVariables(resultMap);
            File file = new File("src/main/resources/templates/item_detail_" + commodityId + ".html");
            writer = new PrintWriter(file);
            templateEngine.process("item_detail", context, writer);
        } catch (Exception e) {
            log.error(e.toString());
            log.error("页面静态化出错：" + commodityId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}