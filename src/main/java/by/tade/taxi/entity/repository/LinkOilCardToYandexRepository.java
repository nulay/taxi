package by.tade.taxi.entity.repository;

import by.tade.taxi.entity.LinkOilCardToYandexDriverEntity;

import java.util.List;
import java.util.Optional;

public interface LinkOilCardToYandexRepository extends IdentifiableRepository<LinkOilCardToYandexDriverEntity, Integer> {
    List<LinkOilCardToYandexDriverEntity> getAllByUserTaxiId(Integer id);

    LinkOilCardToYandexDriverEntity getByUserTaxiIdAndCardCode(Integer id, String cardCode);

    Optional<LinkOilCardToYandexDriverEntity> getByCardCodeAndUserTaxiId(String code, Integer userId);
}
