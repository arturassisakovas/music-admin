package com.mAdmin.controller;

import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.ProgressLevel;
import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.repository.AttendeeInSessionRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeInSession;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Mail;
import com.mAdmin.entity.Subscription;
import com.mAdmin.model.AttendeeCreationModel;
import com.mAdmin.model.AttendeeRegistrationSessionModel;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.service.AttendeeService;
import com.mAdmin.service.ClientService;
import com.mAdmin.service.EmailService;
import com.mAdmin.service.SubscriptionService;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Controller
@Scope(value = "session")
public class ClientRegistrationController {

    
    @Value("${registration.maxattendees}")
    private int maxAttendees;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private RegisterController registerController;

    
    @Autowired
    private AuthenticationManager authManager;

    
    @Autowired
    private AttendeeInSessionRepository attendeeInSessionRepository;

    
    @Autowired
    private SubscriptionService subscriptionService;

    
    @Autowired
    private ClientService clientService;

    
    @Autowired
    private AttendeeService attendeeService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private RegistrationController registrationController;

    
    private String clientPassword;

    
    private Client client;

    
    private Subscription subscription;

    
    private String emailSubject;

    
    private String emailText;

    
    private boolean matchedCodes;

    
    private boolean disabledButton;

    
    private String initialEmail;

    
    private String enteredCode;

    
    private Map<String, Object> sessionMap;

    
    private List<String> availableCities;

    
    private Attendee createdAttendee;

    
    private String attendeeSavedToReserveSubject;

    
    private final List<Subscription> subscriptions = new ArrayList<>();

    
    private int index;

    
    private Attendee attendee;

    
    private LocalDate maxDate;

    
    private static final int EIGHTEEN = 18;

    
    @PostConstruct
    public void init() {

        client = new Client();
        attendee = new Attendee();
        subscription = new Subscription();
        client.setForeigner(false);
        matchedCodes = false;
        disabledButton = false;
        maxDate =  LocalDate.now().minusYears(EIGHTEEN);
        setAvailableCities(poolRepository.findDistinctCity());

    }

    
    public void onLoadExisting() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
                .getRequest();
        boolean isClient = request.isUserInRole("CLIENT");
        if (isClient) {
            client = ((ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal()).getClient();
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            boolean existingAttendee = ((RegisterController) session.get("registerController")).isExistingAttendee();
            if (session.get("selectedAttendee") == null && !existingAttendee) {
                attendee = new Attendee();
                session.put("selectedAttendee", attendee);
            }
        }
    }

    
    @Transactional
    public void save() {

        boolean reserved = registerController.isNoGroupAvailable();
        if (reserved) {
            reserveClient();
        } else {

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            sessionMap = context.getSessionMap();

            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
                    .getSession(false);
            String sessionId = session.getId();
            AttendeeInSession potentialAttendee = attendeeInSessionRepository.findByReservedClientSessionId(sessionId);


            int workoutTimesPerWeek = Faces.getSessionAttribute("workoutTimesPerWeek");
            WorkoutsPerWeek workoutsPerWeek = WorkoutsPerWeek.findByTimes(workoutTimesPerWeek);
            int subscriptionMonths = Integer.parseInt(Faces.getSessionAttribute("subscriptionMonths"));
            Map<String, LocalDate> listOfDates = prepDateListFromSession();

            BigDecimal price = Faces.getSessionAttribute("selectedPrice");

            if (client.getId() == null) {
                client = clientService.create(client, clientPassword, ClientStatus.INACTIVE);

                AttendeeCreationModel attendeeCreationModel = new AttendeeCreationModel(
                        Faces.getSessionAttribute("birthdate"), Faces.getSessionAttribute("firstDay"),
                        Faces.getSessionAttribute("city"), Faces.getSessionAttribute("selectedGroupId"),
                        Faces.getSessionAttribute("attendeeSwimLevel"));
                attendee = attendeeService.create(attendee, attendeeCreationModel, client);

                subscription = subscriptionService.create(subscriptionMonths, workoutsPerWeek, price,
                        listOfDates.get("subStart"), listOfDates.get("subEnd"), attendee);

                sessionMap.put("newClient", client);

                attendeeInSessionRepository.deleteById(potentialAttendee.getId());
            } else {

                subscription = subscriptionService.update(subscription, subscriptionMonths, workoutsPerWeek, price,
                        listOfDates.get("subStart"), listOfDates.get("subEnd"));
                client = clientService.create(client, clientPassword, ClientStatus.INACTIVE);

                AttendeeCreationModel attendeeCreationModel = new AttendeeCreationModel(
                        Faces.getSessionAttribute("birthdate"), Faces.getSessionAttribute("firstDay"),
                        Faces.getSessionAttribute("city"), Faces.getSessionAttribute("selectedGroupId"),
                        Faces.getSessionAttribute("attendeeSwimLevel"));
                attendee = attendeeService.create(attendee, attendeeCreationModel, client);

                sessionMap.put("newClient", client);
                attendeeInSessionRepository.deleteById(potentialAttendee.getId());
            }
            subscriptions.add(subscription);
            sessionMap.put("theSubscription", subscriptions);

            checkCodesMatch();
        }
    }

    
    public void sendConfirmation() {
        client.setConfirmationToken(UUID.randomUUID().toString());
        client.setConfirmationCode(clientService.generateCode());
        initialEmail = client.getEmail();
        checkCodesMatch();
    }

    
    public void reserveClient() {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();


        LocalDate birthDate = Faces.getSessionAttribute("birthdate");
        SwimmingLevel swimmingLevel = Faces.getSessionAttribute("attendeeSwimLevel");
        String city = registerController.getCity();

        client = clientService.createReserveClient(client, clientPassword);
        attendee.setBirthDate(birthDate);
        attendee.setClient(client);
        attendee.setCity(city);
        attendee.setProgressLevel(ProgressLevel.ZERO);
        Attendee reservedAttendee = new Attendee();

        reservedAttendee.setBirthDate(birthDate);
        reservedAttendee.setClient(client);
        reservedAttendee.setCity(city);
        reservedAttendee.setName(attendee.getName());
        reservedAttendee.setSurname(attendee.getSurname());
        reservedAttendee.setGender(attendee.getGender());
        reservedAttendee.setSwimmingLevel(swimmingLevel);
        reservedAttendee.setActive(false);
        reservedAttendee.setHealthProblems(attendee.getHealthProblems());
        reservedAttendee.setProgressLevel(attendee.getProgressLevel());

        attendeeRepository.save(reservedAttendee);

        attendeeService.sendReservedAttendeeEmail(client);

        checkCodesMatch();
    }

    
    public void checkCodesMatch() {

        if (!matchedCodes) {
            emailService.formAndSendEmailToClientAboutConfirmCode(client);

            setEnteredCode(null);

            if (!Objects.equals(initialEmail, client.getEmail())) {
                setDisabledButton(false);
            }
        }

    }

    
    public void redirectToLoginPage() throws IOException {

        FacesContext context = FacesContext.getCurrentInstance();
        messageBundleComponent.generateMessage(
                FacesMessage.SEVERITY_INFO, "registration.reserve.completed", context);
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.getExternalContext().redirect("/login");
    }

    
    public String saveAndRedirect() {
        save();

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", RegisterController.FOURTH_STEP);
        return "/registration/step-4?faces-redirect=true";
    }

    
    public void compareCodes() throws IOException {

        FacesContext context = FacesContext.getCurrentInstance();

        if (getEnteredCode().equals(client.getConfirmationCode())) {
            setMatchedCodes(true);
            initialEmail = client.getEmail();
            save();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            createdAttendee = attendee;
            updateRegisteredAttendees();
            UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(client.getEmail(),
                    clientPassword);
            Authentication auth = authManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);

            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

            if (registerController.isNoGroupAvailable()) {
                session.put("currentRegistrationStep", RegisterController.FIRST_STEP);
                ec.redirect("/registration/step-1");
            } else {
                session.put("currentRegistrationStep", RegisterController.FIFTH_STEP);
                ec.redirect("/registration/step-5");
            }

        } else {
            setMatchedCodes(false);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "client.notMatchedCode", context);
        }
    }

    
    public void resendEmail() {
        if (!disabledButton) {
            Mail mail = emailService.formEmailConfirmationMail(client, emailText);
            emailService.sendEmailByModel(mail, "mail-templates/confirmation-template.html");
            setDisabledButton(true);
        }

    }

    
    public String addAnotherAttendee() {
        saveOrUpdateAttendee();
        updateRegisteredAttendees();
        attendee = new Attendee();
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", 1);
        return "/registration/step-1?faces-redirect=true";
    }

    
    public void updateRegisteredAttendees() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        boolean step2Check = Faces.getSessionAttribute("step2Check");
        if (step2Check) {
            Attendee selectedAttendee = Faces.getSessionAttribute("selectedAttendee");
            List<?> registeredAttendeesTemp = Faces.getSessionAttribute("registeredAttendees");

            if (selectedAttendee != null || createdAttendee != null) {

                List<Attendee> registeredAttendees = new ArrayList<>();

                if (registeredAttendeesTemp != null) {
                    for (Object attendee : registeredAttendeesTemp) {
                        if (attendee != null) {
                            registeredAttendees.add((Attendee) attendee);
                        }
                    }
                }

                if (createdAttendee != null) {
                    selectedAttendee = createdAttendee;
                    session.put("attendeeWrittenToDb", true);
                    createdAttendee = null;
                }

                if (!registeredAttendees.contains(selectedAttendee)) {
                    registeredAttendees.add(selectedAttendee);
                }

                session.put("registeredAttendees", registeredAttendees);
            }
        }
    }

    
    public void saveOrUpdateAttendee() {

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        Map<String, Object> sessionMap = context.getSessionMap();


        attendee = Faces.getSessionAttribute("selectedAttendee");

        List<?> registeredAndPaidAttendeesTemp = Faces.getSessionAttribute("registeredAndPaidAttendees");
        List<Attendee> registeredAndPaidAttendees = new ArrayList<>();
        if (registeredAndPaidAttendeesTemp != null) {
            registeredAndPaidAttendeesTemp.forEach(a -> registeredAndPaidAttendees.add((Attendee) a));
        }
        if (attendee != null && (registeredAndPaidAttendees.contains(attendee) || attendee.getGender() == null)) {
            sessionMap.put("selectedAttendee", null);
            attendee = null;
        }

        if (attendee == null) {
            return;
        }

        boolean step2Check = Faces.getSessionAttribute("step2Check");

        if (step2Check) {

            attendee.setBirthDate(Faces.getSessionAttribute("birthdate"));
            attendee.setCity(Faces.getSessionAttribute("city"));
            Group group = groupRepository.getOne(Faces.getSessionAttribute("selectedGroupId"));
            Map<String, LocalDate> listOfDates = prepDateListFromSession();
            Optional<Subscription> possibleSubscription = subscriptions.stream()
                    .filter(s -> s.getAttendee().getId().equals(attendee.getId())).findFirst();

            Attendee checkAttendee = attendee;

            if (checkAttendee.getGroup() != null
                    && !possibleSubscription.isPresent()) {
                attendee.setNewGroup(group);
            } else {
                attendee.setGroup(group);
            }

            attendee.setClient(client);

            if (attendee.getProgressLevel() == null) {
                attendee.setProgressLevel(ProgressLevel.ZERO);
            }

            attendee.setSwimmingLevel(Faces.getSessionAttribute("attendeeSwimLevel"));


            attendee.setWorkoutStartDate(listOfDates.get("firstDay"));

            attendee = attendeeRepository.save(attendee);
            removeReservationSpotInGroup();

            if (!possibleSubscription.isPresent()) {
                Subscription subscription = new Subscription(
                        Integer.parseInt(Faces.getSessionAttribute("subscriptionMonths")),
                        listOfDates.get("subStart"), listOfDates.get("subEnd"));
                WorkoutsPerWeek workoutsPerWeek = WorkoutsPerWeek
                        .findByTimes(Faces.getSessionAttribute("workoutTimesPerWeek"));

                subscription.setWorkoutsPerWeek(workoutsPerWeek);
                subscription.setPrice(Faces.getSessionAttribute("selectedPrice"));
                subscription.setAttendee(attendee);
                subscription = subscriptionRepository.save(subscription);
                subscriptions.add(subscription);
            }
            sessionMap.put("theSubscription", subscriptions);
            List<?> attendeesTemp = Faces.getSessionAttribute("registeredAttendees");
            List<Attendee> attendees = formAttendeesFromSession(attendeesTemp);

            if (!attendee.getActive()) {
                attendee.setActive(true);
                attendeeRepository.save(attendee);

                attendeesTemp.remove(attendee);

                sessionMap.put("registeredAttendees", attendeesTemp);
            }

            if (!attendees.contains(attendee)) {

                attendees.add(attendee);
                attendees.removeIf(Objects::isNull);

                sessionMap.put("registeredAttendees", attendees);
            }

            sessionMap.put("selectedAttendee", attendee);
        }
    }

    
    public void removeReservationSpotInGroup() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String sessionId = session.getId();

        AttendeeInSession potentialAttendee = attendeeInSessionRepository.findByReservedClientSessionId(sessionId);

        if (potentialAttendee != null) {
            attendeeInSessionRepository.deleteById(potentialAttendee.getId());
        }
    }

    
    public String continueWithExisting() {
        saveOrUpdateAttendee();

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        registrationController.setAgreeWithTermsOfService(false);
        session.put("attendeeWrittenToDb", true);
        session.put("currentRegistrationStep", RegisterController.FIFTH_STEP);
        return "/registration/step-5?faces-redirect=true";
    }

    
    public void removeRegisteredAttendee(Attendee attendee) throws IOException {
        sessionMap = getSessionMap();
        List<?> attendeesTemp = Faces.getSessionAttribute("registeredAttendees");

        List<Attendee> attendees = formAttendeesFromSession(attendeesTemp);

        removeReservationSpotInGroup();
        sessionMap.put("registeredAttendees", attendees);
        AttendeeRegistrationSessionModel attendeeRegistrationSessionModel = new AttendeeRegistrationSessionModel();

        if (attendee != null) {
            if ((attendee.getNewGroup() == null)
                    && (contractRepository.findByAttendeeAndType(attendee, ContractType.ACTIVE) == null)) {
                Subscription subscription = subscriptionRepository.findByAttendeeAndInvoiceIsNull(attendee);

                if (attendeeRegistrationSessionModel.getSubscriptions() != null && !attendeeRegistrationSessionModel.
                        getSubscriptions().isEmpty() && subscription != null) {

                index = attendeeRegistrationSessionModel.getSubscriptions().indexOf(subscription);

                } else if (attendeeRegistrationSessionModel.getSubscriptions() != null && attendees.size()
                        != attendeeRegistrationSessionModel.getSubscriptions().size()) {
                    index = attendees.indexOf(attendee);
                }

                attendeeRegistrationSessionModel.getPeriods().remove(index);
                attendeeRegistrationSessionModel.getFirstDays().remove(index);

                if (subscription != null) {
                    attendeeRegistrationSessionModel.getSubscriptions().remove(subscription);
                    subscription.setAttendee(null);
                    subscriptionRepository.save(subscription);
                    subscriptionRepository.delete(subscription);
                }

                attendees.remove(attendee);
                attendeeService.convertAttendeeToReservedAttendee(attendee);
                sessionMap.put("selectedAttendee", null);
                sessionMap.put("registeredAttendees", attendees);
            } else {
                Subscription subscription = subscriptionRepository.findByAttendeeAndInvoiceIsNull(attendee);
                attendees.forEach(a -> {
                    if (a == null) {
                        index = attendees.indexOf(null);
                    }
                });

                if (subscription != null) {
                    index = attendeeRegistrationSessionModel.getSubscriptions().indexOf(subscription);
                    attendeeRegistrationSessionModel.getSubscriptions().remove(subscription);
                    subscription.setAttendee(null);
                    subscriptionRepository.save(subscription);
                    subscriptionRepository.delete(subscription);
                }

                attendeeRegistrationSessionModel.getPeriods().remove(index);
                attendeeRegistrationSessionModel.getFirstDays().remove(index);
                attendees.remove(index);

                attendees.remove(attendee);
                sessionMap.put("registeredAttendees", attendees);
                attendee.setNewGroup(null);
                attendeeRepository.save(attendee);

            }
        } else {
            attendees.forEach(a -> {
                if (a == null) {
                    index = attendees.indexOf(null);
                }
            });
            attendeeRegistrationSessionModel.getPeriods().remove(index);
            attendeeRegistrationSessionModel.getFirstDays().remove(index);
            attendees.remove(index);
            sessionMap.put("registeredAttendees", attendees);
        }

        sessionMap.put("periods", attendeeRegistrationSessionModel.getPeriods());
        sessionMap.put("firstWorkoutDays", attendeeRegistrationSessionModel.getFirstDays());
        sessionMap.put("theSubscription", attendeeRegistrationSessionModel.getSubscriptions());

        if (attendees.isEmpty()) {
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            session.put("currentRegistrationStep", 1);
            FacesContext.getCurrentInstance().getExternalContext().redirect("/registration/step-1");
        }

    }

    
    public List<Attendee> collectRegisteredAttendees() {
        sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<?> attendeesTemp = Faces.getSessionAttribute("registeredAttendees");

        List<Attendee> attendees = new ArrayList<>();

        if (attendeesTemp != null) {
            attendeesTemp.forEach(a -> {
                if (a == null) {
                    attendees.add(0, null);
                } else {
                    attendees.add((Attendee) a);
                }
            });
        }
        return attendees;
    }

    
    public Map<String, Object> getSessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    
    public String getAttendeeName(Attendee attendee) {

        Map<String, Object> session = getSessionMap();
        Attendee selectedAttendee = (Attendee) session.get("selectedAttendee");

        if (attendee != null && attendee.getId() != null) {

            return attendee.getName();

        } else if (selectedAttendee != null && selectedAttendee.getId() != null) {

            return selectedAttendee.getName();

        }

        return null;

    }

    
    private Map<String, LocalDate> prepDateListFromSession() {
        LocalDate firstDay = Faces.getSessionAttribute("firstDay");
        LocalDate lastDay = Faces.getSessionAttribute("lastDay");
        LocalDate subStart = firstDay.withDayOfMonth(1);
        LocalDate subEnd = lastDay.withDayOfMonth(lastDay.lengthOfMonth());
        Map<String, LocalDate> listOfDates = new HashMap<>();
        listOfDates.put("firstDay", firstDay);
        listOfDates.put("lastDay", lastDay);
        listOfDates.put("subStart", subStart);
        listOfDates.put("subEnd", subEnd);
        return listOfDates;

    }

    
    private List<Attendee> formAttendeesFromSession(List<?> attendeesTemp) {
        List<Attendee> attendees = new ArrayList<>();

        if (attendeesTemp != null) {
            attendeesTemp.forEach(a -> attendees.add((Attendee) a));
        }

        return attendees;
    }

    
    public String getClientPassword() {
        return clientPassword;
    }

    
    public void setClientPassword(String clientPassword) {
        this.clientPassword = clientPassword;
    }

    
    public boolean isMatchedCodes() {
        return matchedCodes;
    }

    
    public void setMatchedCodes(boolean matchedCodes) {
        this.matchedCodes = matchedCodes;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public boolean isDisabledButton() {
        return disabledButton;
    }

    
    public void setDisabledButton(boolean disabledButton) {
        this.disabledButton = disabledButton;
    }

    
    public String getInitialEmail() {
        return initialEmail;
    }

    
    public void setInitialEmail(String initialEmail) {
        this.initialEmail = initialEmail;
    }

    
    public String getEmailSubject() {
        return emailSubject;
    }

    
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    
    public String getEmailText() {
        return emailText;
    }

    
    public void setEmailText(String emailText) {
        this.emailText = emailText;
    }

    
    public String getEnteredCode() {
        return enteredCode;
    }

    
    public void setEnteredCode(String enteredCode) {
        this.enteredCode = enteredCode;
    }

    
    public List<String> getAvailableCities() {
        return availableCities;
    }

    
    public void setAvailableCities(List<String> availableCities) {
        this.availableCities = availableCities;
    }

    
    public int getMaxAttendees() {
        return maxAttendees;
    }

    
    public Attendee getAttendee() {
        return attendee;
    }

    
    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    
    public LocalDate getMaxDate() {
        return maxDate;
    }

    
    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }
}
