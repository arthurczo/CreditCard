package org.jala.university.infrastructure.persistance;

import org.jala.university.domain.entity.CreditCard;
import org.jala.university.domain.repository.CreditCardRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class CreditCardRepositoryMock implements CreditCardRepository {
    private final Map<UUID, CreditCard> creditCards = new HashMap<>();

    public CreditCardRepositoryMock() {
        // Initialize with sample test data
        CreditCard card1 = CreditCard.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"))
                .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("Memphis Depay")
                .number("1234 5678 9123 4567")
                .cvv(189)
                .creditLimit(new BigDecimal("5000.00"))
                .availableLimit(new BigDecimal("4500.00"))
                .status(CreditCard.Status.ACTIVE)
                .build();

        CreditCard card2 = CreditCard.builder()
                .id(UUID.fromString("f1e2d3c4-b5a6-7890-1234-567890abcdef"))
                .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .name("PEDRO SILVA")
                .number("9876 5432 1098 7654")
                .cvv(233)
                .creditLimit(new BigDecimal("10000.00"))
                .availableLimit(new BigDecimal("8000.00"))
                .status(CreditCard.Status.PENDING)
                .build();

        creditCards.put(card1.getId(), card1);
        creditCards.put(card2.getId(), card2);
    }

    @Override
    public Optional<CreditCard> findById(UUID id) {
        return Optional.ofNullable(creditCards.get(id));
    }

    @Override
    public List<CreditCard> findAll() {
        return new ArrayList<>(creditCards.values());
    }

    @Override
    public CreditCard save(CreditCard creditCard) {
        if (creditCard.getId() == null) {
            creditCard.setId(UUID.randomUUID());
        }
        creditCards.put(creditCard.getId(), creditCard);
        return creditCard;
    }

    @Override
    public void delete(CreditCard creditCard) {
        creditCards.remove(creditCard.getId());
    }

    @Override
    public void deleteById(UUID id) {
        creditCards.remove(id);
    }

    @Override
    public Optional<CreditCard> findByUserId(UUID userId) {
        return creditCards.values().stream()
                .filter(card -> card.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<CreditCard> findPendingApplications() {
        return creditCards.values().stream()
                .filter(card -> card.getStatus() == CreditCard.Status.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserId(UUID userId) {
        return creditCards.values().stream()
                .filter(card -> card.getUserId().equals(userId))
                .count();
    }
    @Override
    public CreditCard updateStatus(UUID cardId, CreditCard.Status newStatus) {
        CreditCard card = creditCards.get(cardId);
        if (card == null) {
            throw new RuntimeException("Cartão não encontrado");
        }
        card.setStatus(newStatus);
        return card;
    }

    @Override
    public List<CreditCard> findByStatus(CreditCard.Status status) {
        return creditCards.values().stream()
                .filter(card -> card.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CreditCard> findByCardNumber(String number) {
        return creditCards.values().stream()
                .filter(card -> card.getNumber().equals(number))
                .findFirst();
    }


    // Helper method for testing
    public void clear() {
        creditCards.clear();
    }
}