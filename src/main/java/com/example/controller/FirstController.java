package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class FirstController {

    @RequestMapping(value = "/intro", method = {RequestMethod.GET,RequestMethod.POST})
//    @ResponseBody
    public String test(Model model) {

        model.addAttribute("message", "Hello, World!");
        return "fileSelect"; // upload.jsp 파일을 응답으로 반환
    }

}
