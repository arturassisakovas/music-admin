package com.mAdmin.service;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Payment;
import com.mAdmin.entity.Subscription;


public interface InvoiceService {

    
    List<Invoice> getAll();

    
    void generatePdf(Invoice invoice, Payment payment) throws DocumentException, IOException;

    
    Invoice saveAndGenerateNumber(Invoice invoice);

    
    List<Invoice> saveAllAndGenerateNumbers(List<Invoice> invoices);

    
    String formSubscriptionStartAndEndDate(Invoice invoice);

    
    List<Invoice> invalidateInvoices();

    
    List<Invoice> formNonPaidList(List<Invoice> invoices);

    
    Invoice formOneCentInvoice(Client client, Invoice invoice, List<Subscription> subscriptions);

    
    Invoice createInvoice(Attendee attendee);

    
    void invalidateInvoiceIfSingleAttendee(Contract contract);
}
