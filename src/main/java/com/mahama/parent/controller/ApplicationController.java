package com.mahama.parent.controller;

import com.alibaba.fastjson.JSONObject;
import com.mahama.parent.config.ApplicationConfig;
import com.mahama.parent.vo.Ret;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mahama
 * @date 2023年05月04日
 */
@RestController
@RequestMapping("/application")
@AllArgsConstructor
@Api(tags = {"程序信息"})
public class ApplicationController extends BaseController {
    private final ApplicationConfig config;

    @GetMapping("/info")
    public Ret<JSONObject> info() {
        JSONObject json = new JSONObject();
        json.put("version", config.getVersion());
        json.put("buildTime", config.getBuildTime());
        return success(json);
    }
}
