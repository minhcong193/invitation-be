package minhhang.invitationbe.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModerationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    @JsonValue
    private final String value;

    @JsonCreator
    public static ModerationStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ModerationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown moderation status: " + value);
    }
}

