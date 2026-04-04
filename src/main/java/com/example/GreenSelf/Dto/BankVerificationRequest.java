package com.example.GreenSelf.Dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankVerificationRequest {

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;

    @NotBlank(message = "Account type is required")
    private String accountType;     // SAVINGS or CURRENT

    private boolean setPrimary;
}