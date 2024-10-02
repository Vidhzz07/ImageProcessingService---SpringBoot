package com.ImageService.ImageProcessing.utils;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public String getUsernameFromAuth()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication!=null && authentication.isAuthenticated())
        {
            return authentication.getName();
        }
        else return null;
    }
}
