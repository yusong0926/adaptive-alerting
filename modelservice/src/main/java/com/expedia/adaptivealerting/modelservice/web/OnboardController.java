package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.modelservice.service.OnboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tbahl on 12/27/18.
 */
@Slf4j
@RestController
public class OnboardController {

    @Autowired
    private OnboardService onboardService;

    @PostMapping(path = "/onboard")
    public boolean isOnboarded(@RequestBody String tags) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            onboardService.isOnboarded(tags);
            log.info("tags:{}", tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
