package com.lhb.calculation.calculation.controller;

import com.lhb.calculation.calculation.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/cache")
public class CacheController {


    @Resource
    private CacheService cacheService;

    @RequestMapping("/get")
    @ResponseBody
    public String get(){
        System.out.println("进入controller方法啦");
        String result = cacheService.get();
        return result;
    }


    @RequestMapping("/getDemo")
    @ResponseBody
    public String getDemo(String key){
        System.out.println("进入controller方法啦");
        String result1 = cacheService.demo1(key);
        String result2 = cacheService.demo2(key);
        return result1+"::::::"+result2;
    }


}
