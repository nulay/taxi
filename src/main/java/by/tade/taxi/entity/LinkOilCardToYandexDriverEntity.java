package by.tade.taxi.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "link_oil_card_to_yandex_driver")
@Getter
@Setter
@FieldNameConstants(asEnum = true)
public class LinkOilCardToYandexDriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_taxi_id", nullable = false)
    private Integer userTaxiId;

    @Column(name = "card_code", nullable = false)
    private String cardCode;

    @Column(name = "yandex_driver_profile", nullable = false)
    private String yandexDriverProfile;
}
