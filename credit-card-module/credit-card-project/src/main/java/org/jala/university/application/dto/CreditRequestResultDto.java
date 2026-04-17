package org.jala.university.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequestResultDto {
    private boolean approved;
    private BigDecimal approvedLimit;
    private String message;
    private String rejectionReason;
}
