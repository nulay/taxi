package by.tade.taxi.beloil.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
public class CardStatusDto {

    private Integer contrCode;

    private Integer cardCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal monthNorm;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dayNorm;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dayNormAmount;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dosePermitted;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dosePermittedAmount;

    private Boolean kapschCard;

    private Boolean kapschContract;

    private String yandexId;
}
