package com.ssafy.pocketfolio.api.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class CommonUtil {
    public static Pageable createPageable(int page, int size, String sort) {
        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(page - 1, size);
        }
        return PageRequest.of(page - 1, size, Sort.by(getSortOrder(sort)));
    }

    public static Pageable createPageable(int page, int size, String[] sorts) {
        if (sorts == null || sorts.length == 0) {
            return PageRequest.of(page - 1, size);
        }
        if (sorts.length == 2 && (sorts[1].equalsIgnoreCase("desc") || sorts[1].equalsIgnoreCase("asc"))) {
            return PageRequest.of(page - 1, size, Sort.by(getSortOrder(sorts[0] + "," + sorts[1])));
        }
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sorts).forEach(sort -> orders.add(getSortOrder(sort)));
        return PageRequest.of(page - 1, size, Sort.by(orders));
    }

    private static Sort.Order getSortOrder(String sort) {
        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        String sortOrder = sortParams.length > 1 ? sortParams[1] : "desc";
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new Sort.Order(direction, sortBy);
    }
}
