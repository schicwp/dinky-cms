package org.schicwp.web;

import org.schicwp.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by will.schick on 1/15/19.
 */
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    SearchService searchService;

    @GetMapping
    public String index(Model model, @RequestParam Map<String,Object> params){
        model.addAttribute("content",searchService);
        model.addAttribute("params",params);
        return "index";
    }

    @GetMapping("page/{pageName}")
    public String page(@PathVariable("pageName") String pageName, Model model, @RequestParam Map<String,Object> params){
        model.addAttribute("content",searchService);
        model.addAttribute("params",params);
        return String.format("pages/%s",pageName);
    }


}
