package com.mAdmin.service;

import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Payment;
import com.mAdmin.model.InvoiceRecordCreationModel;


public interface InvoiceRecordService {

    
    InvoiceRecord createInvoiceRecord(InvoiceRecordCreationModel invoiceRecordCreationModel);

    
    void formOneCentRecord(Invoice oneCentInvoice, Contract contract, Invoice invoice, Payment oneCentPayment,
                           boolean setPrice);

    
    InvoiceRecord findInvoiceOfSingleAttendee(Contract contract);
}
