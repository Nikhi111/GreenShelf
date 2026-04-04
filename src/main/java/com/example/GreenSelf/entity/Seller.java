package com.example.GreenSelf.entity;
import jakarta.persistence.*;
import java.util.List;
@Entity
public class Seller{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @OneToOne
    User user;
    String nurseryName;
    private boolean bankVerified;
    @OneToMany
    List<Nursery> nurseries;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isApprovedSeller=false;
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<BankDetails> bankAccounts;

    public List<BankDetails> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankDetails> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public boolean isBankVerified() {
        return bankVerified;
    }

    public void setBankVerified(boolean bankVerified) {
        this.bankVerified = bankVerified;
    }

    public String getNurseryName() {
        return nurseryName;
    }

    public void setNurseryName(String nurseryName) {
        this.nurseryName = nurseryName;
    }

    public List<Nursery> getNurseries() {
        return nurseries;
    }

    public void setNurseries(List<Nursery> nurseries) {
        this.nurseries = nurseries;
    }

    public Boolean isApprovedSeller() {
        return isApprovedSeller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getApprovedSeller() {
        return isApprovedSeller;
    }

    public void setApprovedSeller(Boolean approvedSeller) {
        isApprovedSeller = approvedSeller;
    }

    public void setApprovedSeller(boolean approvedSeller) {
        isApprovedSeller = approvedSeller;
    }
}
