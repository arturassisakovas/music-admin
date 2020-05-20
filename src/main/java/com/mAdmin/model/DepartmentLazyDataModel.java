package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Pool;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.util.LazyDataModelUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;


@Component
public class DepartmentLazyDataModel extends LazyDataModel<Pool> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private PoolRepository poolRepository;

    @Override
    public List<Pool> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Pool> poolPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            poolPage = poolRepository.findAll(
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            poolPage = poolRepository.findAll(PageRequest.of(first, pageSize));
        }
        if (poolPage.getContent().isEmpty() && first > 0) {
            poolPage = poolRepository.findAll(PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) poolPage.getTotalElements());

        return poolPage.getContent();
    }

}
