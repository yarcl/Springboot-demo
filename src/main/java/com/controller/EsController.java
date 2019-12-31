package com.controller;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xiaozhi on 2019/12/31.
 */
@RestController("/es")
public class EsController {

    public String queryProductInfo(@RequestParam("uuid") String uuid) {


        return "";
    }

}
