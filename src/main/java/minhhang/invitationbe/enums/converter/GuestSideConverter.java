package minhhang.invitationbe.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import minhhang.invitationbe.enums.GuestSide;

@Converter(autoApply = true)
public class GuestSideConverter implements AttributeConverter<GuestSide, String> {

    @Override
    public String convertToDatabaseColumn(GuestSide attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public GuestSide convertToEntityAttribute(String dbData) {
        return dbData != null ? GuestSide.fromValue(dbData) : null;
    }
}

