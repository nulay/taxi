package by.tade.taxi.entity.repository;

import by.tade.taxi.entity.UserTaxiEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends IdentifiableRepository<UserTaxiEntity, Long> {

    Optional<UserTaxiEntity> getByLogin(String login);

    Optional<UserTaxiEntity> getByLoginAndPassword(String login, String password);
}
