package com.example.spring.controller;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAsync
public class HellowController {

    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hi");
        return "hellow";
    }

    @GetMapping("hello-mvc")
    public String hellowMvc(@RequestParam(value = "name", required = false) String name, Model model) {
        model.addAttribute("name", name);
        return "hellow-templates";
    }

    @GetMapping("hellow-string")
    @ResponseBody
    public String hellowString(@RequestParam("name") String name) {
        return "hello " + name;
    }

    @GetMapping("hellow-api")
    @ResponseBody
    public Hello hellowApi(@RequestParam("name") String name) {
        Hello hellow = new Hello();
        hellow.setName(name);
        return hellow;
    }

    static class Hello {
        private String name;

        public String getName() {
            return  name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
