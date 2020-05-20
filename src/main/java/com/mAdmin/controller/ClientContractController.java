package com.mAdmin.controller;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.model.ClientContractLazyDataModel;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.security.UserPrincipal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.time.LocalDate;
import java.util.Optional;


@Controller
@Scope(value = "view")
public class ClientContractController {

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    @Autowired
    private ClientContractLazyDataModel model;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    private final Log logger = LogFactory.getLog(this.getClass());

    
    private long clientId;

    
    private Client client;

    
    private LocalDate terminationDate;

    
    private LocalDate minDate;

    
    private LocalDate maxDate;

    
    private Contract contract;

    
    @PostConstruct
    public void init() {
        contract = null;
    }

    
    public void onLoad() {
        if (clientId == 0) {
            client = (((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClient());
            model.setClient(client);
        } else {
            Optional<Client> clientById = clientRepository.findById(clientId);
            if (clientById.isPresent()) {
                client = clientById.get();
                model.setClient(client);
            }
        }
    }

    
    public void populateContract(Contract preContract) {
        contract = preContract;
        terminationDate = preContract.getTerminationDate();
        minDate = LocalDate.now();
        if (contract.getType() == ContractType.TERMINATED) {
            maxDate = LocalDate.now().minusDays(1);
        } else {
            maxDate = contract.getValidTo();
        }
    }

    
    public void save() {

       logger.info("------------- Attendees contract termination by employee was STARTED ----------------------------");

        FacesContext context = FacesContext.getCurrentInstance();
        contract.setTerminationDate(terminationDate);
        contractRepository.saveAndFlush(contract);
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "contract.termination.msg", context);

        logSuccessfulAttendeesContractTerminationByEmployee();
    }

    
    private void logSuccessfulAttendeesContractTerminationByEmployee() {
       Long userId = ((UserPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getUserId();
       logger.info("Attendees ID "
                + contract.getAttendee().getId() + " contract " + contract.getNumber() + " was terminated"
               + " by employee ID " + userId + " with termination date " + terminationDate.toString());

       logger.info("------------- Attendees contract termination by employee was FINISHED ---------------------------");

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

    
    public ClientContractLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(ClientContractLazyDataModel model) {
        this.model = model;
    }

    
    public long getClientId() {
        return clientId;
    }

    
    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    
    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    
    public Contract getContract() {
        return contract;
    }

    
    public void setContract(Contract contract) {
        this.contract = contract;
    }

    
    public LocalDate getMinDate() {
        return minDate;
    }

    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    
    public LocalDate getMaxDate() {
        return maxDate;
    }

    
    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

}
