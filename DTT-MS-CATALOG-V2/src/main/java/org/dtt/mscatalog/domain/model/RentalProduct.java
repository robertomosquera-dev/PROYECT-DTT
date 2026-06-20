package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
public class RentalProduct extends BaseProductItem {

    private BigDecimal weeklyPrice;
    private BigDecimal monthlyPrice;
    private BigDecimal securityDeposit;

    public void updateWeeklyPrice(BigDecimal weeklyPrice) {
        validatePrice(weeklyPrice, "weekly price");
        this.weeklyPrice = weeklyPrice;
    }

    public void updateMonthlyPrice(BigDecimal monthlyPrice) {
        validatePrice(monthlyPrice, "monthly price");
        this.monthlyPrice = monthlyPrice;
    }

    public void updateSecurityDeposit(BigDecimal securityDeposit) {
        validatePrice(securityDeposit, "security deposit");
        this.securityDeposit = securityDeposit;
    }

}