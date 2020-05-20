package com.mAdmin.controller;

import com.mAdmin.enumerator.AgreementType;
import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.entity.Agreement;
import com.mAdmin.entity.Client;
import com.mAdmin.model.ClientLazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@Scope(value = "view")
public class ClientController {

    
    @Autowired
    private ClientLazyDataModel model;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    private Client client;

    
    private List<Client> clients = new ArrayList<>();

    
    @PostConstruct
    public void init() {
        Set<ClientStatus> clientStatuses = new HashSet<>();
        clientStatuses.add(ClientStatus.ACTIVE);

        model.setClientStatuses(clientStatuses);
    }

    
    public String previewClient(Long clientId) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionRedirectMap = externalContext.getSessionMap();
        sessionRedirectMap.put("redirectPath", "clients");
        return "/admin/clientsPreview.xhtml?clientId=" + clientId + "&faces-redirect=true";
    }

    
    public Boolean directMarketingDecision(Collection<Agreement> agreements) {
        List<Agreement> clientAgreements = (List<Agreement>) agreements;
        for (Agreement agreement : clientAgreements) {
            if (agreement.getType() == AgreementType.DIRECT_MARKETING_AGREES) {
                return true;
            } else {
                if (agreement.getType() == AgreementType.DIRECT_MARKETING_DISAGREES) {
                    return false;
                }
            }
        }
        return null;
    }

    
    public Boolean dataProcessingDecision(Collection<Agreement> agreements) {
        List<Agreement> clientAgreements = (List<Agreement>) agreements;
        for (Agreement agreement : clientAgreements) {
            if (agreement.getType() == AgreementType.DATA_PROCESSING_AGREES) {
                return true;
            } else {
                if (agreement.getType() == AgreementType.DATA_PROCESSING_DISAGREES) {
                    return false;
                }
            }
        }
        return null;
    }

    
    public ClientLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(ClientLazyDataModel model) {
        this.model = model;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

    
    public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
        this.rowsPerPageTemplate = rowsPerPageTemplate;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public List<Client> getClients() {
        return clients;
    }

    
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
