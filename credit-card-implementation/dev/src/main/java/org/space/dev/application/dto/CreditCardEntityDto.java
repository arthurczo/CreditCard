package arthurczo.dev.application.dto;

import lombok.Builder;//Permite usar a anotação @Builder, que ajuda a criar objetos de forma mais fácil e organizada.

import java.math.BigDecimal; //Trabalhar com valores decimais de forma precisa
import lombok.Value; //Value gera automaticamente getters e torna variaveis imutaveis.


import java.util.UUID;

@Builder// Permite a construção de objetos de forma fluente usando o padrão Builder
@Value// Torna a classe imutável e gera getters automaticamente
public class CreditCardEntityDto {
    UUID id;
    String name;
    String applicantEmail;
    String CPFNumber;
    String number;
    String cvv;
    BigDecimal monthlyIncome;
    BigDecimal requestedLimit;
    String address;
    int creditScore;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public String getCPFNumber() {
        return CPFNumber;
    }

    public String getNumber() {
        return number;
    }

    public String getCvv() {
        return cvv;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getRequestedLimit() {
        return requestedLimit;
    }

    public String getAddress() {
        return address;
    }

    public int getCreditScore() {
        return creditScore;
    }
}
