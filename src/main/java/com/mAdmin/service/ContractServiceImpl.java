package com.mAdmin.service;

import com.itextpdf.text.DocumentException;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.repository.CabinetWeekdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Season;
import com.mAdmin.entity.Subscription;
import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;


@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private TemplateEngine templateEngine;

    
    @Autowired
    private AmazonClient amazonClient;

    
    @Autowired
    private Environment env;

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    @Autowired
    private PDFService pdfService;

    
    @Autowired
    private InvoiceService invoiceService;

    
    private static final String CONTRACTS = "Sutartys";

    
    private static final String S3bucketPath = "j58vhu0b6n24/public/music-admin-bucket/";

    
    @Override
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

    
    @Override
    public Optional<Contract> getById(Long id) {

        return contractRepository.findById(id);

    }

    
    @Override
    public Contract getByAttendeeAndType(Attendee attendee, ContractType type) {

        return contractRepository.findByAttendeeAndType(attendee, type);

    }

    @Override
    @Transactional
    public Contract saveAndGenerateNumber(Contract contract) {

        contract = contractRepository.save(contract);
        contract = generateNumber(contract);
        contract = contractRepository.save(contract);

        return contract;
    }


    @Override
    @Transactional
    public List<Contract> saveAllAndGenerateNumbers(List<Contract> contracts) {

        contracts = contractRepository.saveAll(contracts);

        for (Contract contract : contracts) {
            generateNumber(contract);
        }

        contracts = contractRepository.saveAll(contracts);

        return contracts;

    }

    
    private Contract generateNumber(Contract contract) {

        if (contract.getId() == null) {
            throw new IllegalArgumentException("To generate contract number id must not be null.");
        }

        final String staticPart = "MA-";

        LocalDateTime dateTime = contract.getCreatedAt();
        LocalDateTime yearBeginning = LocalDate.of(dateTime.getYear(), 1, 1).atStartOfDay();
        Contract lastYearContract = contractRepository.findTopByCreatedAtBeforeOrderByIdDesc(yearBeginning);

        Long contractNumber;

        if (lastYearContract != null) {
            contractNumber = contract.getId() - lastYearContract.getId();
        } else {
            contractNumber = contract.getId();
        }

        String fullContractNumber = staticPart;

        String currentYear = String.valueOf(dateTime.getYear());

        fullContractNumber = fullContractNumber.concat(currentYear);
        fullContractNumber = fullContractNumber.concat("-");
        fullContractNumber = fullContractNumber.concat(String.valueOf(contractNumber));

        contract.setNumber(fullContractNumber);

        return contract;

    }

    
    @Override
    @Transactional
    public List<Contract> terminateContracts(List<Contract> contracts) {
        if (contracts != null) {
            List<Contract> terminatedContracts = new ArrayList<>();
            for (Contract contract : contracts) {
                contract.setType(ContractType.TERMINATED);
                terminatedContracts.add(contract);
                invoiceService.invalidateInvoiceIfSingleAttendee(contract);
            }
            return contractRepository.saveAll(terminatedContracts);
        }
        return new ArrayList<>();
    }

    
    @Override
    public void generatePdf(List<Contract> contracts, boolean showGdpr) throws DocumentException, IOException {
        for (Contract contract: contracts) {
            final String pdfExtension = ".pdf";
            int currentYear = LocalDate.now().getYear();

            String html = generateHtml(Collections.singletonList(contract), showGdpr);

            String fileName = contract.getNumber().concat(pdfExtension);
            String documentPath = S3bucketPath + CONTRACTS + "/" + currentYear + "/" + fileName;

            File generatedFile = pdfService.generateFile(fileName, html);

            amazonClient.uploadFile(generatedFile, documentPath);

            contract.setDocumentPath(documentPath);
            contractRepository.save(contract);
        }
    }

    @Override
    public String generateHtml(List<Contract> contracts, boolean showGdpr) {

        Context context = new Context();

        Map<String, Object> contextMap = new HashMap<>();

        contextMap.put("env", env);

        contextMap.put("showGDPR", showGdpr);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle resourceBundle = facesContext.getApplication()
                .getResourceBundle(FacesContext.getCurrentInstance(), "label");
        contextMap.put("label", resourceBundle);

        contextMap.put("contracts", contracts);

        ResourceBundle calendarLabel = facesContext.getApplication().getResourceBundle(facesContext, "calendarLabel");
        String monthsNames = calendarLabel.getString("monthNamesForContract");
        String[] monthsLabels = monthsNames.split(",");

        List<Integer> contractYear = new ArrayList<>();
        List<String> contractMonth = new ArrayList<>();
        List<Integer> contractDay = new ArrayList<>();

        List<StringBuilder> sbWorkoutTimes = new ArrayList<>();

        List<String> lessonPrices = new ArrayList<>();

        List<String> workoutsPerWeeks = new ArrayList<>();

        for (Contract contract : contracts) {
            Season season = seasonRepository.findActiveSeason(contract.getValidTo());
            if (contract.getCreatedAt() != null) {
                contractYear.add(contract.getCreatedAt().getYear());
                contractMonth.add(monthsLabels[contract.getCreatedAt().getMonthValue() - 1]);
                contractDay.add(contract.getCreatedAt().getDayOfMonth());
            } else {
                contractYear.add(LocalDate.now().getYear());
                contractMonth.add(monthsLabels[LocalDate.now().getMonthValue() - 1]);
                contractDay.add(LocalDate.now().getDayOfMonth());
            }

            Group groupToCheck;
            if (contract.getAttendee().getNewGroup() == null) {
                groupToCheck = contract.getAttendee().getGroup();
            } else {
                groupToCheck = contract.getAttendee().getNewGroup();
            }
            List<TrackWeekday> trackWeekDays = cabinetWeekdayRepository
                    .findByTrackPeriodId(groupToCheck.getTrackPeriod().getId());
            StringBuilder workoutTimes = new StringBuilder();
            String daysNames = calendarLabel.getString("weekdaysnames");
            String[] daysLabels = daysNames.split(",");

            for (TrackWeekday trackWeekday : trackWeekDays) {
                List<TrackWorkingHour> trackWorkingHours = cabinetWorkingHourRepository
                        .findAllByGroupAndTrackWeekday(groupToCheck, trackWeekday);
                for (TrackWorkingHour trackWorkingHour : trackWorkingHours) {
                    workoutTimes.append(daysLabels[trackWeekday.getDayOfWeek().ordinal()] + " ("
                            + TimeUtil.minutesToTimeConverter(trackWorkingHour.getStartHour()) + "-"
                            + TimeUtil.minutesToTimeConverter(trackWorkingHour.getEndHour()) + ") ");
                }
            }
            sbWorkoutTimes.add(workoutTimes);

            BigDecimal groupPrice;
            final int six = 6;
            if (contract.getAttendee().getGroup().getNumOfAttendees() == six) {
                groupPrice = season.getSmallGroupPrice();
            } else {
                groupPrice = season.getBigGroupPrice();
            }
            lessonPrices.add(groupPrice + " EUR");

            String workoutsPerWeek = "";
            if (contract.getAttendee().getGroup().getWorkoutsPerWeek().getValue() == 1) {
                workoutsPerWeek = "1 kartas per savaitę";
            } else if (contract.getAttendee().getGroup().getWorkoutsPerWeek().getValue() == 2) {
                workoutsPerWeek = "2 kartai per savaitę";
            }
            workoutsPerWeeks.add(workoutsPerWeek);

        }

        contextMap.put("contractYear", contractYear);
        contextMap.put("contractMonth", contractMonth);
        contextMap.put("contractDay", contractDay);
        contextMap.put("workoutTimes", sbWorkoutTimes);
        contextMap.put("lessonPrice", lessonPrices);
        contextMap.put("workoutsPerWeek", workoutsPerWeeks);

        context.setVariables(contextMap);

        return templateEngine.process("pdf/contract", context);
    }

    @Override
    public List<Contract> generatePreviewContracts(List<Subscription> sessionSubscriptions, List<LocalDate> firstDays,
                                                   List<Period> periods) {
        List<Contract> contracts = new ArrayList<>();
        for (int i = 0; i < sessionSubscriptions.size(); i++) {
            Contract contract = new Contract();
            contract.setType(ContractType.NOT_ACTIVE);
            contract.setAttendee(sessionSubscriptions.get(i).getAttendee());

            contract.setValidFrom(firstDays.get(i).withDayOfMonth(1));
            contract.setValidTo(periods.get(i).getEndDate());

            contract.setNumber("");
            contracts.add(contract);
        }
        return contracts;
    }

    @Override
    public InputStream generatePreviewPdf(List<Contract> contracts, boolean showGdpr)
            throws IOException, DocumentException {
        String html = generateHtml(contracts, showGdpr);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public Contract fillContractForPdf(Attendee attendee, Contract contract, Period period) {
        contract.setAttendee(attendee);
        contract.setType(ContractType.NOT_ACTIVE);
        contract.setValidFrom(attendee.getWorkoutStartDate().withDayOfMonth(1));
        contract.setValidTo(period.getEndDate());
        contract.setNumber("");
        return contract;
    }

    @Override
    public Contract fillContractForPdf(Contract newContract, int year, int month, Period period, Attendee attendee) {
        LocalDate newContractValidFrom = LocalDate.of(year, month, 1);
        LocalDate contractValidTo = period.getEndDate();


        newContract.setNumber("");
        newContract.setAttendee(attendee);
        newContract.setType(ContractType.NOT_ACTIVE);
        newContract.setValidFrom(newContractValidFrom);
        newContract.setValidTo(contractValidTo);

        return newContract;
    }
}
