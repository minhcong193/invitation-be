package minhhang.invitationbe.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import minhhang.invitationbe.enums.RsvpStatus;

@Converter(autoApply = true)
public class RsvpStatusConverter implements AttributeConverter<RsvpStatus, String> {

    @Override
    public String convertToDatabaseColumn(RsvpStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public RsvpStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? RsvpStatus.fromValue(dbData) : null;
    }
}
