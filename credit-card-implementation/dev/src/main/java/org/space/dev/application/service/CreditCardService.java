package org.jala.university.application.service;

import org.jala.university.application.dto.AdditionalCardRequestDto;
import org.jala.university.application.dto.CreditCardEntityDto;
import org.jala.university.application.dto.CreditLimitRequestDto;
import org.jala.university.application.dto.CreditRequestResultDto;
import org.jala.university.domain.entity.CreditCard;

import java.util.UUID;

public interface CreditCardService {
    CreditCardEntityDto getCreditCardByUserId(UUID userId);

    CreditRequestResultDto requestCard(CreditLimitRequestDto creditLimitRequestDto);
    CreditRequestResultDto requestCreditIncrease(CreditLimitRequestDto creditLimitRequestDto);

    void approveCard(CreditCardEntityDto creditCardEntityDto);
    void rejectCard(CreditCardEntityDto creditCardEntityDto);
    void unblockCard(CreditCardEntityDto creditCardEntityDto);
    void blockCard(String number);
    CreditCard requestAdditionalCard(AdditionalCardRequestDto requestDto);
}
