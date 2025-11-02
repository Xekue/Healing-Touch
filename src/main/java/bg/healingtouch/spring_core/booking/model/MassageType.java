package bg.healingtouch.spring_core.booking.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum MassageType {

    CLASSIC(60, BigDecimal.valueOf(50)),
    RELAX(60, BigDecimal.valueOf(55)),
    ANTICELLULITE_LEGS(45, BigDecimal.valueOf(45)),
    ANTICELLULITE_BACK(45, BigDecimal.valueOf(45)),
    SPORTS(75, BigDecimal.valueOf(70)),
    HEALING(60, BigDecimal.valueOf(65)),
    DEEP_TISSUE(90, BigDecimal.valueOf(90)),
    AROMA(60, BigDecimal.valueOf(60)),
    THAI(90, BigDecimal.valueOf(95));

    private final int durationMinutes;
    private final BigDecimal basePrice;

    MassageType(int durationMinutes, BigDecimal basePrice) {
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

}
