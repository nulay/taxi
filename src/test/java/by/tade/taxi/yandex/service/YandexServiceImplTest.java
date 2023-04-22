package by.tade.taxi.yandex.service;

import by.tade.taxi.dto.DiscountGasDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class YandexServiceImplTest {
    YandexServiceImpl yandexService = new YandexServiceImpl(null);

    @Test
    public void test1() {
        BigDecimal oneHundred = new BigDecimal("100");
        BigDecimal result = yandexService.percentage(oneHundred, new BigDecimal(3));
        Assertions.assertTrue(result.equals(oneHundred.subtract(new BigDecimal(97))));

        List<DiscountGasDto> discountGas = List.of(new DiscountGasDto(new BigDecimal(100), new BigDecimal(1)),
                new DiscountGasDto(new BigDecimal(200), new BigDecimal(2)),
                new DiscountGasDto(new BigDecimal(300), new BigDecimal(3)));

        BigDecimal oneHundredFifty = new BigDecimal(150);

        BigDecimal percent2 = yandexService.getDiscountPercent(oneHundredFifty, discountGas);
        BigDecimal result2  = yandexService.calculateDiscount(oneHundredFifty, percent2);
        Assertions.assertTrue(result2.equals(new BigDecimal("148.5")));

        BigDecimal summ250 = new BigDecimal(250);
        BigDecimal percent3 = yandexService.getDiscountPercent(summ250, discountGas);
        BigDecimal result3  = yandexService.calculateDiscount(summ250, percent3);
        Assertions.assertTrue(result3.equals(new BigDecimal(245)));

        BigDecimal summ1 = new BigDecimal(1);
        BigDecimal percent4 = yandexService.getDiscountPercent(summ1, discountGas);
        BigDecimal result4  = yandexService.calculateDiscount(summ1, percent4);
        Assertions.assertTrue(result4.equals(new BigDecimal(1)));

        BigDecimal summ350 = new BigDecimal(350);
        BigDecimal percent5 = yandexService.getDiscountPercent(summ350, discountGas);
        BigDecimal result5  = yandexService.calculateDiscount(summ350, percent5);
        Assertions.assertTrue(result5.equals(new BigDecimal("339.5")));
    }
}