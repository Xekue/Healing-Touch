package bg.healingtouch.spring_core.booking.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum MassageType {

    CLASSIC("Classic Massage",
            "A gentle full-body massage that relieves tension, improves circulation, and promotes relaxation.",
            60, BigDecimal.valueOf(50)
    ),

    RELAX("Relaxing Massage",
            "A calming massage designed to reduce stress, release muscle tension, and restore inner balance.",
            60, BigDecimal.valueOf(55)
    ),

    ANTICELLULITE_LEGS("Anticellulite - Legs",
            "Focused on the legs, this treatment improves blood flow, reduces cellulite, and tones the skin.",
            45, BigDecimal.valueOf(45)
    ),

    ANTICELLULITE_BACK("Anticellulite + Back",
            "Targets cellulite and muscle tightness on the back, leaving your skin smoother and firmer.",
            45, BigDecimal.valueOf(45)
    ),

    SPORTS("Sports Massage",
            "An intensive massage that relieves muscle soreness, enhances recovery, and improves flexibility for athletes.",
            75, BigDecimal.valueOf(70)
    ),

    HEALING("Healing Massage",
            "A restorative therapy combining gentle pressure and energy work to relieve pain and promote healing.",
            60, BigDecimal.valueOf(65)
    ),

    DEEP_TISSUE("Deep Tissue",
            "A powerful massage that focuses on deep layers of muscle tissue to ease chronic pain and stiffness.",
            90, BigDecimal.valueOf(90)
    ),

    AROMA("Aroma Therapy",
            "A luxurious massage with essential oils that calm the senses, balance the body, and rejuvenate the spirit.",
            60, BigDecimal.valueOf(60)
    ),

    THAI("Thai",
            "A traditional Thai massage combining acupressure and yoga-like stretches to increase flexibility and energy flow.",
            90, BigDecimal.valueOf(95)
    );

    private final String displayName;
    private final String description;
    private final int durationMinutes;
    private final BigDecimal basePrice;

    MassageType(String displayName, String description, int durationMinutes, BigDecimal basePrice) {
        this.displayName = displayName;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
    }
}
