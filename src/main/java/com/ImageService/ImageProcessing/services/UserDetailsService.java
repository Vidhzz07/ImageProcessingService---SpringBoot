package com.ImageService.ImageProcessing.services;

import com.ImageService.ImageProcessing.Repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return  userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }
}
