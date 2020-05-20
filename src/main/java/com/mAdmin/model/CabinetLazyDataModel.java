package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Track;
import com.mAdmin.repository.CabinetRepository;
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
public class CabinetLazyDataModel extends LazyDataModel<Track> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    @Override
    public List<Track> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Track> trackPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            trackPage = cabinetRepository.findAll(
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            trackPage = cabinetRepository.findAll(PageRequest.of(first, pageSize));
        }
        if (trackPage.getContent().isEmpty() && first > 0) {
            trackPage = cabinetRepository.findAll(PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) trackPage.getTotalElements());

        return trackPage.getContent();
    }

}
