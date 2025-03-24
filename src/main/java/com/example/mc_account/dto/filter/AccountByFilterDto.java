package com.example.mc_account.dto.filter;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountByFilterDto {

    private AccountSearchDto accountSearchDto;

    @PositiveOrZero(message = "Поля пагинации должны быть заполнены. Нумерация страниц начинается с ноля.")
    private int page;

    @Positive(message = "Поле должно быть заполнено и быть больше ноля")
    private int size;
}
