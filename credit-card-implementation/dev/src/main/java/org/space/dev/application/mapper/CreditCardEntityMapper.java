package arthurczo.dev.application.mapper;

import arthurczo.dev.application.dto.CreditCardEntityDto; //Importa a classe DTO
import arthurczo.dev.domain.entity.CreditCard;// Importa a entidade CreditCard
import arthurczo.dev.application.mapper.Mapper; //Importa a interface Mapper
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreditCardEntityMapper implements Mapper<CreditCard, CreditCardEntityDto> {

    @Override
    public CreditCardEntityDto mapTo(CreditCard creditCard) {
        if (creditCard == null){
            return null;
        }

        // Método que converte uma instância de CreditCard em CreditCardEntityDto
        return CreditCardEntityDto.builder()
                .id(creditCard.getId())// Define o ID do CreditCard no DTO
                .name(creditCard.getName()) // Define o nome do CreditCard no DTO
                .requestedLimit(creditCard.getCreditLimit())// Assumindo que requestedLimit no DTO corresponde a creditLimit na entidade
                .monthlyIncome(BigDecimal.ZERO) //Defina um valor padrão ou adicione lógica para calcular
                .creditScore(0) //Defina um valor valor padrão ou adidicione lógica para calcular
                .build();// Constrói a instância do DTO
    }

    @Override
    public CreditCard mapFrom(CreditCardEntityDto creditCardEntityDto) {
        if(creditCardEntityDto == null){
            return null;
        }
        // Cria e retorna um novo CreditCard usando o padrão Builder
        return CreditCard.builder()
                .id(creditCardEntityDto.getId())// Define o ID do DTO na entidade
                .name(creditCardEntityDto.getName()) // Define o nome do DTO na entidade
                .creditLimit(creditCardEntityDto.getRequestedLimit())// Assumindo que o limite disponível inicia igual ao limite solicitado
                .number(creditCardEntityDto.getNumber())
                .userId(UUID.randomUUID())// Você precisará definir a lógica para obter o userId apropriado
                .build();// Constrói a instância da entidade
    }
}
