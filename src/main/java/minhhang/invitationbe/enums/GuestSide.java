package minhhang.invitationbe.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GuestSide {
    GROOM("groom", "Chú rể"),
    BRIDE("bride", "Cô dâu");

    @JsonValue
    private final String value;
    private final String label;

    @JsonCreator
    public static GuestSide fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        for (GuestSide side : values()) {
            if (side.value.equalsIgnoreCase(trimmed) || side.label.equalsIgnoreCase(trimmed)) {
                return side;
            }
        }
        if ("chu re".equalsIgnoreCase(trimmed)) {
            return GROOM;
        }
        if ("co dau".equalsIgnoreCase(trimmed)) {
            return BRIDE;
        }
        throw new IllegalArgumentException("Unknown guest side: " + value);
    }
}

