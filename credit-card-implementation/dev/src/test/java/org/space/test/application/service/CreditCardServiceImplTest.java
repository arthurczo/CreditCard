package arthurczo.dev.application.service;

import arthurczo.dev.application.dto.*;
import arthurczo.dev.application.mapper.CreditCardEntityMapper;
import arthurczo.dev.domain.entity.CreditCard;
import arthurczo.dev.domain.repository.CreditCardRepository;
import arthurczo.dev.domain.service.CreditAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import javax.persistence.EntityNotFoundException;
import arthurczo.dev.domain.exceptions.BusinessRuleException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para {@link CreditCardServiceImpl}.
 * Esta classe testa os principais cenários do serviço de cartão de crédito, incluindo:
 * Solicitação de novo cartão
 * Aprovação de cartão pendente
 * Rejeição de cartão pendente
 * Bloqueio e desbloqueio de cartão
 * Utiliza o framework Mockito para simular as dependências e JUnit 5 para execução dos testes.
 */
@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardEntityMapper creditCardMapper;

    @Mock
    private CreditAnalysisService creditAnalysisService;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testCardId = UUID.randomUUID();
    private final String testCardNumber = "4111111111111111";

    // Objetivo: Testar a solicitação de um novo cartão quando a análise de crédito aprova.

    /**
     * Testa o cenário de sucesso na solicitação de um novo cartão de crédito.
     * Verifica se o serviço retorna um resultado aprovado quando a análise de crédito é favorável.
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O resultado indica aprovação (isApproved = true)</li>
     *     <li>O limite aprovado corresponde ao esperado</li>
     *     <li>O cartão é salvo no repositório</li>
     * </ul>
     */
    @Test
    void requestCard_shouldReturnApprovedResult_whenCreditAnalysisApproves() {
        // Arrange
        CreditLimitRequestDto requestDto = CreditLimitRequestDto.builder()
                .userId(testUserId)
                .monthlyIncome(new BigDecimal("5000"))
                .timeAtCurrentJob(12)
                .requestedLimit(new BigDecimal("3000"))
                .build();

        when(creditAnalysisService.analyzeCreditRequest(
                any(BigDecimal.class),
                anyInt(),
                any(BigDecimal.class),
                any(BigDecimal.class))
        ).thenReturn(new CreditAnalysisService.CreditAnalysisResult(
                true,
                new BigDecimal("3000"),
                "Approved",
                null
        ));

        // Act
        CreditRequestResultDto result = creditCardService.requestCard(requestDto);

        // Assert
        assertTrue(result.isApproved());
        assertEquals(new BigDecimal("3000"), result.getApprovedLimit());
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    // Objetivo: Testar a aprovação de um cartão pendente.

    /**
     * Testa a aprovação de um cartão com status PENDING.
     * Verifica se o status do cartão é alterado para ACTIVE após aprovação.
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O status do cartão é atualizado para ACTIVE</li>
     *     <li>O cartão é persistido no repositório</li>
     * </ul>
     */
    @Test
    void approveCard_shouldChangeStatusToActive_whenCardExists() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        CreditCardEntityDto dto = CreditCardEntityDto.builder()
                .id(cardId)
                .build();

        CreditCard card = new CreditCard();
        card.setStatus(CreditCard.Status.PENDING);
        card.setId(cardId);

        when(creditCardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // Act
        creditCardService.approveCard(dto);

        // Assert
        assertEquals(CreditCard.Status.ACTIVE, card.getStatus());
        verify(creditCardRepository).save(card);
    }

    // Objetivo: Testar a rejeição de um cartão pendente.

    /**
     * Testa a rejeição de um cartão com status PENDING.
     * Verifica se o status do cartão é alterado para BLOCKED após rejeição.
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O status do cartão é atualizado para BLOCKED</li>
     *     <li>O cartão é persistido no repositório</li>
     * </ul>
     */
    @Test
    void rejectCard_shouldChangeStatusToBlocked_whenCardExists() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        CreditCardEntityDto dto = CreditCardEntityDto.builder()
                .id(cardId)
                .build();

        CreditCard card = new CreditCard();
        card.setStatus(CreditCard.Status.PENDING);
        card.setId(cardId);

        when(creditCardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // Act
        creditCardService.rejectCard(dto);

        // Assert
        assertEquals(CreditCard.Status.BLOCKED, card.getStatus());
        verify(creditCardRepository).save(card);
        // Verifique se não há outros mocks desnecessários
    }

    // Objetivo: Testar o desbloqueio de um cartão bloqueado.

    /**
     * Testa o desbloqueio de um cartão com status BLOCKED.
     * Verifica se o status do cartão é alterado para ACTIVE após desbloqueio.
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O status do cartão é atualizado para ACTIVE</li>
     *     <li>O status do cartão é atualizado para ACTIVE</li>
     * </ul>
     */
    @Test
    void unblockCard_shouldChangeStatusToActive_whenCardIsBlocked() {
        // Arrange
        UUID testCardId = UUID.randomUUID();
        CreditCardEntityDto dto = CreditCardEntityDto.builder()
                .id(testCardId)
                .build();

        CreditCard card = new CreditCard();
        card.setId(testCardId);
        card.setStatus(CreditCard.Status.BLOCKED);

        when(creditCardRepository.findById(testCardId)).thenReturn(Optional.of(card));

        // Act
        creditCardService.unblockCard(dto);

        // Assert
        assertEquals(CreditCard.Status.ACTIVE, card.getStatus());
        verify(creditCardRepository).save(card);
    }

    //Objetivo: Testar o bloqueio de um cartão ativo.

    /**
     * Testa o bloqueio de um cartão com status ACTIVE.
     * Verifica se o status do cartão é alterado para BLOCKED após bloqueio.
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O status do cartão é atualizado para BLOCKED</li>
     *     <li>O cartão é persistido no repositório</li>
     * </ul>
     */
    @Test
    void blockCard_shouldChangeStatusToBlocked_whenCardExists() {
        // Arrange
        CreditCard card = new CreditCard();
        card.setStatus(CreditCard.Status.ACTIVE);

        when(creditCardRepository.findByCardNumber(testCardNumber)).thenReturn(Optional.of(card));

        // Act
        creditCardService.blockCard(testCardNumber);

        // Assert
        assertEquals(CreditCard.Status.BLOCKED, card.getStatus());
        verify(creditCardRepository).save(card);
    }

    /**
     * Testa se um novo cartão é criado com status PENDING após solicitação.
     *
     * @assert Verifica que:
     * <ul>
     *   <li>O cartão criado tem status PENDING</li>
     *   <li>O cartão é persistido no repositório</li>
     * </ul>
     */
    @Test
    void requestCard_shouldCreateCardWithPendingStatus() {
        // Arrange
        CreditLimitRequestDto requestDto = CreditLimitRequestDto.builder()
                .userId(testUserId)
                .monthlyIncome(new BigDecimal("5000"))
                .timeAtCurrentJob(12)
                .requestedLimit(new BigDecimal("3000"))
                .build();

        when(creditAnalysisService.analyzeCreditRequest(any(), anyInt(), any(), any()))
                .thenReturn(new CreditAnalysisService.CreditAnalysisResult(
                        true, new BigDecimal("3000"), "Approved", null));

        // Act
        CreditRequestResultDto result = creditCardService.requestCard(requestDto);

        // Assert
        ArgumentCaptor<CreditCard> cardCaptor = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(cardCaptor.capture());

        CreditCard savedCard = cardCaptor.getValue();
        assertEquals(CreditCard.Status.PENDING, savedCard.getStatus());
        assertTrue(result.isApproved());
    }

    /**
     * Testa a solicitação de aumento de limite de crédito
     *
     * @assert Verifica que:
     * <ul>
     *     <li>O limite total do cartão é atualizado de acordo com o limite aprovado</li>
     *     <li>O limite disponível do cartão é atualizado</li>
     *     <li>O cartão é persistido no repositório</li>
     * </ul>
     */

    @Test
    void requestCreditIncrease_shouldUpdateLimit_whenCreditAnalysisApproves() {
        // Arrange
        CreditCard card = new CreditCard();
        card.setId(testCardId);
        card.setUserId(testUserId);
        card.setCreditLimit(new BigDecimal("5000"));
        card.setAvailableLimit(new BigDecimal("3000"));

        CreditLimitRequestDto requestDto = CreditLimitRequestDto.builder()
                .userId(testUserId)
                .cardId(testCardId)
                .requestedLimit(new BigDecimal("12000"))
                .monthlyIncome(new BigDecimal("8000"))
                .timeAtCurrentJob(12)
                .build();

        when(creditAnalysisService.analyzeCreditRequest(
                any(BigDecimal.class),
                anyInt(),
                any(BigDecimal.class),
                any(BigDecimal.class))
        ).thenReturn(new CreditAnalysisService.CreditAnalysisResult(
                true, new BigDecimal("7000"), "Approved", null));

        when(creditCardRepository.findById(testCardId))
                .thenReturn(Optional.of(card));

        // Act
        CreditRequestResultDto result = creditCardService.requestCreditIncrease(requestDto);

        // Assert
        ArgumentCaptor<CreditCard> cardCaptor = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(cardCaptor.capture());

        CreditCard savedCard = cardCaptor.getValue();

        // limite novo disponivel = (limite antigo disponivel) + (limite novo total - limite antigo total)
        BigDecimal expectedNewAvailableLimit = card.getAvailableLimit().add(savedCard.getCreditLimit()).subtract(card.getCreditLimit());

        assertEquals(result.getApprovedLimit(), savedCard.getCreditLimit());
        assertEquals(expectedNewAvailableLimit, savedCard.getAvailableLimit());
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    void requestAdditionalCard_shouldCreateActiveAdditionalCard_whenMainCardIsActiveAndExists() {
        // Dados de teste:
        UUID mainCardHolderUserId = UUID.randomUUID(); //ID do usuário que possui o cartão principal
        UUID mainCardId = UUID.randomUUID(); //ID do cartão principal
        BigDecimal mainCardLimit = new BigDecimal("10000.00");
        BigDecimal mainCardAvailableLimit = new BigDecimal("5000.00");

        // Criação do DTO (Objeto de Transferência de Dados) para a requisição:
        // Este objeto simula os dados que seriam enviados para solicitar o cartão adicional.
        AdditionalCardRequestDto requestDto = AdditionalCardRequestDto.builder()
                .mainCardId(mainCardId)// Informa qual é o cartão principal
                .build();

        // Criação de um objeto 'CreditCard' simulando o cartão principal:
        // O cartão que esperamos que o repositório "encontre".
        CreditCard mainCard = CreditCard.builder()
                .id(mainCardId)
                .userId(mainCardHolderUserId)
                .name("Titular Principal Exemplo")
                .number("1111222233334444")
                .cvv(123)
                .creditLimit(mainCardLimit)
                .availableLimit(mainCardAvailableLimit)
                .status(CreditCard.Status.ACTIVE) // Principal está ATIVO
                .type(CreditCard.CreditCardType.MAIN)
                .build();

        //    Aqui, estamos usando o Mockito (através do 'when') para dizer como
        //    o 'creditCardRepository' deve se comportar quando seus métodos forem chamados.

        when(creditCardRepository.findById(mainCardId)).thenReturn(Optional.of(mainCard));

        when(creditCardRepository.save(any(CreditCard.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ---- Act (Agir) ----
        //agora camamos o método do serviço que queremos testar, passando o DTO preparado.
        CreditCard createdAdditionalCard = creditCardService.requestAdditionalCard(requestDto);

        // ---- Assert (Verificar) ----
        // Nesta seção, verificamos se o resultado da ação (Act) é o esperado.
        // Verifica se o cartão adicional criado não é nulo.
        assertNotNull(createdAdditionalCard, "O cartão adicional não deveria ser nulo.");
        assertNotNull(createdAdditionalCard.getId(), "O ID do cartão adicional não deveria ser nulo.");
        assertNotEquals(mainCardId, createdAdditionalCard.getId(), "O ID do adicional deve ser diferente do principal.");

        // Verifica se as propriedades do cartão adicional foram definidas corretamente
        assertEquals(mainCard.getUserId(), createdAdditionalCard.getUserId(), "UserID deve ser o mesmo do cartão principal.");
        assertEquals(mainCard.getName(), createdAdditionalCard.getName(), "Nome deve ser o mesmo do titular do cartão principal.");
        assertEquals(CreditCard.CreditCardType.ADDITIONAL, createdAdditionalCard.getType(), "Tipo do cartão deve ser ADDITIONAL.");
        assertEquals(CreditCard.Status.ACTIVE, createdAdditionalCard.getStatus(), "Status do cartão adicional deve ser ACTIVE.");
        assertEquals(mainCard.getCreditLimit(), createdAdditionalCard.getCreditLimit(), "Limite de crédito deve ser o mesmo do principal.");
        assertEquals(mainCard.getAvailableLimit(), createdAdditionalCard.getAvailableLimit(), "Limite disponível deve ser o mesmo do principal no momento da criação.");

        // Verifica se um novo número e CVV foram gerados para o cartão adicional.
        assertNotNull(createdAdditionalCard.getNumber(), "Número do cartão adicional não deveria ser nulo.");
        assertNotEquals(mainCard.getNumber(), createdAdditionalCard.getNumber(), "Número do adicional deve ser diferente do principal.");
        assertTrue(createdAdditionalCard.getCvv() > 0, "CVV do adicional deve ser gerado.");

        // Verificar se o método save foi chamado uma vez com qualquer instância de CreditCard
        verify(creditCardRepository, times(1)).save(any(CreditCard.class));

        // Capturar o argumento para verificar em detalhe se necessário (opcional aqui, pois já temos o objeto retornado)
        ArgumentCaptor<CreditCard> cardCaptor = ArgumentCaptor.forClass(CreditCard.class);
        verify(creditCardRepository).save(cardCaptor.capture());
        CreditCard savedCardByCaptor = cardCaptor.getValue();

        assertEquals(createdAdditionalCard.getId(), savedCardByCaptor.getId()); // Confirma que o capturado é o mesmo retornado
    }

    @Test
    void requestAdditionalCard_shouldThrowIllegalArgumentException_whenMainCardNotFound() {
        // Criação de um ID para um cartão principal que não existe
        UUID nonExistentMainCardId = UUID.randomUUID();
        // Criação do DTO de requisição com este ID inexistente
        AdditionalCardRequestDto requestDto = AdditionalCardRequestDto.builder()
                .mainCardId(nonExistentMainCardId)
                .build();

        // Configuração do Mock para findById:
        when(creditCardRepository.findById(nonExistentMainCardId)).thenReturn(Optional.empty());

        // Aqui, estamos verificando se uma exceção específica é lançada E qual a mensagem dela
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creditCardService.requestAdditionalCard(requestDto);
        }, "Deveria lançar IllegalArgumentException se o cartão principal não for encontrado.");

        assertEquals("Primary card not found.", exception.getMessage(), "Mensagem da exceção incorreta.");

        // Garantir que o save NÃO foi chamado
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }
}