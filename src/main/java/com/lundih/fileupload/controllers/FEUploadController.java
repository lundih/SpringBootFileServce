package com.lundih.fileupload.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FEUploadController {

    @GetMapping("/upload-file")
    ModelAndView fileUpload() {
        return new ModelAndView("index.html");
    }
}
