package com.mahama.parent.controller;

import com.mahama.parent.controller.BaseController;
import com.mahama.parent.vo.Ret;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
@AllArgsConstructor
public class WelcomeController extends BaseController {
    @GetMapping({"", "/1"})
    public String welcome1() {
        return "success";
    }

    @GetMapping("/2")
    public Ret<?> welcome2() {
        return success();
    }
}
