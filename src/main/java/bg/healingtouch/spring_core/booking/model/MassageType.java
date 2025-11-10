package bg.healingtouch.spring_core.booking.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum MassageType {

    CLASSIC(
            "A gentle full-body massage that relieves tension, improves circulation, and promotes relaxation.",
            60, BigDecimal.valueOf(50)
    ),

    RELAX(
            "A calming massage designed to reduce stress, release muscle tension, and restore inner balance.",
            60, BigDecimal.valueOf(55)
    ),

    ANTICELLULITE_LEGS(
            "Focused on the legs, this treatment improves blood flow, reduces cellulite, and tones the skin.",
            45, BigDecimal.valueOf(45)
    ),

    ANTICELLULITE_BACK(
            "Targets cellulite and muscle tightness on the back, leaving your skin smoother and firmer.",
            45, BigDecimal.valueOf(45)
    ),

    SPORTS(
            "An intensive massage that relieves muscle soreness, enhances recovery, and improves flexibility for athletes.",
            75, BigDecimal.valueOf(70)
    ),

    HEALING(
            "A restorative therapy combining gentle pressure and energy work to relieve pain and promote healing.",
            60, BigDecimal.valueOf(65)
    ),

    DEEP_TISSUE(
            "A powerful massage that focuses on deep layers of muscle tissue to ease chronic pain and stiffness.",
            90, BigDecimal.valueOf(90)
    ),

    AROMA(
            "A luxurious massage with essential oils that calm the senses, balance the body, and rejuvenate the spirit.",
            60, BigDecimal.valueOf(60)
    ),

    THAI(
            "A traditional Thai massage combining acupressure and yoga-like stretches to increase flexibility and energy flow.",
            90, BigDecimal.valueOf(95)
    );

    private final String description;
    private final int durationMinutes;
    private final BigDecimal basePrice;

    MassageType(String description, int durationMinutes, BigDecimal basePrice) {
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
    }
}
