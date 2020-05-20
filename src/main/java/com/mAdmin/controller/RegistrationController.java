package com.mAdmin.controller;

import com.itextpdf.text.DocumentException;
import com.mAdmin.entity.Client;
import com.mAdmin.util.PrimeFacesWrapper;
import com.mAdmin.service.ClientService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;


@Controller
@Scope(value = "session")
public class RegistrationController {

    
    @Autowired
    private PaymentController paymentController;

    
    @Autowired
    private ClientService clientService;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private Boolean dataProcessing = null;

    
    private Boolean directMarketing = null;

    
    private Client newClient;

    
    private boolean agreeWithTermsOfService = false;

    
    public void onLoad() {
        newClient = Faces.getSessionAttribute("newClient");
        agreeWithTermsOfService = false;
    }

    
    public boolean checkClientAgreementsPresence() {
        if (newClient != null) {
            return !newClient.getAgreements().isEmpty();
        }
        return true;
    }

    
    public void saveClientChoiceAndProceedToPayment() throws IOException, DocumentException {
        if (agreeWithTermsOfService) {
            if (!checkClientAgreementsPresence() && newClient != null) {
                clientService.saveClientAgreements(directMarketing, dataProcessing, newClient);
            }
            paymentController.validateForm();
        }
    }

    
    public boolean isAgreeWithTermsOfService() {
        return agreeWithTermsOfService;
    }

    
    public void setAgreeWithTermsOfService(boolean agreeWithTermsOfService) {
        this.agreeWithTermsOfService = agreeWithTermsOfService;
    }
    
    public Boolean getDataProcessing() {
        return dataProcessing;
    }

    
    public void setDataProcessing(Boolean dataProcessing) {
        this.dataProcessing = dataProcessing;
    }

    
    public Boolean getDirectMarketing() {
        return directMarketing;
    }

    
    public void setDirectMarketing(Boolean directMarketing) {
        this.directMarketing = directMarketing;
    }
}
