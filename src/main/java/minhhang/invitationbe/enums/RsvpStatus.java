package minhhang.invitationbe.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RsvpStatus {
    PENDING("pending"),
    ATTENDING("attending"),
    DECLINED("declined");

    @JsonValue
    private final String value;

    @JsonCreator
    public static RsvpStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RsvpStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RSVP status: " + value);
    }
}

