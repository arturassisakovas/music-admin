package com.mAdmin.controller;

import com.amazonaws.util.IOUtils;
import com.itextpdf.text.DocumentException;
import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Subscription;
import com.mAdmin.service.ContractService;
import com.mAdmin.service.PeriodService;
import org.omnifaces.util.Faces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ContractController {

    
    public static final String CONTRACT_PREVIEW_FILE_NAME = "sutartis.pdf";

    
    @Autowired
    private ContractService contractService;

    
    @Autowired
    private PeriodService periodService;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    private StreamedContent preview;

    
    private InputStream inputStream;

    
    private String base64String;

    
    public void setUpPreview() throws Exception {
        createContractPreview();
        try {
            setPreview(new DefaultStreamedContent(inputStream, "application/pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void setUpDownload() throws Exception {
        createContractPreview();
        try {
            base64String = Base64.getEncoder().encodeToString(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void setUpPreviewForReservedAttendee(Attendee attendee, Group group) throws IOException, DocumentException {
        createSingleContractPreviewForReservedAttendee(attendee, group);
        try {
            setPreview(new DefaultStreamedContent(inputStream, "application/pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void setUpPreviewForGroupChange(Attendee attendee, Group group, int year, int month, Period period)
            throws IOException, DocumentException {
        createSingleContractPreviewForGroupChange(attendee, group, year, month, period);
        try {
            setPreview(new DefaultStreamedContent(inputStream, "application/pdf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void createSingleContractPreviewForGroupChange(Attendee attendee, Group group,
                                                          int year, int month, Period period)
            throws IOException, DocumentException {


        attendee.setNewGroup(group);
        Contract newContract = new Contract();
        newContract = contractService.fillContractForPdf(newContract, year, month, period, attendee);
        generateReviewPdf(Collections.singletonList(newContract), false);
    }

    
    public void createSingleContractPreviewForReservedAttendee(Attendee attendee, Group group)
            throws IOException, DocumentException {

        Period period = periodService.getPeriodForFittingSeason(group.getTrackPeriod().getStartDate(),
                group.getTrackPeriod().getEndDate());

        List<LocalDate> possibleWorkoutDays = new ArrayList<>();
        groupWorkoutRepository.findByGroupIdAndDateBetween(group.getId(),
                LocalDate.now().withDayOfMonth(1).plusMonths(1), period.getEndDate()).forEach(
                        pwd -> possibleWorkoutDays.add(pwd.getDate()));

        Optional<LocalDate> optionalFirstDay = possibleWorkoutDays.stream().min(Comparator.comparing(
                LocalDate::toEpochDay));

        optionalFirstDay.ifPresent(attendee::setWorkoutStartDate);

        Contract contract = new Contract();
        attendee.setGroup(group);
        contract = contractService.fillContractForPdf(attendee, contract, period);


        generateReviewPdf(Collections.singletonList(contract), false);
    }

    
    public void createContractPreview() throws IOException, DocumentException {
        List<Contract> contracts = new ArrayList<>();

        List<Subscription> sessionSubscriptions = Faces.getSessionAttribute("theSubscription");
        List<Period> periods = Faces.getSessionAttribute("periods");
        List<LocalDate> firstDays = Faces.getSessionAttribute("firstWorkoutDays");

        contracts = contractService.generatePreviewContracts(sessionSubscriptions, firstDays, periods);

        generateReviewPdf(contracts, false);
    }

    
    private void generateReviewPdf(List<Contract> contracts, boolean showGdpr) throws IOException, DocumentException {

        String html = contractService.generateHtml(contracts, showGdpr);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();

    }

    
    public void createMultipleContractPreviewForDekstop(List<InvoiceRecord> invoiceRecords)
            throws IOException, DocumentException {

        List<Contract> contracts = new ArrayList<>();
        for (InvoiceRecord invoiceRecord : invoiceRecords) {
            contracts.add(invoiceRecord.getContract());
        }

        String html = contractService.generateHtml(contracts, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        setPreview(new DefaultStreamedContent(inputStream, "application/pdf"));
    }

    
    public void createMultipleContractPreviewForMobile(List<InvoiceRecord> invoiceRecords)
            throws IOException, DocumentException {

        List<Contract> contracts = new ArrayList<>();
        for (InvoiceRecord invoiceRecord : invoiceRecords) {
            contracts.add(invoiceRecord.getContract());
        }

        String html = contractService.generateHtml(contracts, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.close();
        base64String = Base64.getEncoder().encodeToString(IOUtils.toByteArray(inputStream));
    }

    
    @GetMapping(value = "/contract/preview/file")
    public void downloadContractPreviewFile(HttpServletResponse response) throws IOException {
        if (inputStream != null) {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.addHeader("Content-Disposition", "attachment; "
                    + "filename=" + CONTRACT_PREVIEW_FILE_NAME);
            ServletOutputStream outputStream = response.getOutputStream();
            inputStream.reset();
            inputStream.transferTo(outputStream);
            outputStream.flush();
        }
    }

    
    public InputStream getInputStream() {
        return inputStream;
    }

    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    
    public String getBase64String() {
        return base64String;
    }

    
    public void setBase64String(String base64String) {
        this.base64String = base64String;
    }

    
    public StreamedContent getPreview() {
        return preview;
    }

    
    public void setPreview(StreamedContent preview) {
        this.preview = preview;
    }

}
