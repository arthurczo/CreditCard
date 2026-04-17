package arthurczo.dev.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Entidade representando um cartão de crédito.
 * Esta classe mapeia a tabela de cartões de crédito no banco de dados.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard extends BaseEntity<UUID> {

    @Id// Indica que este campo é a chave primária
    @GeneratedValue(strategy = GenerationType.UUID) // Gera o ID como um UUID
    private UUID id;// Identificador único do cartão de crédito

    @Column(nullable = false)
    private UUID userId;// ID do usuário associado ao cartão

    @Column
    @Enumerated(EnumType.STRING)
    private CreditCardType type; // MAIN, ADDITIONAL ou REPLACEMENT

    @Column(nullable = false)
    private String name;// Nome do titular do cartão

    @Column (nullable = false, unique= true)
    private String number; // número do cartão

    @Column (nullable = false)
    private int cvv; // número de segurança do cartão

    @Column(nullable = false)
    private BigDecimal creditLimit;// Limite de crédito do cartão

    @Column(nullable = false)
    private BigDecimal availableLimit;// Limite disponível para uso no cartão

    @CreatedDate //Registrar data de criação
    private Date createdDate; // Data em que o cartão foi criado

    @LastModifiedDate
    private Date updatedDate;// Data da última atualização do cartão

    @Enumerated(EnumType.STRING) // Para armazenar o status como uma string
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "block_reason")
    private String blockReason;

    // Enum para representar os possíveis status do cartão
    public enum Status {
        PENDING,
        ACTIVE,
        BLOCKED,
        CANCELLED,
        LOST
    }

    public enum CreditCardType {
        MAIN,       // Cartão principal
        ADDITIONAL, // Cartão adicional
        REPLACEMENT // Cartão de substituição
    }

    public void setId(UUID id) {
        this.id = id;
    }

    // Retorna uma representação em string do objeto CreditCard
    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", creditLimit=" + creditLimit +
                ", availableLimit=" + availableLimit +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }

}
