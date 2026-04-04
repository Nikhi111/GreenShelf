package com.example.GreenSelf.Dto;



import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankVerificationResponse {
    private boolean verified;
    private String message;
    private String nameReturnedByBank;
    private String bankName;
    private String bankBranch;
    private String verificationStatus;
    private Long bankDetailId;
    private String failureReason;
}