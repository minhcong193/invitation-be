package minhhang.invitationbe.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import minhhang.invitationbe.enums.ModerationStatus;

@Converter(autoApply = true)
public class ModerationStatusConverter implements AttributeConverter<ModerationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ModerationStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ModerationStatus convertToEntityAttribute(String dbData) {
        return dbData != null ? ModerationStatus.fromValue(dbData) : null;
    }
}

