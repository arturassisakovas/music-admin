package com.mAdmin.controller;

import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.InvoiceRepository;
import com.mAdmin.repository.PaymentRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Payment;
import com.mAdmin.model.InvoiceLazyDataModel;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.service.InvoiceRecordRepository;
import com.mAdmin.service.InvoiceService;
import com.mAdmin.service.PaymentService;
import com.mAdmin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@Scope(value = "view")
public class InvoiceController {

    
    @Autowired
    private InvoiceLazyDataModel model;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private PaymentService paymentService;

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Autowired
    private PaymentRepository paymentRepository;

    
    @Autowired
    private InvoiceRepository invoiceRepository;

    
    private Client client;

    
    private Invoice notPaidInvoice;

    
    private LocalDate createdAt;

    
    private boolean isForeigner;

    
    private boolean agreeWithTermsOfService;

    
    private Payment payment;

    
    public void onLoad() {
        client = (((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClient());
        setForeigner(client.getForeigner());
        model.setClient(client);
    }

    
    public LocalDate convertLocalDateTime(Invoice invoice) {
        return DateUtil.convertLocalDateTime(invoice.getPayment().getDatePaid());
    }

    
    public void redirectToPay() {
        Payment thePayment = new Payment();
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(notPaidInvoice.getId());
        optionalInvoice.ifPresent(invoice -> notPaidInvoice = invoice);

        if (notPaidInvoice.getPayment().getStatus() == null) {
            thePayment = notPaidInvoice.getPayment();
            thePayment.setStatus(false);
            paymentRepository.save(thePayment);
            thePayment = new Payment();
        }
        thePayment.setInvoice(notPaidInvoice);
        thePayment = paymentRepository.save(thePayment);
        paymentService.processPayment(notPaidInvoice, thePayment.getId());
    }

    
    public boolean displayMessage(Invoice invoice) {
        return invoice.getPayment().getDatePaid() != null;
    }

    
    public boolean displayStyle(Invoice invoice) {
        payment = paymentRepository.findByInvoiceAndDatePaidNotNull(invoice);
        return LocalDate.now().isAfter(invoice.getExpireDate()) && payment == null;
    }

    
    public String findAttendee(Invoice invoice) {
        List<InvoiceRecord> invoiceRecords = invoice.getRecords();
        List<String> str = new ArrayList<>();

        invoiceRecords.forEach(s -> str.add(s.getAttendee().getName() + " " + s.getAttendee().getSurname()));
        return String.join(", ", str);
    }

    
    public boolean checkIfContractIsSigned(Invoice invoice) {
        InvoiceRecord invoiceRecord = invoiceRecordRepository.findFirstByInvoice(invoice);
        if (invoiceRecord.getContract().getDateSigned() == null) {
            BigDecimal price = invoice.getPrice();
            return !(price.compareTo(BigDecimal.ZERO) <= 0);
        } else {
            return false;
        }
    }

    
    public boolean isInvoiceValid() {
        if (notPaidInvoice != null) {
            Optional<Invoice> invoice = invoiceRepository.findById(notPaidInvoice.getId());
            return invoice.map(Invoice::isValid).orElse(false);
        }
        return true;
    }

    
    public void validateForm() {
        Payment thePayment = new Payment();
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(notPaidInvoice.getId());
        optionalInvoice.ifPresent(invoice -> notPaidInvoice = invoice);

        if (notPaidInvoice.getPayment().getStatus() == null) {
            thePayment = notPaidInvoice.getPayment();
            thePayment.setStatus(false);
            paymentRepository.save(thePayment);
            thePayment = new Payment();
        }

        thePayment.setInvoice(notPaidInvoice);
        thePayment = paymentRepository.save(thePayment);

        List<Attendee> attendees = new ArrayList<>();
        notPaidInvoice.getRecords().forEach(ir -> {
            attendees.add(ir.getAttendee());
        });

        paymentService.extendPayTime(attendees);
        paymentService.processPayment(notPaidInvoice, "", thePayment.getId());
    }

    
    public String subscriptionStartAndEndDate(Invoice invoice) {
        return invoiceService.formSubscriptionStartAndEndDate(invoice);
    }

    
    public boolean displayDate(Invoice invoice) {
        payment = paymentRepository.findByInvoiceAndDatePaidNotNull(invoice);
        return payment != null;
    }

    
    public boolean invoiceBeingTerminated(Invoice invoice) {
        if (invoice.getType() != InvoiceType.IMMINENT_TERMINATION) {
            List<InvoiceRecord> invoiceRecords = invoice.getRecords();
            for (InvoiceRecord invoiceRecord : invoiceRecords) {
                if (invoiceRecord.getContract().getTerminationDate() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public InvoiceLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(InvoiceLazyDataModel model) {
        this.model = model;
    }

    
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    
    public void setRowsPerPageTemplate(String rowsPerPageTemplate) {
        this.rowsPerPageTemplate = rowsPerPageTemplate;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    
    public Invoice getNotPaidInvoice() {
        return notPaidInvoice;
    }

    
    public void setNotPaidInvoice(Invoice notPaidInvoice) {
        this.notPaidInvoice = notPaidInvoice;
    }


    
    public boolean isForeigner() {
        return isForeigner;
    }

    
    private void setForeigner(boolean foreigner) {
        isForeigner = foreigner;
    }

    
    public boolean isAgreeWithTermsOfService() {
        return agreeWithTermsOfService;
    }

    
    public void setAgreeWithTermsOfService(boolean agreeWithTermsOfService) {
        this.agreeWithTermsOfService = agreeWithTermsOfService;
    }
}
