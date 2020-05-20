package com.mAdmin.util;

import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;


public class LazyDataModelUtil {

    
    public static Direction getDirection(SortOrder sortOrder) {
        if (sortOrder.ordinal() == 0) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
