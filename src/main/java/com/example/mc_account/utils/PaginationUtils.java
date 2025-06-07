package com.example.mc_account.utils;

import com.example.mc_account.dto.response.PagedResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class PaginationUtils {

    public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setContent(page.getContent());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        return response;
    }
}
