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
import java.time.LocalDate;

@Entity
@Table(name = "user_taxi")
@Getter
@Setter
@FieldNameConstants(asEnum = true)
public class UserTaxiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "end_activation_date")
    private LocalDate endActivationDate;

    @Column(name = "beloil_credential", columnDefinition = "TEXT")
    private String beloilCredential;

    @Column(name = "yandex_credential", columnDefinition = "TEXT")
    private String yandexCredential;

    @Column(name = "write_off_gas_time", columnDefinition = "TEXT")
    private String writeOffGasTime;

    @Column(name = "discount_gas", columnDefinition = "TEXT")
    private String discountGas;
}
