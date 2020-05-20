package com.mAdmin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mAdmin.entity.Client;
import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.repository.ClientRepository;
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
public class ClientLazyDataModel extends LazyDataModel<Client> {

    
    private Set<ClientStatus> clientStatuses;

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public List<Client> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Client> clientPage;

        String globalFilter = (String) filters.get("globalFilter");

        if (globalFilter != null && !globalFilter.isEmpty()) {

            List<Client> clients;

            String query = globalFilter.toLowerCase();

            clients = clientRepository.findByStatusInWithNameContainingOrSurnameContaining(clientStatuses, query);

            List<Client> clientsListPage = new ArrayList<>();

            for (int i = first; i < clients.size(); i++) {
                clientsListPage.add(clients.get(i));
            }

            setRowCount(clients.size());

            return clientsListPage;
        }

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            clientPage = clientRepository.findByStatusIn(clientStatuses,
                    PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            clientPage = clientRepository.findByStatusInOrderByCreatedAtDesc(clientStatuses,
                    PageRequest.of(first, pageSize));

        }

        setRowCount((int) clientPage.getTotalElements());

        return clientPage.getContent();
    }

    
    public Set<ClientStatus> getClientStatuses() {
        return clientStatuses;
    }

    
    public void setClientStatuses(Set<ClientStatus> clientStatuses) {
        this.clientStatuses = clientStatuses;
    }
}
