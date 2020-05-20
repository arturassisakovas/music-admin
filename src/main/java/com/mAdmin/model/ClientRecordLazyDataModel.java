package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Record;
import com.mAdmin.enumerator.RecordStatus;
import com.mAdmin.repository.RecordRepository;
import com.mAdmin.util.LazyDataModelUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientRecordLazyDataModel extends LazyDataModel<Record> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private RecordRepository recordRepository;

    
    private Client client;

    @Override
    public List<Record> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Record> recordPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            recordPage = recordRepository.findByClientAndStatus(client, RecordStatus.COMPLETED,
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            recordPage = recordRepository.findByClientAndStatusOrderByCreatedAtDesc(client, RecordStatus.COMPLETED,
                    PageRequest.of(first, pageSize));

        }

        if (recordPage.getContent().isEmpty() && first > 0) {
            recordPage = recordRepository.findByClientAndStatusOrderByCreatedAtDesc(client, RecordStatus.COMPLETED,
                    PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) recordPage.getTotalElements());

        return recordPage.getContent();
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

}
