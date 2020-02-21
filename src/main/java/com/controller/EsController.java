package com.controller;

import com.entity.qo.EsQueryQo;
import com.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiaozhi on 2019/12/31.
 */
@RestController
@RequestMapping("/es")
public class EsController {

    @Autowired
    private EsService esService;

    @GetMapping(value = "queryOneInfo")
    public String queryProductInfo(@RequestParam("name") String name) {
        EsQueryQo esQueryQo = new EsQueryQo();
        esQueryQo.setQueryCondition(name);
        esQueryQo.setQueryIndex("ecommerce");
        esQueryQo.setQueryType("product");
        String result = esService.queryByName(esQueryQo);
        return result;
    }

    //测试
}
