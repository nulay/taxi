package by.tade.taxi.beloil.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CardStatusDto {

    private Integer contrCode;

    private Integer cardCode;

    private Integer monthNorm;

    private Integer dayNorm;

    private String dayNormAmount;

    private List<OilGroupSetDto> oilGroupSet;

    private Boolean transitFl;

    private Boolean goodsFl;

    private Boolean transitGoodsFl;

    private String status;

    private String actionDate;

    private Integer division;

    private String driver;

    private String carNum;

    private Integer priority;

    private Integer dosePermitted;

    private String dosePermittedAmount;

    private Boolean kapschCard;

    private Boolean kapschContract;

    private String yandexId;
}
