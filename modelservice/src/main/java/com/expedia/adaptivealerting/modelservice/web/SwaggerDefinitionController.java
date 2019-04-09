package com.expedia.adaptivealerting.modelservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SwaggerDefinitionController {

  @RequestMapping("/swagger-ui")
  public String home() {
    return "redirect:swagger-ui.html";
  }
}