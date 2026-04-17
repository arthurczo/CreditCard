package arthurczo.dev.application.service;

import arthurczo.dev.application.dto.AdditionalCardRequestDto;
import arthurczo.dev.application.dto.CreditCardEntityDto;
import arthurczo.dev.application.dto.CreditLimitRequestDto;
import arthurczo.dev.application.dto.CreditRequestResultDto;
import arthurczo.dev.domain.entity.CreditCard;

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
