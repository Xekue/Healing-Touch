package bg.healingtouch.spring_core.booking.model;

import lombok.Getter;

@Getter
public enum MassageType {

    CLASSIC("Classic Massage"),
    RELAX("Relaxing Massage"),
    ANTICELLULITE_LEGS("Anticellulite Legs"),
    ANTICELLULITE_BACK("Anticellulite Back"),
    SPORTS("Sports Massage"),
    HEALING("Healing Massage"),
    DEEP_TISSUE("Deep Tissue Massage"),
    AROMA("Aromatherapy"),
    THAI("Thai Massage");

    private final String displayName;

    MassageType(String displayName) {
        this.displayName = displayName;
    }

}
