package arthurczo.dev;

import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.repository.CreditCardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    @Bean
    public CommandLineRunner unblockDemo(CreditCardRepository repository) {
        return args -> {
//            System.out.println("\n=== TESTE DE DESBLOQUEIO ===");
//
//            UUID cardId = UUID.fromString("22222222-2222-2222-2222-222222222222");
//            CreditCard card = repository.findById(cardId).orElseThrow();
//
//            if(card.getStatus() == CreditCard.Status.BLOCKED) {
//                card.setStatus(CreditCard.Status.ACTIVE);
//                repository.save(card);
//                System.out.println("✅ Cartão desbloqueado com sucesso");
//            } else {
//                System.out.println("❌ Cartão não está bloqueado");
//            }

            System.out.println("\n=== TESTE DE BLOQUEIO ===");

            UUID cardId = UUID.fromString("33333333-3333-3333-3333-333333333333");
            CreditCard card = repository.findById(cardId).orElseThrow();

            if(card.getStatus() != CreditCard.Status.BLOCKED) {
                card.setStatus(CreditCard.Status.BLOCKED);
                card.setBlockReason("Perda do cartão");
                repository.save(card);
                System.out.println("✅ Cartão bloqueado com sucesso");
            } else {
                System.out.println("❌ Cartão já está bloqueado");
            }
//
//            System.out.println("\n=== TESTE DE APROVAÇÃO ===");
//
//            UUID cardId = UUID.fromString("44444444-4444-4444-4444-444444444444");
//            CreditCard card = repository.findById(cardId).orElseThrow();
//
//            if(card.getStatus() == CreditCard.Status.PENDING) {
//                card.setStatus(CreditCard.Status.ACTIVE);
//                card.setCreditLimit(new BigDecimal("5000.00")); // Define limite padrão
//                repository.save(card);
//                System.out.println("✅ Cartão aprovado com sucesso");
//            } else {
//                System.out.println("❌ Só é possível aprovar cartões pendentes");
//            }
        };
    }

}