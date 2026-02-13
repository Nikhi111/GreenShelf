package com.example.GreenSelf.service;


import com.example.GreenSelf.entity.CustomUserDetails;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService  implements UserDetailsService {
    @Autowired
    UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findUserByUsername((username)).orElseThrow(()->new RuntimeException("user not found"));
        System.out.println(user.toString());
        return new CustomUserDetails(user);
    }
}
