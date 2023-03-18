package by.tade.taxi.mapper;

import by.tade.taxi.beloil.dto.BeloilUserCredentialDto;
import by.tade.taxi.dto.DiscountGasDto;
import by.tade.taxi.dto.RegistrationDto;
import by.tade.taxi.dto.UserDto;
import by.tade.taxi.dto.WriteOffGasTimeDto;
import by.tade.taxi.entity.UserTaxiEntity;
import by.tade.taxi.yandex.dto.YandexUserCredentialDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class UserMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endActivationDate", ignore = true)
    @Mapping(target = "beloilCredential", ignore = true)
    @Mapping(target = "yandexCredential", ignore = true)
    @Mapping(target = "writeOffGasTime", ignore = true)
    @Mapping(target = "discountGas", ignore = true)
    public abstract UserTaxiEntity toUserTaxi(RegistrationDto registrationDto);

    public abstract List<UserDto> toUserStorageDto(List<UserTaxiEntity> allUsers);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "settings.beloilUserCredential", expression = "java(getBeloilUserCredential(userTaxiEntity))")
    @Mapping(target = "settings.yandexUserCredential", expression = "java(getYandexUserCredentialDto(userTaxiEntity))")
    @Mapping(target = "settings.writeOffGasTime", expression = "java(getWriteOffGasTimeDto(userTaxiEntity))")
    @Mapping(target = "settings.discountGas", expression = "java(getDiscountGasDto(userTaxiEntity))")
    public abstract UserDto toUserDto(UserTaxiEntity userTaxiEntity);

    public BeloilUserCredentialDto getBeloilUserCredential(UserTaxiEntity registrationDto) {
        if(registrationDto == null || registrationDto.getBeloilCredential() == null){
            return null;
        }
        try {
            return objectMapper.readValue(registrationDto.getBeloilCredential(), BeloilUserCredentialDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public YandexUserCredentialDto getYandexUserCredentialDto(UserTaxiEntity registrationDto) {
        if(registrationDto == null || registrationDto.getYandexCredential() == null){
            return null;
        }
        try {
            return objectMapper.readValue(registrationDto.getYandexCredential(), YandexUserCredentialDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public WriteOffGasTimeDto getWriteOffGasTimeDto(UserTaxiEntity registrationDto) {
        if(registrationDto == null || registrationDto.getWriteOffGasTime() == null){
            return null;
        }
        try {
            return objectMapper.readValue(registrationDto.getWriteOffGasTime(), WriteOffGasTimeDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DiscountGasDto> getDiscountGasDto(UserTaxiEntity registrationDto) {
        if(registrationDto == null || registrationDto.getDiscountGas() == null){
            return null;
        }
        try {
            return objectMapper.readValue(registrationDto.getDiscountGas(), new TypeReference<List<DiscountGasDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
