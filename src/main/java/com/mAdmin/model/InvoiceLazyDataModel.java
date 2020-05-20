package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;
import com.mAdmin.repository.InvoiceRepository;
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
public class InvoiceLazyDataModel extends LazyDataModel<Invoice> {

    
    @Autowired
    private InvoiceRepository invoiceRepository;

    
    private Client client;

    @Override
    public List<Invoice> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Invoice> invoicePage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            invoicePage = invoiceRepository.findByClientAndValidTrue(client,
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            invoicePage = invoiceRepository.findByClientAndValidTrueOrderByCreatedAtDesc(client,
                    PageRequest.of(first, pageSize));
        }
        if (invoicePage.getContent().isEmpty() && first > 0) {
            invoicePage = invoiceRepository.findByClientAndValidTrueOrderByCreatedAtDesc(client,
                    PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) invoicePage.getTotalElements());

        return invoicePage.getContent();
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

}
