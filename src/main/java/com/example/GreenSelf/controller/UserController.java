package com.example.GreenSelf.controller;
import com.example.GreenSelf.Dto.CartDto;
import com.example.GreenSelf.Dto.UserRequestDto;
import com.example.GreenSelf.entity.Address;
import com.example.GreenSelf.entity.Role;
import com.example.GreenSelf.entity.SellerRequest;
import com.example.GreenSelf.entity.User;
import com.example.GreenSelf.service.UserService;
import com.example.GreenSelf.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/login")
    public String login(@RequestBody User request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        String token = jwtUtil.generateToken(request.getUsername());
        return token;
    }
    @PostMapping("user/register")
    void register( @RequestBody UserRequestDto userRequestDto){
        userService.createUser(userRequestDto);
    }
    @GetMapping("/home")
    String home(){
        return "this is home";
    }
    @GetMapping("/me")
    String home(@AuthenticationPrincipal UserDetails userDetails){
        return "Hiiii "+userDetails.getUsername();
    }
    @PostMapping("/user/addTocart")
     void  addTOCart(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CartDto cartDto){
        System.out.println("entered controller");
          userService.addToCart(userDetails.getUsername(),cartDto);
    }
    @PostMapping("user/createOrder")
    void createOrder(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Address address){
        userService.createOrder(userDetails.getUsername(),address);
    }
}
