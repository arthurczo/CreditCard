package arthurczo.dev.infrastructure.persistance;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.repository.CreditCardRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@Transactional
@Slf4j
public class CreditCardRepositoryImpl implements CreditCardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CreditCard> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(CreditCard.class, id));
    }

    @Override
    public List<CreditCard> findAll() {
        return entityManager.createQuery("SELECT c FROM CreditCard c", CreditCard.class)
                .getResultList();
    }

    @Override
    public CreditCard save(CreditCard creditCard) {
        if (creditCard.getId() == null) {
            entityManager.persist(creditCard);
            return creditCard;
        }
        return entityManager.merge(creditCard);
    }

    @Override
    public void delete(CreditCard creditCard) {
        CreditCard managedCard = entityManager.contains(creditCard)
                ? creditCard
                : entityManager.merge(creditCard);
        entityManager.remove(managedCard);
    }

    @Override
    public void deleteById(UUID id) {
        CreditCard creditCard = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado com ID: " + id));
        entityManager.remove(creditCard);
    }

    @Override
    public Optional<CreditCard> findByUserId(UUID userId) {
        TypedQuery<CreditCard> query = entityManager.createQuery(
                "SELECT c FROM CreditCard c WHERE c.userId = :userId", CreditCard.class);
        query.setParameter("userId", userId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) { // por excessões
            log.warn("Nenhum cartão encontrado para o usuário com ID: {}", userId);
            throw new EntityNotFoundException("Cartão não encontrado para o usuário com ID: " + userId);
        }
    }

    @Override
    public List<CreditCard> findPendingApplications() {
        return entityManager.createQuery(
                        "SELECT c FROM CreditCard c WHERE c.status = 'PENDING'", CreditCard.class)
                .getResultList();
    }

    @Override
    public long countByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM CreditCard c WHERE c.userId = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public CreditCard updateStatus(UUID cardId, CreditCard.Status newStatus) {
        CreditCard card = entityManager.find(CreditCard.class, cardId);
        if (card == null) {
            throw new EntityNotFoundException("Cartão não encontrado com ID: " + cardId);
        }
        card.setStatus(newStatus);
        return entityManager.merge(card);
    }

    @Override
    public List<CreditCard> findByStatus(CreditCard.Status status) {
        return entityManager.createQuery(
                        "SELECT c FROM CreditCard c WHERE c.status = :status", CreditCard.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Optional<CreditCard> findByCardNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Número do cartão não pode ser nulo ou vazio");
        }

        try {
            return Optional.ofNullable(
                    entityManager.createQuery(
                                    "SELECT c FROM CreditCard c WHERE c.number = :number", CreditCard.class)
                            .setParameter("number", number.trim())
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            log.warn("Nenhum cartão encontrado com o número: {}", number);
            throw new EntityNotFoundException("Cartão não encontrado com número: " + number);
        } catch (NonUniqueResultException e) {
            log.error("Número de cartão duplicado encontrado: {}", number);
            throw new DataIntegrityViolationException("Número de cartão duplicado: " + number);
        }
    }
}
