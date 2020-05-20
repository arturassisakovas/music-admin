package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.repository.ContractRepository;
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
public class ClientContractLazyDataModel extends LazyDataModel<Contract> {

    
    @Autowired
    private ContractRepository contractRepository;

    
    private Client client;

    @Override
    public List<Contract> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Contract> contractPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            contractPage = contractRepository.findAllByClientsAndTypeNot(
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)), client, ContractType.INVALID);

        } else {

            contractPage = contractRepository.findAllByClientsAndTypeNot(PageRequest.of(first, pageSize), client,
                    ContractType.INVALID);
        }
        if (contractPage.getContent().isEmpty() && first > 0) {
            contractPage = contractRepository.findAllByClientsAndTypeNot(PageRequest.of(first - 1, pageSize), client,
                    ContractType.INVALID);
        }

        setRowCount((int) contractPage.getTotalElements());

        return contractPage.getContent();
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

}
