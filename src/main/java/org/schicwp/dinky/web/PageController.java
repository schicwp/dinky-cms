package org.schicwp.dinky.web;

import org.schicwp.dinky.search.SearchContent;
import org.schicwp.dinky.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

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

    @GetMapping("content/{index}/{id}")
    public String page(@PathVariable("id") String id,@PathVariable("index") String index ,Model model, @RequestParam Map<String,Object> params){


        Page<SearchContent> contents = searchService.search(index, "id:" + id, 0, 1);

        if (contents.getContent().size() != 1)
            throw new RuntimeException();


        SearchContent item = contents.getContent().get(0);
        model.addAttribute("item", item);
        model.addAttribute("content",searchService);
        model.addAttribute("params",params);
        return String.format("content/%s/show",item.getType());
    }


}
