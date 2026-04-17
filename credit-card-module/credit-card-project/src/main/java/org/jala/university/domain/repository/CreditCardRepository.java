package org.jala.university.domain.repository;

import org.jala.university.domain.entity.CreditCard;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditCardRepository extends Repository<CreditCard, UUID> {
    Optional<CreditCard> findById(UUID id);

    List<CreditCard> findAll();

    CreditCard save(CreditCard creditCard);

    void delete(CreditCard creditCard);

    void deleteById(UUID id);

    Optional<CreditCard> findByUserId(UUID userId);

    List<CreditCard> findPendingApplications();

    long countByUserId(UUID userId);

    CreditCard updateStatus(UUID cardId, CreditCard.Status newStatus);

    List<CreditCard> findByStatus(CreditCard.Status status);

    Optional<CreditCard> findByCardNumber(String number);

}

