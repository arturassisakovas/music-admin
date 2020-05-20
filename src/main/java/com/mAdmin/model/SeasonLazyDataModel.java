package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Season;
import com.mAdmin.repository.SeasonRepository;
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
public class SeasonLazyDataModel extends LazyDataModel<Season> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private SeasonRepository seasonRepository;

    @Override
    public List<Season> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Season> seasonPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            seasonPage = seasonRepository.findAllBy(PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            seasonPage = seasonRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(first, pageSize));
        }
        if (seasonPage.getContent().isEmpty() && first > 0) {
            seasonPage = seasonRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) seasonPage.getTotalElements());

        return seasonPage.getContent();
    }

}
