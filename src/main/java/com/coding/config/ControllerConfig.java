package com.coding.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * @author https://github.com/zziaguan/
 */
@ControllerAdvice
public class ControllerConfig {

    private final ResourceUrlProvider resourceUrlProvider;

    @Autowired
    public ControllerConfig(ResourceUrlProvider resourceUrlProvider) {
        this.resourceUrlProvider = resourceUrlProvider;
    }


    @ModelAttribute("urls")
    public ResourceUrlProvider urls() {
        return resourceUrlProvider;
    }


}
