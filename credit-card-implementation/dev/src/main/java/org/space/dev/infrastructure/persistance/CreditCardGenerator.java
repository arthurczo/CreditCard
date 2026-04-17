package arthurczo.dev.infrastructure.persistance; // Alterado o package para algo mais apropriado

import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.entity.CreditCard.Status;

import java.math.BigDecimal;
import java.util.*;

public class CreditCardGenerator {

    private static final Random RANDOM = new Random();

    private static final String[] CARD_PREFIXES = {"4", "5", "3", "6", "2"}; // Sem comentários inúteis
    private static final String[] BLOCK_REASONS = {
            "Perda do cartão",
            "Roubo",
            "Uso suspeito",
            "Solicitação do cliente",
            "Fraude detectada"
    };

    private CreditCardGenerator() {
    }

    public static Map<UUID, CreditCard> generateRandomCreditCards(int size) {
        Map<UUID, CreditCard> creditCards = new HashMap<>();
        while (creditCards.size() < size) {
            CreditCard creditCard = buildRandomCreditCard();
            creditCards.putIfAbsent(creditCard.getId(), creditCard); // Evita colisão de UUID
        }
        return creditCards;
    }

    public static CreditCard buildRandomCreditCard() {
        Status status = getRandomStatus();

        BigDecimal creditLimit = BigDecimal.valueOf(1000 + (RANDOM.nextDouble() * 9000));
        BigDecimal availableLimit = creditLimit.multiply(
                BigDecimal.valueOf(0.1 + (RANDOM.nextDouble() * 0.9))
        );

        String blockReason = status == Status.BLOCKED ?
                BLOCK_REASONS[RANDOM.nextInt(BLOCK_REASONS.length)] : null;

        return CreditCard.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .number(generateCardNumber())
                .cvv(generateCVV())
                .creditLimit(creditLimit)
                .availableLimit(availableLimit)
                .status(status)
                .blockReason(blockReason)
                .build();
    }

    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();

        String prefix = CARD_PREFIXES[RANDOM.nextInt(CARD_PREFIXES.length)];
        cardNumber.append(prefix);

        int remainingDigits = 16 - prefix.length();
        for (int i = 0; i < remainingDigits; i++) {
            cardNumber.append(RANDOM.nextInt(10));
        }

        return cardNumber.toString();
    }

    public static int generateCVV() {
        return 100 + RANDOM.nextInt(900);
    }

    private static Status getRandomStatus() {
        Status[] statuses = Status.values();
        return statuses[RANDOM.nextInt(statuses.length)];
    }
}
