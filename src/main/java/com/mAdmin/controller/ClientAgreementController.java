package com.mAdmin.controller;

import com.mAdmin.enumerator.AgreementType;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.entity.Agreement;
import com.mAdmin.entity.Client;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


@Controller
@Scope(value = "view")
public class ClientAgreementController {

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private ClientService clientService;

    
    private Boolean directMarketing;

    
    private Boolean dataProcessing;

    
    public void checkClientAgreements() {
        Optional<Client> client = getClientFromDatabase();
            if (client.isPresent()) {
                List<Agreement> clientAgreements = (List<Agreement>) client.get().getAgreements();
            for (Agreement agreement : clientAgreements) {
                if (agreement.getType() == AgreementType.DATA_PROCESSING_AGREES) {
                    dataProcessing = true;
                } else if (agreement.getType() == AgreementType.DATA_PROCESSING_DISAGREES) {
                    dataProcessing = false;
                }

                if (agreement.getType() == AgreementType.DIRECT_MARKETING_AGREES) {
                    directMarketing = true;
                } else if (agreement.getType() == AgreementType.DIRECT_MARKETING_DISAGREES) {
                    directMarketing = false;
                }
            }
        }
    }

    
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

        Optional<Client> client = getClientFromDatabase();
        if (client.isPresent()) {
            Client freshClient = client.get();
            freshClient.getAgreements().clear();
            clientService.saveClientAgreements(directMarketing, dataProcessing, freshClient);
        }
        context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", msg.
                        getString("client.profile.agreement.changes.saved")));
        ec.getFlash().setKeepMessages(true);
    }

    
    private Optional<Client> getClientFromDatabase() {
        Long clientId = ((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClientId();
        return clientRepository.findById(clientId);
    }

    
    public Boolean getDirectMarketing() {
        return directMarketing;
    }

    
    public void setDirectMarketing(Boolean directMarketing) {
        this.directMarketing = directMarketing;
    }

    
    public Boolean getDataProcessing() {
        return dataProcessing;
    }

    
    public void setDataProcessing(Boolean dataProcessing) {
        this.dataProcessing = dataProcessing;
    }
}
