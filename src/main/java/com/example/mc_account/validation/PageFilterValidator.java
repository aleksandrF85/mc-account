package com.example.mc_account.validation;

import com.example.mc_account.dto.filter.PageFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;

public class PageFilterValidator implements ConstraintValidator<PageFilterValidation, PageFilter> {

    @Override
    public boolean isValid(PageFilter value, ConstraintValidatorContext context) {
        return !ObjectUtils.anyNull(value.getPageSize(), value.getPageNumber()) &&
                value.getPageSize() > 0 && value.getPageNumber() >= 0;
    }
}
