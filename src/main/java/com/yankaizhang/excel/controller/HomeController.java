package com.yankaizhang.excel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author dzzhyk
 */
@Controller
public class HomeController {

    @RequestMapping({"/index", "/"})
    public String index(){
        return "index";
    }

}
