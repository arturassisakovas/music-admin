package com.mAdmin.service;

import com.itextpdf.text.DocumentException;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ContractService {

    
    List<Contract> getAll();

    
    Optional<Contract> getById(Long id);

    
    Contract getByAttendeeAndType(Attendee attendee, ContractType type);

    
    Contract saveAndGenerateNumber(Contract contract);

    
    List<Contract> saveAllAndGenerateNumbers(List<Contract> contracts);

    
    List<Contract> terminateContracts(List<Contract> contracts);

    
    void generatePdf(List<Contract> contracts, boolean showGdpr) throws DocumentException, IOException;

    
    String generateHtml(List<Contract> contracts, boolean showGdpr);

    
    List<Contract> generatePreviewContracts(
            List<Subscription> sessionSubscriptions, List<LocalDate> firstDays, List<Period> periods);

    
    InputStream generatePreviewPdf(List<Contract> contracts, boolean showGdpr) throws IOException, DocumentException;

    
    Contract fillContractForPdf(Attendee attendee, Contract contract, Period period);

    
    Contract fillContractForPdf(Contract newContract, int year, int month, Period period, Attendee attendee);
}
