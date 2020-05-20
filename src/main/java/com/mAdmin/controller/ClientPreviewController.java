package com.mAdmin.controller;

import com.itextpdf.text.DocumentException;
import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.InvoiceRepository;
import com.mAdmin.repository.PaymentRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Payment;
import com.mAdmin.entity.Subscription;
import com.mAdmin.security.UserPrincipal;
import com.mAdmin.service.InvoiceService;
import com.mAdmin.util.PrimeFacesWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omnifaces.util.Faces;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@Scope(value = "session")
public class ClientPreviewController {

    
    private long clientId;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private InvoiceRepository invoiceRepository;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    @Autowired
    private PaymentRepository paymentRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    private final Log logger = LogFactory.getLog(this.getClass());

    
    private Client client;

    
    private List<Attendee> attendees;

    
    private List<Invoice> sortedInvoices;

    
    private Invoice invoice;

    
    private LocalDate createdAt;

    
    private LocalDate endDate;

    
    @PostConstruct
    public void init() {
        Optional<Client> clientById = clientRepository.findById(clientId);
        if (clientById.isPresent()) {
            client = clientById.get();
            if (client.getAttendees() != null) {
                List<Attendee> attendees = new ArrayList<>(client.getAttendees());
                setAttendees(attendees);
                List<Invoice> invoices = new ArrayList<>(client.getInvoices());
                sortedInvoices = invoices.stream().filter(Invoice::isValid).collect(Collectors.toList());
                sortedInvoices = sortedInvoices.stream().sorted(Comparator.comparing(Invoice::getCreatedAt).reversed())
                        .collect(Collectors.toList());
            } else {
                setAttendees(new ArrayList<>());
            }
        }
    }

    
    public void changeStatusOfInvoicePayment() throws IOException, DocumentException {
       logger.info("-------------- Clients invoice status change by employee was STARTED ----------------------------");

        FacesContext context = FacesContext.getCurrentInstance();
        Payment payment = invoice.getPayment();
        payment.setStatus(true);
        payment.setDatePaid(LocalDateTime.now());
        paymentRepository.save(payment);
        invoiceService.generatePdf(invoice, payment);
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "invoice.status.changed.message", context);

        logSuccessfulClientsInvoiceStatusChangeByEmployee();
    }

    
    public void setSelectInvoice(Invoice invoice) {
        this.invoice = invoice;
        PrimeFaces current = primeFacesWrapper.current();
        current.executeScript("PF('changeInvoiceStatusModal').show();");
        current.scrollTo("invoicesForm:changeInvoiceStatusModal");
    }

    
    public String subscriptionStartAndEndDate(Invoice invoice) {
        return invoiceService.formSubscriptionStartAndEndDate(invoice);
    }

    
    public String findAttendee(Invoice invoice) {
        List<Subscription> subscriptions = subscriptionRepository.findByInvoice(invoice);
        List<String> str = new ArrayList<>();

        subscriptions.forEach(s -> str.add(s.getAttendee().getName() + " " + s.getAttendee().getSurname()));
        return String.join(", ", str);
    }

    
    public String redirectBack() {
        String redirectPath = Faces.getSessionAttribute("redirectPath");
        String redirectUrl = ("/admin/" + redirectPath);
        return redirectUrl;

    }

    
    private void logSuccessfulClientsInvoiceStatusChangeByEmployee() {
       Long userId = ((UserPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getUserId();
       logger.info("---- Clients ID "
                + invoice.getClient().getId() + " invoice " + invoice.getInvoiceNumber() + " status was changed"
                + " to PAID by employee ID " + userId + " ------------");

       logger.info("-------------- Clients invoice status change by employee was FINISHED ---------------------------");
    }

    
    public long getClientId() {
        return clientId;
    }

    
    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public List<Attendee> getAttendees() {
        return attendees;
    }

    
    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    
    public List<Invoice> getSortedInvoices() {
        return sortedInvoices;
    }

    
    public void setSortedInvoices(List<Invoice> sortedInvoices) {
        this.sortedInvoices = sortedInvoices;
    }

    
    public InvoiceType getInvoiceTypeThatDisablesStatusChange() {
        return InvoiceType.CONTRACT_CONFIRMATION;
    }
}
