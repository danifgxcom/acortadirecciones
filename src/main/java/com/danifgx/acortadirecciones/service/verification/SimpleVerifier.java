package com.danifgx.acortadirecciones.service.verification;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SimpleVerifier implements UrlVerifier {

    @Value("${url.blacklisted.keywords}")
    private String blacklistedKeywordsString;

    private List<String> blacklistedKeywords;

    @PostConstruct
    public void init() {
        this.blacklistedKeywords = Arrays.asList(blacklistedKeywordsString.split(","));
    }


    @Override
    public boolean verify(String url) {
        System.out.println(this.blacklistedKeywords);
        for (String keyword : this.blacklistedKeywords) {
            if (url.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

}



