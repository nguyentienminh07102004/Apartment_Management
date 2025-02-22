package com.ptitB22CN539.LaptopShop.Utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PageableUtils {
    public Pageable getPageable(Integer page, Integer limit) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (limit == null || limit < 1) {
            limit = 10;
        }
        return PageRequest.of(page - 1, limit);
    }
}
