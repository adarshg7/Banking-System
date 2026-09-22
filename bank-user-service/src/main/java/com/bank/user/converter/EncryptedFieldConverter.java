package com.bank.user.converter;

import com.bank.common.util.EncryptionUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Converter
public class EncryptedFieldConverter implements AttributeConverter<String,String> {

    @Value("${encryption.secret-key}")
    private String secretKey;

    @Override
    public String convertToDatabaseColumn(String attribute){
        if(attribute == null){
            return null;
        }
        return EncryptionUtils.encrypt(attribute,secretKey);
    }

    @Override
    public String convertToEntityAttribute(String dbData){
        if(dbData == null) {
            return null;
        }
        return EncryptionUtils.decrypt(dbData,secretKey);

    }

}
