package com.example.GreenSelf.Dto;

import com.example.GreenSelf.entity.Address;
import com.example.GreenSelf.entity.Cart;
import com.example.GreenSelf.entity.Order;
import com.example.GreenSelf.entity.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
public class UserRequestDto {
    String username;
    String password;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
