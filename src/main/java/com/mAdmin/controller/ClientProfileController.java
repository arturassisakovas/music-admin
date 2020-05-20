package com.mAdmin.controller;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.MedicalCertificateStatus;
import com.mAdmin.repository.AttendanceRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.repository.MedicalCertificateRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.MedicalCertificate;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.service.AmazonClient;
import com.mAdmin.service.AttendeeService;
import com.mAdmin.service.ClientService;
import com.mAdmin.service.EmailService;
import com.mAdmin.util.DateUtil;
import com.mAdmin.util.PrimeFacesWrapper;
import com.mAdmin.util.TimeUtil;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;



@Controller
@Scope("session")
public class ClientProfileController {

    
    @Autowired
    private AttendeeService attendeeService;

    
    @Autowired
    private AmazonClient amazonClient;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private AttendanceRepository attendanceRepository;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private MedicalCertificateRepository medicalCertificateRepository;

    
    @Autowired
    private ClientService clientService;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private String content;

    
    private String name, surname, email, oldEmail, phone;

    
    private Client client;

    
    private List<Attendee> attendees = new ArrayList<>();

    
    private int attendeeId;

    
    private boolean singleAttendee;

    
    private String enteredCode;

    
    private boolean matchedCodes;

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    private boolean disabledButton;

    
    private String emailSubject;

    
    private String emailText;

    
    private String ageGroupString;

    
    private List<String> namesOfCoaches = new ArrayList<>();

    
    private List<String> timesOfGroup = new ArrayList<>();

    
    private List<Attendance> attendanceListOfMonth = new ArrayList<>();

    
    private List<String> weekDays = new ArrayList<>();

    
    private List<LocalDate> attendanceListForMonthlyCalendar;

    
    private LocalDate selectedAttendanceDate;

    
    private List<GroupWorkout> groupWorkoutsListOfMonth = new ArrayList<>();

    
    private List<Contract> attendeeContracts = new ArrayList<>();

    
    private Set<Attendee> registeredAttendees = new HashSet<>();

    
    private Boolean present;

    
    private boolean contractActive = true;

    
    private Set<Attendance> attendance = new HashSet<>();

    
    private Attendee selectedAttendee;

    
    private List<MedicalCertificate> medicalCertificates = new ArrayList<>();

    
    private Contract contract;

    
    private boolean groupChangedContract;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private Boolean displayByGroupWorkouts;

    
    private Boolean firstLesson = true;

    
    private int secondLessonEnd = 0;

    
    private List<Attendance> sortedAttendance = new ArrayList<>();

    
    @PostConstruct
    public void init() {
        matchedCodes = false;
        disabledButton = false;

        content = "/client/contacts";
        Long clientId = ((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClientId();

        client = clientRepository.getOne(clientId);
    }

    
    public void onLoad() {
        Long clientId = ((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClientId();

        client = clientRepository.getOne(clientId);
        setOldEmail(client.getEmail());
        email = client.getEmail();

        registeredAttendees = client.getAttendees();
        attendees.clear();
        attendees.addAll(registeredAttendees);
        registeredAttendees.removeIf(a -> !a.getActive());

        namesOfCoaches = new ArrayList<>();
        timesOfGroup = new ArrayList<>();
        ageGroupString = "";
        attendanceListOfMonth = new ArrayList<>();

        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();

        if (!client.getAttendees().isEmpty()) {
            if (selectedAttendee == null) {
                sessionMap.put("activeIndex", 0);
                selectedAttendee = attendees.get(0);
                attendeeContracts = contractRepository.findByAttendeeAndTypeNot(selectedAttendee, ContractType.INVALID);
            }

            if (!attendeeContracts.isEmpty()) {
                displayCalendarWithOnlyMonth();
                formatAttendanceDate();
            }

            sortAndUpdateMedicalCertificates();
            ResourceBundle calendarLabel = context.getApplication().getResourceBundle(context, "calendarLabel");
            setWeekDays(Arrays.asList(calendarLabel.getString("weekdaysnames").split(",")));
        }

    }

    
    public boolean checkIfAttendeeIsRegistered(Attendee attendee) {
        return registeredAttendees.contains(attendee);
    }

    
    public void confirmEmailAndSave() {
        FacesContext context = FacesContext.getCurrentInstance();
        setMatchedCodes(false);
        setEnteredCode(null);
        Client oldClientData = clientRepository.getOne(client.getId());
        client.setPhone(oldClientData.getPhone());
        if ((clientRepository.findByEmail(email) != null || employeeRepository.findByEmail(email) != null)
                && !oldEmail.equals(email)) {
            setEmail(oldEmail);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "client.duplicate.email", context);
        } else if (oldEmail.equals(email)) {
            primeFacesWrapper.current().executeScript("changeSaveCancelIcons();");
            return;
        } else {
            primeFacesWrapper.current().executeScript("PF('confirmModal').show()");
            client.setEmail(email);
            toSendEmail();
        }

        if (!oldEmail.equals(email)) {
            setDisabledButton(false);
        }
        primeFacesWrapper.current().executeScript("changeSaveCancelIcons();");
        primeFacesWrapper.current().executeScript("PF('editPhoneButton').jq.show();");
        primeFacesWrapper.current().executeScript("PF('editEmailButton').jq.show();");
    }

    
    public void handleSave() {
        FacesContext context = FacesContext.getCurrentInstance();
        Client oldClientData = clientRepository.getOne(client.getId());
        client.setEmail(oldClientData.getEmail());
        if (!oldClientData.getPhone().equals(client.getPhone())) {
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "edit.success", context);
        }
        clientRepository.saveAndFlush(client);
        primeFacesWrapper.current().executeScript("changeSaveCancelIcons();");
        primeFacesWrapper.current().executeScript("PF('editPhoneButton').jq.show();");
        primeFacesWrapper.current().executeScript("PF('editEmailButton').jq.show();");
    }

    
    public void toSendEmail() {
        client = clientService.createNewConfirmationCode(client);
        emailService.formAndSendEmailToClientAboutConfirmCode(client);

    }

    
    public void resendEmail() {

        if (!disabledButton) {
            toSendEmail();
            setDisabledButton(true);
        }

    }

    
    public void compareCodes() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (getEnteredCode().equals(client.getConfirmationCode())) {
            setMatchedCodes(true);
            clientRepository.saveAndFlush(client);
            oldEmail = email;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "edit.success", context);
        } else {
            setMatchedCodes(false);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "client.notMatchedCode", context);
        }
    }

    
    public void onChange(TabChangeEvent event) {

        ageGroupString = "";
        attendanceListOfMonth = new ArrayList<>();

        Attendee attendee = (Attendee) event.getData();
        Optional<Attendee> optAttendee = attendeeService.getById(attendee.getId());
        if (optAttendee.isPresent()) {
            selectedAttendee = optAttendee.get();
            attendeeContracts = contractRepository.findByAttendeeAndTypeNot(selectedAttendee, ContractType.INVALID);

            attendanceListForMonthlyCalendar = null;

            sortAndUpdateMedicalCertificates();

            if (selectedAttendee.getActive()) {
                if (!attendeeContracts.isEmpty()) {
                    selectedAttendanceDate = null;
                    displayCalendarWithOnlyMonth();
                    formatAttendanceDate();
                }
            }
        }
    }

    
    public void checkIfContractIsActive() {

        contract = new Contract();
        displayByGroupWorkouts = null;
        attendeeContracts.forEach(ac -> {
            if (ac.getValidFrom().isBefore(selectedAttendanceDate.plusDays(1))
                    && ac.getValidTo().isAfter(selectedAttendanceDate.minusDays(1))
                    && ac.getType() != ContractType.INVALID) {
                contract = ac;
            }
        });

        displayByGroupWorkouts = contract.getType() == ContractType.ACTIVE;
        contractActive = displayByGroupWorkouts;
        sortedAttendance.clear();
        formatAttendanceDate();
    }

    
    public String formatAgeGroup() {
        if (selectedAttendee.getGroup() != null) {
            return selectedAttendee.getGroup().getAgeGroup().getValue();
        }
        return "";
    }

    
    public List<String> formatNamesOfCoaches() {
        namesOfCoaches = new ArrayList<>();
        if (selectedAttendee.getGroup() != null) {
            List<Employee> coachesList = employeeRepository.findByGroupId(selectedAttendee.getGroup().getId());
            for (Employee coach : coachesList) {
                String name = coach.getName() + " " + coach.getSurname();
                namesOfCoaches.add(name);
            }
        }
        return namesOfCoaches;
    }

    
    public List<String> formatTimeOfGroup() {
        timesOfGroup = new ArrayList<>();
        List<TrackWorkingHour> workingHoursList = new ArrayList<>(
                cabinetWorkingHourRepository.findByGroup((selectedAttendee).getGroup()));

        for (TrackWorkingHour trackWorkingHour : workingHoursList) {
            String time = weekDays.get(trackWorkingHour.getTrackWeekday().getDayOfWeek().ordinal()) + " ("
                    + TimeUtil.minutesToTimeConverter(trackWorkingHour.getStartHour()) + " -  "
                    + TimeUtil.minutesToTimeConverter(trackWorkingHour.getEndHour()) + ")";
            timesOfGroup.add(time);
        }
        return timesOfGroup;

    }

    
    public void displayCalendarWithOnlyMonth() {
        attendanceListForMonthlyCalendar = new ArrayList<>();

        attendeeContracts.forEach(ac -> {

            if (ac.getValidFrom().plusMonths(1).isAfter(ac.getValidTo())) {
                selectedAttendanceDate = ac.getValidTo();
            } else {
                selectedAttendanceDate = ac.getValidFrom().plusMonths(1);
            }
            if (ac.getValidFrom().isBefore(ac.getValidTo())) {
                checkIfContractIsActive();
                if (displayByGroupWorkouts) {
                    if (selectedAttendee.getWorkoutStartDate().isAfter(ac.getValidTo())) {

                        startDate = ac.getValidFrom();
                        endDate = ac.getValidTo();
                    } else {

                        startDate = (selectedAttendee).getWorkoutStartDate();
                        endDate = (selectedAttendee).getGroup().getEndDate();
                    }
                } else {

                    startDate = ac.getValidFrom();
                    endDate = ac.getValidTo();
                }
                if (!attendanceListForMonthlyCalendar.contains(startDate.withDayOfMonth(1))) {
                    attendanceListForMonthlyCalendar.add(startDate);
                }
                LocalDate month = startDate.plusMonths(1);
                while (month.isBefore(endDate.with(TemporalAdjusters.lastDayOfMonth()))) {
                    if (!attendanceListForMonthlyCalendar.contains(month.withDayOfMonth(1))) {
                        attendanceListForMonthlyCalendar.add(month.withDayOfMonth(1));
                    }
                    month = month.plusMonths(1);
                }
            }
        });
        selectedAttendanceDate = null;
    }

    
    public void formatAttendanceDate() {
        attendance.clear();

        attendeeContracts = contractRepository.findByAttendeeAndTypeNot(selectedAttendee, ContractType.INVALID);
        groupChangedContract = attendeeContracts.get(attendeeContracts.size() - 1).equals(contract)
                && contract.getAttendee().getNewGroup() != null;

        if (selectedAttendanceDate == null) {
            selectedAttendanceDate = attendanceListForMonthlyCalendar.get(0);
            attendanceListForMonthlyCalendar.forEach(alfmc -> {
                if (selectedAttendanceDate.isBefore(LocalDate.now().withDayOfMonth(1).minusDays(1))) {
                    selectedAttendanceDate = alfmc;
                }
            });
        }

        Collections.sort(attendanceListForMonthlyCalendar, Collections.reverseOrder());
        contractActive = displayByGroupWorkouts;
        if (contractActive) {

            formAttendanceByActiveGroup();
        } else {
            if (groupChangedContract) {

                formAttendanceByActiveGroup();
                contractActive = true;
            } else {

                List<Attendance> attendanceByContract = attendanceRepository.findByAttendeeAndDateBetween(
                        selectedAttendee.getId(), contract.getValidFrom(), contract.getValidTo());
                for (Attendance singleAttendance : attendanceByContract) {
                    if (singleAttendance.getWorkoutDate().getMonth() == selectedAttendanceDate.getMonth()
                            && singleAttendance.getWorkoutDate().getYear() == selectedAttendanceDate.getYear()) {
                        attendance.add(singleAttendance);
                    }
                }
            }
        }
    }

    
    public Boolean showAttendance(GroupWorkout groupWorkout) {
        present = null;
        secondLessonEnd++;
        boolean hasTwoWorkoutsSameDay = false;
        if (groupWorkoutsListOfMonth.size() >1) {
            hasTwoWorkoutsSameDay = groupWorkoutsListOfMonth.get(0).getDate().equals(
                    groupWorkoutsListOfMonth.get(1).getDate());
        }

        if ((selectedAttendee).getAttendance() != null) {
            if (hasTwoWorkoutsSameDay) {
                if (sortedAttendance.isEmpty()) {
                    sortedAttendance = attendanceRepository.findByAttendeeAndDateBetween(selectedAttendee.getId(),
                            groupWorkout.getDate().withDayOfMonth(1),
                            groupWorkout.getDate().with(TemporalAdjusters.lastDayOfMonth()));
                }
                for (Attendance monthAttendance : sortedAttendance) {
                    if (firstLesson) {
                        if (monthAttendance.getWorkoutDate().equals(groupWorkout.getDate())) {

                            present = monthAttendance.getIsPresent();
                            firstLesson = false;
                            break;
                        }
                    } else if (monthAttendance.getWorkoutDate().equals(groupWorkout.getDate())) {
                        firstLesson = true;
                    }
                }
            } else {

                for (Attendance monthAttendance : (selectedAttendee).getAttendance()) {
                    if (monthAttendance.getWorkoutDate().equals(groupWorkout.getDate())) {
                        present = monthAttendance.getIsPresent();
                    }
                }
            }
        }

        if (secondLessonEnd == 2) {
            secondLessonEnd = 0;
            firstLesson = true;
        }
        return present;

    }

    
    public void updateHealthProblems(Attendee attendee) {
        FacesContext context = FacesContext.getCurrentInstance();
        attendeeRepository.save(attendee);
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "edit.success", context);
        primeFacesWrapper.current().executeScript("$('.editHealthProblemsBtn').show();");
    }


    
    public void delete(MedicalCertificate selectedMedicalCertificate) {
        amazonClient.deletFile(selectedMedicalCertificate.getDocumentUrl());
        medicalCertificateRepository.deleteById(selectedMedicalCertificate.getId());
        sortAndUpdateMedicalCertificates();
    }

    
    public LocalDate localDateConverter(MedicalCertificate medicalCertificate) {
        return DateUtil.convertLocalDateTime(medicalCertificate.getCreatedAt());
    }

    
    public boolean checkIfDisable() {
        MedicalCertificate certificates = medicalCertificateRepository.
                findFirstByAttendeeAndStatusNotAndStatusNotOrderByIdDesc(
                selectedAttendee, MedicalCertificateStatus.ABSENT, MedicalCertificateStatus.EXPIRED);

        if (certificates != null) {
                LocalDate date = certificates.getValidTo();
                if (date != null) {
                    return certificates.getValidTo().isAfter(LocalDate.now().plusMonths(1))
                            || certificates.getStatus().equals(MedicalCertificateStatus.PENDING);
                } else {
                    return true;
                }
        } else {
            return false;
        }
    }

    
    public void formAttendanceByActiveGroup() {
        groupWorkoutsListOfMonth = new ArrayList<>();
        List<GroupWorkout> groupWorkouts = groupWorkoutRepository
                .findGroupWorkoutsByGroupId((selectedAttendee).getGroup());

        for (GroupWorkout groupWorkout : groupWorkouts) {
            if (groupWorkout.getDate().getMonth() == selectedAttendanceDate.getMonth()
                    && groupWorkout.getDate().getYear() == selectedAttendanceDate.getYear()) {
                groupWorkoutsListOfMonth.add(groupWorkout);
            }
        }
    }

    
    public String formUnsignedContractString() {
        Set<Attendee> setOfAttendees = client.getAttendees();
        List<Contract> notSignedContracts = contractRepository.findAllByAttendeeInAndTypeEqualsAndIsSignedIsFalse(
                setOfAttendees, ContractType.NOT_ACTIVE);

        if (!notSignedContracts.isEmpty()) {
            StringBuilder fullErrorMessage = new StringBuilder();
            StringBuilder allUnsignedContractsAsString = new StringBuilder();
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

            for (int i = 0; i < notSignedContracts.size(); i++) {
                if (i < notSignedContracts.size() - 1) {
                    allUnsignedContractsAsString.append(notSignedContracts.get(i).getNumber()).append(", ");
                } else {
                    allUnsignedContractsAsString.append(notSignedContracts.get(i).getNumber());
                }
            }

            if (notSignedContracts.size() == 1) {
                String msgLabel = msg.getString("client.contract.not.signed");
                fullErrorMessage.append(MessageFormat.format(msgLabel, allUnsignedContractsAsString));
            } else {
                String msgLabel = msg.getString("client.contracts.not.signed");
                fullErrorMessage.append(MessageFormat.format(msgLabel, allUnsignedContractsAsString));
            }
            return fullErrorMessage.toString();
        } else {
            return null;
        }
    }

    
    private void sortAndUpdateMedicalCertificates() {
        medicalCertificates = medicalCertificateRepository.findByAttendeeOrderByIdDesc(selectedAttendee);
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

    
    public int getAttendeeId() {
        return attendeeId;
    }

    
    public void setAttendeeId(int attendeeId) {
        this.attendeeId = attendeeId;
    }

    
    public Attendee getSelectedAttendee() {
        return selectedAttendee;
    }

    
    public void setSelectedAttendee(Attendee selectedAttendee) {
        this.selectedAttendee = selectedAttendee;
    }

    
    public boolean isSingleAttendee() {
        return singleAttendee;
    }

    
    public void setSingleAttendee(boolean singleAttendee) {
        this.singleAttendee = singleAttendee;
    }

    
    public String getContent() {
        return content;
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getSurname() {
        return surname;
    }

    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPhone() {
        return phone;
    }

    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    public String getEnteredCode() {
        return enteredCode;
    }

    
    public void setEnteredCode(String enteredCode) {
        this.enteredCode = enteredCode;
    }

    
    public boolean isMatchedCodes() {
        return matchedCodes;
    }

    
    public void setMatchedCodes(boolean matchedCodes) {
        this.matchedCodes = matchedCodes;
    }

    
    public String getOldEmail() {
        return oldEmail;
    }

    
    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    
    public boolean isDisabledButton() {
        return disabledButton;
    }

    
    public void setDisabledButton(boolean disabledButton) {
        this.disabledButton = disabledButton;
    }

    
    public String getAgeGroupString() {
        return ageGroupString;
    }

    
    public void setAgeGroupString(String ageGroupString) {
        this.ageGroupString = ageGroupString;
    }

    
    public List<String> getNamesOfCoaches() {
        return namesOfCoaches;
    }

    
    public void setNamesOfCoaches(List<String> namesOfCoaches) {
        this.namesOfCoaches = namesOfCoaches;
    }

    
    public List<String> getTimesOfGroup() {
        return timesOfGroup;
    }

    
    public void setTimesOfGroup(List<String> timesOfGroup) {
        this.timesOfGroup = timesOfGroup;
    }

    
    public List<Attendance> getAttendanceListOfMonth() {
        return attendanceListOfMonth;
    }

    
    public void setAttendanceListOfMonth(List<Attendance> attendanceListOfMonth) {
        this.attendanceListOfMonth = attendanceListOfMonth;
    }

    
    public List<String> getWeekDays() {
        return weekDays;
    }

    
    public void setWeekDays(List<String> weekDays) {
        this.weekDays = weekDays;
    }

    
    public List<LocalDate> getAttendanceListForMonthlyCalendar() {
        return attendanceListForMonthlyCalendar;
    }

    
    public void setAttendanceListForMonthlyCalendar(List<LocalDate> attendanceListForMonthlyCalendar) {
        this.attendanceListForMonthlyCalendar = attendanceListForMonthlyCalendar;
    }

    
    public LocalDate getSelectedAttendanceDate() {
        return selectedAttendanceDate;
    }

    
    public void setSelectedAttendanceDate(LocalDate selectedAttendanceDate) {
        this.selectedAttendanceDate = selectedAttendanceDate;
    }

    
    public List<GroupWorkout> getGroupWorkoutsListOfMonth() {
        return groupWorkoutsListOfMonth;
    }

    
    public void setGroupWorkoutsListOfMonth(List<GroupWorkout> groupWorkoutsListOfMonth) {
        this.groupWorkoutsListOfMonth = groupWorkoutsListOfMonth;
    }

    
    public Boolean getPresent() {
        return present;
    }

    
    public void setPresent(Boolean present) {
        this.present = present;
    }

    
    public Set<Attendee> getRegisteredAttendees() {
        return registeredAttendees;
    }

    
    public void setRegisteredAttendees(Set<Attendee> registeredAttendees) {
        this.registeredAttendees = registeredAttendees;
    }

    
    public List<Contract> getAttendeeContracts() {
        return attendeeContracts;
    }

    
    public void setAttendeeContracts(List<Contract> attendeeContracts) {
        this.attendeeContracts = attendeeContracts;
    }

    
    public boolean isContractActive() {
        return contractActive;
    }

    
    public void setContractActive(boolean contractActive) {
        this.contractActive = contractActive;
    }

    
    public Set<Attendance> getAttendance() {
        return attendance;
    }

    
    public void setAttendance(Set<Attendance> attendance) {
        this.attendance = attendance;
    }

    
    public List<MedicalCertificate> getMedicalCertificates() {
        return medicalCertificates;
    }

    
    public void setMedicalCertificates(List<MedicalCertificate> medicalCertificates) {
        this.medicalCertificates = medicalCertificates;
    }

    
    public boolean isGroupChangedContract() {
        return groupChangedContract;
    }

    
    public void setGroupChangedContract(boolean groupChangedContract) {
        this.groupChangedContract = groupChangedContract;
    }

}
