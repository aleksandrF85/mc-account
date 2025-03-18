package com.example.mc_account.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageFilter {

    private int page;

    private int size;

    private String sort;
}
