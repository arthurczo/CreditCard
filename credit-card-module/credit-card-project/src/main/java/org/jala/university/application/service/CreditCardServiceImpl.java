package org.jala.university.application.service;

import org.jala.university.application.dto.CreditCardEntityDto;
import org.jala.university.application.dto.CreditLimitRequestDto;
import org.jala.university.application.dto.CreditRequestResultDto;
import org.jala.university.application.dto.AdditionalCardRequestDto;
import org.jala.university.application.mapper.CreditCardEntityMapper;
import org.jala.university.domain.entity.CreditCard;
import org.jala.university.domain.repository.CreditCardRepository;
import org.jala.university.domain.service.CreditAnalysisService;
import org.jala.university.infrastructure.persistance.CreditCardGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CreditCardServiceImpl implements CreditCardService {
    private final CreditCardRepository creditCardRepository;// Repositório para acessar os dados do cartão de crédito
    private final CreditCardEntityMapper creditCardEntityMapper;
    private final CreditAnalysisService creditAnalysisService;

    // Construtor que recebe as dependências necessárias
    public CreditCardServiceImpl(CreditCardRepository creditCardRepository, CreditCardEntityMapper creditCardEntityMapper, CreditAnalysisService creditAnalysisService) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardEntityMapper = creditCardEntityMapper;
        this.creditAnalysisService = creditAnalysisService;
    }

    @Override
    @Transactional(readOnly = true)
    public CreditCardEntityDto getCreditCardByUserId(UUID userId) {
        CreditCard creditCard = creditCardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit card not found"));;

        return creditCardEntityMapper.mapTo(creditCard);
    }

    @Override
    @Transactional
    public CreditRequestResultDto requestCard(CreditLimitRequestDto requestDto) {
        // Validação de entrada
        if (requestDto.getMonthlyIncome() == null || requestDto.getMonthlyIncome().compareTo(BigDecimal.ZERO) <= 0) {
            return CreditRequestResultDto.builder()
                    .approved(false)
                    .rejectionReason("Renda mensal inválida")
                    .build();
        }

        // Análise de crédito
        CreditAnalysisService.CreditAnalysisResult analysisResult = creditAnalysisService.analyzeCreditRequest(
                requestDto.getMonthlyIncome(),
                requestDto.getTimeAtCurrentJob(),
                requestDto.getRequestedLimit(),
                BigDecimal.ZERO // Novo cartão, sem limite atual
        );

        // Se aprovado, criar novo cartão

        if (analysisResult.isApproved()) {
            CreditCard newCard = CreditCard.builder()
                    .id(UUID.randomUUID())
                    .userId(requestDto.getUserId())
                    .number(CreditCardGenerator.generateCardNumber())
                    .cvv(CreditCardGenerator.generateCVV())
                    .creditLimit(analysisResult.getApprovedLimit())
                    .availableLimit(analysisResult.getApprovedLimit())
                    .status(CreditCard.Status.PENDING)
                    .build();

            creditCardRepository.save(newCard);

            return CreditRequestResultDto.builder()
                    .approved(true)
                    .approvedLimit(analysisResult.getApprovedLimit())
                    .message(analysisResult.getMessage())
                    .build();
        } else {
            return CreditRequestResultDto.builder()
                    .approved(false)
                    .rejectionReason(analysisResult.getRejectionReason())
                    .build();
        }
    }

    @Override
    @Transactional
    public CreditRequestResultDto requestCreditIncrease(CreditLimitRequestDto requestDto) {
        // Validação de entrada
        if (requestDto.getMonthlyIncome() == null || requestDto.getMonthlyIncome().compareTo(BigDecimal.ZERO) <= 0) {
            return CreditRequestResultDto.builder()
                    .approved(false)
                    .rejectionReason("Renda mensal inválida")
                    .build();
        }

        CreditCard creditCard = creditCardRepository.findById(requestDto.getCardId())
                .orElseThrow(() -> new RuntimeException("Credit card not found"));

        // Análise de crédito
        CreditAnalysisService.CreditAnalysisResult analysisResult = creditAnalysisService.analyzeCreditRequest(
                requestDto.getMonthlyIncome(),
                requestDto.getTimeAtCurrentJob(),
                requestDto.getRequestedLimit(),
                creditCard.getCreditLimit()
        );

        // Se aprovado, criar novo cartão

        BigDecimal newAvailableLimit = creditCard.getAvailableLimit().add(analysisResult.getApprovedLimit().subtract(creditCard.getCreditLimit()));

        if (analysisResult.isApproved()) {
            CreditCard newCard = CreditCard.builder()
                    .id(UUID.randomUUID())
                    .userId(creditCard.getUserId())
                    .number(creditCard.getNumber())
                    .cvv(creditCard.getCvv())
                    .creditLimit(analysisResult.getApprovedLimit())
                    .availableLimit(newAvailableLimit)
                    .status(creditCard.getStatus())
                    .build();

            creditCardRepository.save(newCard);

            return CreditRequestResultDto.builder()
                    .approved(true)
                    .approvedLimit(analysisResult.getApprovedLimit())
                    .message(analysisResult.getMessage())
                    .build();
        } else {
            return CreditRequestResultDto.builder()
                    .approved(false)
                    .rejectionReason(analysisResult.getRejectionReason())
                    .build();
        }
    }

    // Aprova um cartão de crédito existente
    @Override
    @Transactional
    public void approveCard(CreditCardEntityDto creditCardEntityDto) {
        // Busca o cartão de crédito pelo ID
        CreditCard creditCard = creditCardRepository.findById(creditCardEntityDto.getId())
                .orElseThrow(() -> new RuntimeException("Credit card not found"));// Lança exceção se não encontrado
        creditCard.setStatus(CreditCard.Status.ACTIVE);// Define o status como ACTIVE
        creditCardRepository.save(creditCard);// Salva a entidade no repositório
    }

    @Override
    @Transactional
    public void rejectCard(CreditCardEntityDto creditCardEntityDto) {
        CreditCard creditCard = creditCardRepository.findById(creditCardEntityDto.getId())
                .orElseThrow(() -> new RuntimeException("Credit card not found"));
        creditCard.setStatus(CreditCard.Status.BLOCKED);
        creditCardRepository.save(creditCard);
    }

    public void unblockCard(CreditCardEntityDto creditCardEntityDto) {
        // Buscar o cartão pelo ID
        CreditCard creditCard = creditCardRepository.findById(creditCardEntityDto.getId())
                .orElseThrow(() -> new RuntimeException("Credit card not found"));

        //Verificar o status atual do cartão
        if(creditCard.getStatus() == CreditCard.Status.BLOCKED){
            creditCard.setStatus(CreditCard.Status.ACTIVE);//Alterar o estado para ativado caso esteja bloqueado
            creditCardRepository.save(creditCard);//salvar mudança no banco
            System.out.println("Your card is unlocked");
        }else{
            System.out.println("Unable to unlock your card, please contact one of our technicians");
        }


    }
    @Override
    public void blockCard(String number) {
        CreditCard card = creditCardRepository.findByCardNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("Cartão não encontrado"));

        if (card.getStatus() == CreditCard.Status.BLOCKED) {
            throw new IllegalStateException("Cartão já está bloqueado");
        } else {
            System.out.println("Cartão bloqueado com sucesso! \n");
        }

        card.setStatus(CreditCard.Status.BLOCKED);
        creditCardRepository.save(card);
    }


    @Override
    @Transactional
    public CreditCard requestAdditionalCard(AdditionalCardRequestDto requestDto) {
        CreditCard mainCard = creditCardRepository.findById(requestDto.getMainCardId())
                .orElseThrow(() -> new IllegalArgumentException("Primary card not found."));

        if (mainCard.getStatus() != CreditCard.Status.ACTIVE) {
            throw new IllegalArgumentException("The primary card is not active. It is not possible to request an additional card.");
        }
        CreditCard additionalCard = CreditCard.builder()
                .id(UUID.randomUUID())
                .userId(mainCard.getUserId())
                .name(mainCard.getName())  //copiar o nome do titular
                .number(CreditCardGenerator.generateCardNumber())
                .cvv(CreditCardGenerator.generateCVV())
                .creditLimit(mainCard.getCreditLimit())
                .availableLimit(mainCard.getAvailableLimit()) //usar o limite disponível do principal
                .status(CreditCard.Status.ACTIVE) // definir como ACTIVE
                .type(CreditCard.CreditCardType.ADDITIONAL) // definir o tipo como ADICIONAL
                .build();
        return creditCardRepository.save(additionalCard);
    }
}

