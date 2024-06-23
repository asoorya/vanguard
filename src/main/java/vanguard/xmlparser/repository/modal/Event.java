package vanguard.xmlparser.repository.modal;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerParty;
    private String sellerParty;
    private Double premiumAmount;
    private String premiumCurrency;

    public Event() {
    }

    public Event(String buyerParty, String sellerParty, Double premiumAmount, String premiumCurrency) {
        this.buyerParty = buyerParty;
        this.sellerParty = sellerParty;
        this.premiumAmount = premiumAmount;
        this.premiumCurrency = premiumCurrency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyerParty() {
        return buyerParty;
    }

    public void setBuyerParty(String buyerParty) {
        this.buyerParty = buyerParty;
    }

    public String getSellerParty() {
        return sellerParty;
    }

    public void setSellerParty(String sellerParty) {
        this.sellerParty = sellerParty;
    }

    public Double getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(Double premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getPremiumCurrency() {
        return premiumCurrency;
    }

    public void setPremiumCurrency(String premiumCurrency) {
        this.premiumCurrency = premiumCurrency;
    }
}
