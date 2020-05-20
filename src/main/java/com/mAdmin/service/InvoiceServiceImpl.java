package com.mAdmin.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.InvoiceRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Payment;
import com.mAdmin.entity.Subscription;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.itextpdf.text.DocumentException;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.util.AmountInWordsUtil;


@Service

public class InvoiceServiceImpl implements InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private TemplateEngine templateEngine;

    
    @Autowired
    private AmazonClient amazonClient;

    
    @Autowired
    private Environment env;

    
    @Autowired
    private PaymentService paymentService;

    
    @Autowired
    private PDFService pdfService;

    
    @Autowired
    private InvoiceRecordService invoiceRecordService;

    
    private static final String INVOICES = "Sąskaitos";

    
    private static final String S3bucketPath = "j58vhu0b6n24/public/music-admin-bucket/";

    
    private final int fifteen = 15;

    
    private final Log logger = LogFactory.getLog(this.getClass());

    
    @Override
    public List<Invoice> getAll() {

        return invoiceRepository.findAll();

    }

    
    public void generatePdf(Invoice invoice, Payment payment) throws DocumentException, IOException {

        final String pdfExtension = ".pdf";
        int currentYear = LocalDate.now().getYear();

        Long clientId = invoice.getClient().getId();

        Optional<Client> optionalClient = clientRepository.findById(clientId);

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            Context context = new Context();

            Map<String, Object> contextMap = new HashMap<>();

            contextMap.put("env", env);

            contextMap.put("invoice", invoice);

            contextMap.put("client", client);

            contextMap.put("amountInWords", AmountInWordsUtil.amountInWords(invoice.getPrice()));

            contextMap.put("datePaid", payment.getDatePaid());

            context.setVariables(contextMap);

            String html = templateEngine.process("pdf/invoice", context);

            String fileName = invoice.getInvoiceNumber().concat(pdfExtension);
            String documentPath = S3bucketPath + INVOICES + "/" + currentYear + "/" + fileName;

            File generatedFile = pdfService.generateFile(fileName, html);

            amazonClient.uploadFile(generatedFile, documentPath);

            invoice.setDocumentPath(documentPath);
            invoiceRepository.save(invoice);
        }
    }

    @Override
    @Transactional
    public Invoice saveAndGenerateNumber(Invoice invoice) {

        invoice = invoiceRepository.save(invoice);
        generateNumber(invoice);
        invoice = invoiceRepository.saveAndFlush(invoice);

        return invoice;
    }

    @Override
    @Transactional
    public List<Invoice> saveAllAndGenerateNumbers(List<Invoice> invoices) {

        invoices = invoiceRepository.saveAll(invoices);

        for (Invoice invoice : invoices) {
            generateNumber(invoice);
        }

        invoices = invoiceRepository.saveAll(invoices);

        return invoices;

    }

    
    private void generateNumber(Invoice invoice) {

        if (invoice.getId() == null) {
            throw new IllegalArgumentException("To generate invoice number id must not be null.");
        }

        final String staticPart = "MA-SF-";

        LocalDateTime dateTime = invoice.getCreatedAt();
        LocalDateTime yearBeginning = LocalDate.of(dateTime.getYear(), 1, 1).atStartOfDay();
        Invoice lastYearInvoice = invoiceRepository.findTopByCreatedAtBeforeOrderByIdDesc(yearBeginning);

        Long invoiceNumber;

        if (lastYearInvoice != null) {
            invoiceNumber = invoice.getId() - lastYearInvoice.getId();
        } else {
            invoiceNumber = invoice.getId();
        }

        String fullInvoiceNumber = staticPart;

        String currentYear = String.valueOf(dateTime.getYear());

        fullInvoiceNumber = fullInvoiceNumber.concat(currentYear).concat("-").concat(String.valueOf(invoiceNumber));

        invoice.setInvoiceNumber(fullInvoiceNumber);
    }

    
    public String formSubscriptionStartAndEndDate(Invoice invoice) {
        List<InvoiceRecord> invoiceRecords = invoice.getRecords();
        LocalDate createdAt = invoiceRecords.get(0).getPeriodStartDate();
        LocalDate endDate = invoiceRecords.get(0).getPeriodEndDate();

        for (InvoiceRecord invoiceRecord : invoiceRecords) {
            if (createdAt.isAfter(invoiceRecord.getPeriodStartDate())) {
                createdAt = invoiceRecord.getPeriodEndDate();
            }
            if (endDate.isAfter(invoiceRecord.getPeriodStartDate())) {
                endDate = invoiceRecord.getPeriodEndDate();
            }
        }
        return createdAt.toString() + " - " + endDate.toString();
    }

    
    public List<Invoice> invalidateInvoices() {
        List<Invoice> editedInvoices = new ArrayList<>();
        LocalDate today = LocalDate.now();
            List<Invoice> invoices = invoiceRepository
                    .findByExpireDateAndType(today.withDayOfMonth(fifteen), InvoiceType.IMMINENT_TERMINATION);
            for (Invoice invoice : invoices) {
                if (invoice.getRecords().get(0).getAttendee().getContract().getTerminationDate() == today) {
                    logger.info("------- unpaid invoice id : " + invoice.getId() + " -----------------------------------");
                    invoice.setValid(false);
                    invoice.setType(InvoiceType.WORKOUTS_PAYMENT);
                    invoiceRepository.saveAndFlush(invoice);
                    editedInvoices.add(invoice);
                }
            }
        return editedInvoices;
    }

    
    public List<Invoice> formNonPaidList(List<Invoice> invoices) {
        List<Invoice> nonPaidInvoices = new ArrayList<>();
        for (Invoice invoice : invoices) {
            if (!paymentService.isInvoicePaid(invoice)) {
                nonPaidInvoices.add(invoice);
            }
        }
        return nonPaidInvoices;
    }

    @Override
    public Invoice formOneCentInvoice(Client client, Invoice invoice, List<Subscription> subscriptions) {
        Invoice oneCentInvoice = new Invoice();
        oneCentInvoice.setClient(client);
        oneCentInvoice.setExpireDate(invoice.getExpireDate());
        oneCentInvoice.setType(InvoiceType.CONTRACT_CONFIRMATION);
        oneCentInvoice.setSubscriptions(subscriptions);

        return oneCentInvoice;
    }


    
    public Invoice createInvoice(Attendee attendee) {
        Invoice invoice = new Invoice();
        invoice.setType(InvoiceType.WORKOUTS_PAYMENT);
        invoice.setClient(attendee.getClient());

        invoice.setExpireDate(LocalDate.now().withDayOfMonth(fifteen));

        invoice = saveAndGenerateNumber(invoice);

        paymentService.createInvoicePayment(invoice);
        return invoice;
    }

    @Override
    public void invalidateInvoiceIfSingleAttendee(Contract contract) {
        InvoiceRecord invoiceRecord = invoiceRecordService.findInvoiceOfSingleAttendee(contract);
        if (invoiceRecord != null) {
            Invoice invoice = invoiceRecord.getInvoice();
            if (invoice.getRecords().size() == 1 && invoice.getPayment().getDatePaid() == null) {
                invoice.setValid(false);
                invoiceRepository.save(invoice);
            }
        }
    }
}
