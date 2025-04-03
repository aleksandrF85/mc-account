package com.example.mc_account.dto.filter;

import com.example.mc_account.validation.PageFilterValidation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@PageFilterValidation
public class PageFilter {

    private int pageSize;

    private int pageNumber;


}
