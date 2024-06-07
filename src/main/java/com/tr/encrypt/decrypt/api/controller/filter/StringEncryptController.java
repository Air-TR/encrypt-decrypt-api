package com.tr.encrypt.decrypt.api.controller.filter;

import com.tr.encrypt.decrypt.api.kit.DateKit;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: TR
 */
@Api(tags = "String - Filter")
@RestController
public class StringEncryptController {

    @GetMapping("/encrypt/getTime/noParams")
    public String getTime() {
        return DateKit.getDateTime();
    }

    @GetMapping("/encrypt/String/{name}")
    public String encryptPathVariable(@PathVariable String name) {
        return "Hi, " + name;
    }

    @GetMapping("/encrypt/String/RequestParam")
    public String encryptRequestParam(@RequestParam String name) {
        return "Hi, " + name;
    }

}
