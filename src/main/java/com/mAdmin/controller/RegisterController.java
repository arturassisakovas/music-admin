package com.mAdmin.controller;

import com.mAdmin.enumerator.AgeGroup;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.repository.AttendeeNonPaidRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.repository.InvoiceRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeNonPaid;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Subscription;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.service.AttendeeInSessionService;
import com.mAdmin.service.AttendeeService;
import com.mAdmin.service.DiscountService;
import com.mAdmin.service.GroupService;
import com.mAdmin.service.GroupWorkoutService;
import com.mAdmin.service.PoolService;
import com.mAdmin.service.SubscriptionService;
import com.mAdmin.service.TrackWorkingHourService;
import com.mAdmin.util.DateUtil;
import com.mAdmin.util.PrimeFacesWrapper;
import com.mAdmin.util.StreamUtil;
import com.mAdmin.util.TimeUtil;
import com.mAdmin.view.HoursListElement;
import org.omnifaces.util.Faces;
import com.mAdmin.view.PeriodDatesModel;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Controller
@Scope(value = "session")
public class RegisterController {

    
    @Autowired
    private GroupService groupService;

    
    @Autowired
    private DiscountService discountService;

    
    @Autowired
    private PoolService poolService;

    
    @Autowired
    private TrackWorkingHourService trackWorkingHourService;

    
    @Autowired
    private AttendeeService attendeeService;

    
    @Autowired
    private GroupWorkoutService groupWorkoutService;

    
    @Autowired
    private AttendeeInSessionService attendeeInSessionService;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private SubscriptionService subscriptionService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private AttendeeNonPaidRepository attendeeNonPaidRepository;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private InvoiceRepository invoiceRepository;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private List<SelectItem> years, months, days;

    
    private List<String> allCities = new ArrayList<>();

    
    private List<Group> groups;

    
    private List<Pool> filteredPools;

    
    private List<String> cities;

    
    private List<WorkoutsPerWeek> workoutTimes;

    
    private List<HoursListElement> availableWorkingHours;

    
    private List<String> weekDays = new ArrayList<>();

    
    private List<Integer> subscriptionsList = new ArrayList<>();

    
    private List<Group> groupsByCity = new ArrayList<>();

    
    private List<Group> groupsByWorkoutTimes = new ArrayList<>();

    
    private List<Group> groupsByPool = new ArrayList<>();

    
    private List<Long> groupsIdList = new ArrayList<>();

    
    private List<Attendee> attendees;

    
    private List<String> availableCities;

    
    private List<Period> periods;

    
    private final List<Period> contractPeriods = new ArrayList<>();

    
    private final List<LocalDate> firstWorkoutDays = new ArrayList<>();

    
    @Value("${registration.school.subscription.months}")
    private int[] schoolSubscriptionsArray;

    
    @Value("${registration.summer.subscription.months}")
    private int[] summerSubscriptionsArray;

    
    @Value("${registration.maxbirthdateyears}")
    private int maxBirthdayYears;

    
    @Value("${when.next.period.available}")
    private int whenNextIsAvailable;

    
    private Map<String, Object> sessionMap;

    
    private Map<Integer, Integer> subscriptionsMap = new HashMap<>();

    
    private FacesContext context;

    
    private String[] monthsNamesArray;

    
    private LocalDate birthday;

    
    private String year, month, day;

    
    private String birthdayAsString;

    
    private SwimmingLevel swimLevel;

    
    private int workoutsPerWeek;

    
    private Long poolId;

    
    private Long selectedGroupId;

    
    private String city;

    
    private String subscription;

    
    private HoursListElement selectedHour;

    
    private LocalDate firstDay;

    
    private LocalDate lastDay;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private AgeGroup ageGroup;

    
    private boolean tooYoung;

    
    private boolean noGroupAvailable;

    
    private final Calendar now = Calendar.getInstance();

    
    private int currentYear = now.get(Calendar.YEAR);

    
    private boolean existingAttendee = true;

    
    private BigDecimal singleWorkoutPrice;

    
    private boolean attendeeWithGroup;

    
    private boolean stateOfPeriods;

    
    private Period period;

    
    private boolean periodsAreNull = false;

    
    private String healthProblems;

    
    private Attendee selectedAttendee;

    
    private Attendee reservedAttendee;

    
    public static final int FIRST_STEP = 1;

    
    public static final int SECOND_STEP = 2;

    
    public static final int THIRD_STEP = 3;

    
    public static final int FOURTH_STEP = 4;

    
    public static final int FIFTH_STEP = 5;

    
    public static final int SIXTH_STEP = 6;

    
    private boolean attendeeHasGroup;

    
    private List<PeriodDatesModel> periodDatesModels = new ArrayList<>();

    
    private static final int TWELVE = 12;

    
    @PostConstruct
    public void init() {

        int i;
        years = new ArrayList<>();
        for (i = 0; i < maxBirthdayYears; i++) {
            years.add(new SelectItem("" + currentYear, "" + currentYear));
            currentYear--;
        }
        reservedAttendee = new Attendee();
        tooYoung = false;
        noGroupAvailable = false;
        workoutsPerWeek = 0;
        selectedGroupId = null;
        periodDatesModels = new ArrayList<>();
    }

    
    public void onLoad() {
        context = FacesContext.getCurrentInstance();
        ResourceBundle calendarLabel = context.getApplication().getResourceBundle(context, "calendarLabel");
        weekDays = Arrays.asList(calendarLabel.getString("weekdaysnames").split(","));

        monthsNamesArray = calendarLabel.getString("monthsnames").split(",");

        selectedAttendee = null;
        year = null;
        month = null;
        day = null;
        attendeeWithGroup = false;
        period = null;
        periods = null;
        periodDatesModels = new ArrayList<>();

        LocalDate futureDate = LocalDate.now().plusMonths(whenNextIsAvailable);
        LocalDate currentDatePlus = LocalDate.now().plusMonths(1);
        periods = periodRepository.findPeriodsForRegistration(currentDatePlus, futureDate);
        List<Period> periodToRemove = new ArrayList<>();
        List<TrackPeriod> trackPeriodsFromGroup = cabinetPeriodRepository.findTrackPeriod();

        periods.forEach(p -> {
            List<TrackPeriod> trackPeriods = cabinetPeriodRepository
                    .findAllByStartDateBeforeAndEndDateAfter(p.getEndDate(), p.getStartDate());
            trackPeriods.retainAll(trackPeriodsFromGroup);
            if (trackPeriods.isEmpty()) {
                periodToRemove.add(p);
            }
        });

        if (!periodToRemove.isEmpty()) {
            periods.removeAll(periodToRemove);
        }
        if (periods.size() == 1) {
            setStateOfPeriods(true);

            setUpPeriodDatesModals(periods.get(0));

            setPeriod(periods.get(0));
        } else if (!periods.isEmpty()) {
            for (Period period : periods) {

                setUpPeriodDatesModals(period);

            }
            setStateOfPeriods(false);
        } else {
            setPeriodsAreNull(true);
        }
        boolean isClient = ((HttpServletRequest) context.getExternalContext().getRequest()).isUserInRole("CLIENT");
        if (isClient) {
            existingAttendee = true;
            collectExistingClientAttendees();
            disableAlreadyPaidAttendees();
            if (attendees != null && attendees.isEmpty()) {
                existingAttendee = false;
            }
        }
    }

    
    private void setUpPeriodDatesModals(Period period) {
        if (period != null) {
            if (period.getStartDate().isBefore(LocalDate.now().plusMonths(1).withDayOfMonth(1))) {
                periodDatesModels.add(new PeriodDatesModel(period.getName(),
                        LocalDate.now().plusMonths(1).withDayOfMonth(1)
                        , period.getEndDate(), period));
            } else {
                periodDatesModels.add(new PeriodDatesModel(period.getName(),
                        period.getStartDate(), period.getEndDate(), period));
            }
        }
    }

    
    public void checkCurrentRegistrationStep(int regPageStep) throws IOException {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Integer regStepInSession = Faces.getSessionAttribute("currentRegistrationStep");
        context = FacesContext.getCurrentInstance();
        boolean isClient = ((HttpServletRequest) context.getExternalContext().getRequest()).isUserInRole("CLIENT");

        if (regStepInSession == null) {
            regPageStep = 1;
            session.put("currentRegistrationStep", regPageStep);
            regStepInSession = regPageStep;
            redirectToRegStep(regStepInSession);
        }

        if (isClient) {
            if ((regPageStep > regStepInSession || regStepInSession == FOURTH_STEP || regStepInSession == SIXTH_STEP
                    || regStepInSession - regPageStep > FIRST_STEP) && regPageStep != regStepInSession) {
                redirectToRegStep(regStepInSession);
            } else {
                session.put("currentRegistrationStep", regPageStep);
            }
        } else {
            if ((regPageStep > regStepInSession) || (regStepInSession - regPageStep > FIRST_STEP)) {
                redirectToRegStep(regStepInSession);
            } else {
                session.put("currentRegistrationStep", regPageStep);
            }
        }

    }

    
    public void checkIfGroupAvailable() throws IOException {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Integer regStepInSession = Faces.getSessionAttribute("currentRegistrationStep");

        if (noGroupAvailable && regStepInSession == THIRD_STEP) {
            session.put("currentRegistrationStep", FOURTH_STEP);
            String redirectUri = "/registration/step-" + FOURTH_STEP;
            FacesContext.getCurrentInstance().getExternalContext().redirect(redirectUri);
        }
    }

    
    public void redirectToClientPage() throws IOException {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.remove("currentRegistrationStep");
        sessionMap.clear();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/client");
    }

    
    public void citiesFiller() {

        if (year != null && month != null && day != null && period != null) {
            birthday = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        } else {
            return;
        }

        groups = new ArrayList<>();

        List<TrackPeriod> trackPeriods = cabinetPeriodRepository
                .findAllByStartDateBeforeAndEndDateAfter(period.getEndDate(), period.getStartDate());
        trackPeriods.forEach(tp -> {
            List<Group> publicGroupsInGivenPeriod  = groupRepository.findByTrackPeriodAndIsPublicTrue(tp);
            if (publicGroupsInGivenPeriod  != null) {
                groups.addAll(publicGroupsInGivenPeriod);
            }
        });

        if (birthday != null) {
            groups = groupService.filterByAgeGroup(groups, birthday);
        }
        if (swimLevel != null) {
            groups = groupService.filterBySwimmingLevel(groups, swimLevel);
            List<Group> groupsToRemove = new ArrayList<>();
            for (Group group : groups) {
                if (group.getTotalNumOfAttendees() >= group.getNumOfAttendees()) {
                    groupsToRemove.add(group);
                }
            }
            groups.removeAll(groupsToRemove);
        }

        List<Pool> availablePools = groups.stream().map(Group::getPool).collect(Collectors.toList());
        if (availablePools.isEmpty()) {
            noGroupAvailable = true;

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            sessionMap = externalContext.getSessionMap();

            sessionMap.put("year", year);
            sessionMap.put("month", month);
            sessionMap.put("day", day);
        } else {
            noGroupAvailable = false;
            cities = poolService.getCities(availablePools);
        }

        city = null;
        workoutsPerWeek = 0;
        poolId = null;
        subscription = null;
        selectedGroupId = null;
    }

    
    public void checkIfTooYoung() {
        FacesContext context = FacesContext.getCurrentInstance();
        tooYoung = attendeeService.checkAge(year, month, day, 3);
        if (tooYoung) {
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "register.tooyoung", context);
        } else {
            tooYoung = attendeeService.checkAge(year, month, day, 5);
            if (tooYoung) {
                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "register.tooyoung.but.can.gp.reserve", context);
                tooYoung = false;
            }
        }
    }

    
    public void checkAvailability() {
        checkIfTooYoung();
        if (period != null) {
            citiesFiller();
        } else {
            noGroupAvailable = true;
        }
    }

    
    public String save() {
        if (!existingAttendee && selectedAttendee != null) {
            selectedAttendee = null;
        }
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", SECOND_STEP);
        return getRegStepUrl(SECOND_STEP);
    }

    
    public String saveStep2() {

        birthdayAsString = year + "-" + month + "-" + day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        birthday = LocalDate.parse(birthdayAsString, formatter);
        selectedGroupId = selectedHour.getId();
        BigDecimal selectedPrice = selectedHour.getTotalPrice();
        contractPeriods.add(period);
        firstWorkoutDays.add(selectedHour.getFirstDay());

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession currentSession = (HttpSession) externalContext.getSession(false);
        String sessionID = currentSession.getId();
        Group selectedGroup = groupRepository.findById(selectedGroupId).orElse(null);

        attendeeInSessionService.create(selectedGroup, sessionID);

        sessionMap = externalContext.getSessionMap();
        sessionMap.put("year", year);
        sessionMap.put("month", month);
        sessionMap.put("day", day);
        sessionMap.put("birthdate", birthday);
        sessionMap.put("city", city);
        sessionMap.put("workoutTimesPerWeek", workoutsPerWeek);
        sessionMap.put("poolId", poolId);
        sessionMap.put("subscriptionMonths", subscription);
        sessionMap.put("firstDay", selectedHour.getFirstDay());
        sessionMap.put("lastDay", selectedHour.getLastDay());
        sessionMap.put("selectedGroupId", selectedGroupId);
        sessionMap.put("selectedPrice", selectedPrice);
        sessionMap.put("selectedAttendee", selectedAttendee);
        sessionMap.put("attendeeSwimLevel", swimLevel);
        sessionMap.put("periods", contractPeriods);
        sessionMap.put("firstWorkoutDays", firstWorkoutDays);
        sessionMap.put("step2Check", true);

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", THIRD_STEP);
        return getRegStepUrl(THIRD_STEP);
    }

    
    public String saveReserved() {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();
        sessionMap.put("selectedAttendee", selectedAttendee);

        updateReservedAttendees();

        attendeeService.sendReservedAttendeeEmail(selectedAttendee.getClient());
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", FIRST_STEP);
        return getRegStepUrl(FIRST_STEP);
    }

    
    private String getRegStepUrl(int regStep) {
        return "/registration/step-" + regStep + "?faces-redirect=true";
    }

    
    public String saveRegisteredAndPaid() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();
        sessionMap.put("selectedAttendee", selectedAttendee);

        updateRegisteredAndPaidAttendees();

        sessionMap.put("currentRegistrationStep", FIRST_STEP);
        return getRegStepUrl(FIRST_STEP);
    }

    
    public String addAttendeeToReserve() {
        birthdayAsString = year + "-" + month + "-" + day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        birthday = LocalDate.parse(birthdayAsString, formatter);
        ClientPrincipal clientPrincipal = (ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal();

        Long clientId = clientPrincipal.getClientId();
        Client client = clientRepository.getOne(clientId);

        reservedAttendee = attendeeService.createWithStatusReserved(reservedAttendee, healthProblems, swimLevel,
                birthday, city, client);
        selectedAttendee = reservedAttendee;
        saveReserved();

        sessionMap.put("step2Check", false);
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", FIRST_STEP);
        return getRegStepUrl(FIRST_STEP);
    }

    
    public List<String> poolCities() {
        availableCities = new ArrayList<>();
        if (period != null) {

            List<TrackPeriod> trackPeriods = cabinetPeriodRepository
                    .findAllByStartDateBeforeAndEndDateAfter(period.getEndDate(), period.getStartDate());

            trackPeriods.forEach(tp -> {
                List<Group> publicGroupsInGivenPeriod  = groupRepository.findByTrackPeriodAndIsPublicTrue(tp);
                if (publicGroupsInGivenPeriod  != null) {
                    publicGroupsInGivenPeriod .forEach(g -> allCities.add((g.getPool()).getCity()));
                }
            });

            allCities = allCities.stream().filter(StreamUtil.distinctByKey(c -> c)).collect(Collectors.toList());
            availableCities.addAll(allCities);
        } else {
            allCities = poolRepository.findDistinctCity();
            availableCities.addAll(allCities);
        }
        return availableCities;
    }

    
    public boolean isCurrentPeriod(Period period) {

        LocalDate today = LocalDate.now();
        LocalDate periodEnd = period.getEndDate();

        long daysBetween = ChronoUnit.DAYS.between(today, periodEnd);
        YearMonth yearMonth;
        if (periodEnd.getMonthValue() != TWELVE) {
            yearMonth = YearMonth.of(periodEnd.getYear(), periodEnd.getMonthValue() + 1);
        } else {
            yearMonth = YearMonth.of(periodEnd.getYear(), periodEnd.getMonthValue());
        }
        int daysInMonth = yearMonth.lengthOfMonth();
        return daysBetween > daysInMonth;
    }

    
    public void updateRegisteredAttendees() {
        List<?> registeredAttendeesTemp = Faces.getSessionAttribute("registeredAttendees");
        List<Attendee> registeredAttendees = formAttendeeList(registeredAttendeesTemp);

        sessionMap.put("registeredAttendees", registeredAttendees);
    }

    
    public void updateRegisteredAndPaidAttendees() {
        List<?> registeredAndPaidAttendeesTemp = Faces.getSessionAttribute("registeredAndPaidAttendees");
        List<Attendee> registeredAndPaidAttendees = formAttendeeList(registeredAndPaidAttendeesTemp);

        sessionMap.put("registeredAndPaidAttendees", registeredAndPaidAttendees);
    }

    
    public void updateReservedAttendees() {
        List<?> reservedAttendeesTemp = Faces.getSessionAttribute("reservedAttendees");

        List<Attendee> reservedAttendees = formAttendeeList(reservedAttendeesTemp);

        sessionMap.put("reservedAttendees", reservedAttendees);
    }

    
    public void monthsAndDaysFiller() {

        months = new ArrayList<>();
        int currentYear = Year.now().getValue();
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        int targetMonth = TWELVE;
        if (year != null) {
            int j;
            if (Integer.parseInt(year) == currentYear) {
                targetMonth = currentMonth;
            }
            for (j = 1; j <= targetMonth; j++) {
                months.add(new SelectItem(j, monthsNamesArray[j - 1]));
            }
        }

        daysFiller();
    }

    
    public void daysFiller() {

        int currentYear = Year.now().getValue();
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int todayDay = currentDate.getDayOfMonth();
        days = new ArrayList<>();
        int i, currentDay, maxDaysInMonth;
        if (month != null && year != null) {
            if (Integer.parseInt(year) == currentYear && Integer.parseInt(month) == currentMonth) {
                maxDaysInMonth = todayDay;
            } else {
                maxDaysInMonth = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1).lengthOfMonth();
            }
            for (i = 1; i <= maxDaysInMonth; i++) {
                currentDay = i;
                days.add(new SelectItem("" + currentDay, "" + currentDay));
            }
        }
        checkIfTooYoung();
        citiesFiller();
    }

    
    public void workoutTimesFiller() {

        groupsByCity = groupRepository.filterByCity(city, groups);
        workoutTimes = groupsByCity.stream().map(Group::getWorkoutsPerWeek).distinct().collect(Collectors.toList());

        workoutsPerWeek = 0;
        poolId = null;
        subscription = null;
        selectedHour = null;
    }

    
    public void poolsFiller() {

        groupsByWorkoutTimes = groupService.filterByWorkoutsPerWeek(groupsByCity, workoutsPerWeek);
        filteredPools = groupsByWorkoutTimes.stream().map(Group::getPool).distinct().collect(Collectors.toList());

        poolId = null;
        selectedHour = null;
        subscription = null;
    }

    
    public void subscriptionsFiller() {

        groupsByPool = groupService.filterByPool(groupsByWorkoutTimes, poolId);

        long monthsDiff = subscriptionService.calculateMonthDiff(groupsByPool);

        subscriptionsList = subscriptionService.subscriptionNumberOfMonthsList(period, schoolSubscriptionsArray,
                summerSubscriptionsArray);

        subscriptionsList.removeIf(currentInt -> currentInt > monthsDiff);

        for (int subscription : subscriptionsList) {
            subscriptionsMap.put(subscription,
                    discountService.getDiscountRateBySubscriptionMonths(period.getId(), subscription));
        }

        subscription = null;
        selectedHour = null;
    }

    
    public void workingHoursFiller() {

        availableWorkingHours = new ArrayList<>();

        for (Group group : groupsByPool) {

            singleWorkoutPrice = groupService.getSingleWorkoutPrice(group);

            workingHoursStartEndDates();

            List<LocalDate> possibleWorkoutDays = new ArrayList<>();
            List<LocalDate> actualWorkoutDays;
            selectedGroupId = group.getId();

            int discountRate = discountService.getDiscountRateBySubscriptionMonths(period.getId(),
                    Integer.parseInt(subscription));
            singleWorkoutPrice = groupService.getSingleWorkoutPrice(group);
            HoursListElement groupWorkingHours = new HoursListElement(group.getId(), "", BigDecimal.ZERO,
                    new BigDecimal(discountRate), 0, null, null, BigDecimal.ZERO);
            List<TrackWorkingHour> workingHoursList = new ArrayList<>(
                    cabinetWorkingHourRepository.findByGroup(group));
            workingHoursList.sort(Comparator.comparing(TrackWorkingHour::getDayOfWeek));

            Iterator<TrackWorkingHour> workingHoursListIterator = workingHoursList.iterator();

            while (workingHoursListIterator.hasNext()) {

                GroupWorkout startingGroupWorkout = groupWorkoutRepository.findFirstByGroupId(selectedGroupId);
                TrackWorkingHour workingHour = workingHoursListIterator.next();

                if (startDate.isAfter(startingGroupWorkout.getDate().with(TemporalAdjusters.firstDayOfMonth()))) {

                    groupWorkoutRepository.findByGroupIdAndDateBetween(selectedGroupId, startDate, endDate)
                            .forEach(pwd -> possibleWorkoutDays.add(pwd.getDate()));
                } else {
                    startDate = startingGroupWorkout.getDate().with(TemporalAdjusters.firstDayOfMonth());

                    if (Integer.parseInt(subscription) == 1) {
                        endDate = startDate.plusMonths(Integer.parseInt(subscription)).withDayOfMonth(1).minusDays(1);
                    } else {
                        int months = Integer.parseInt(subscription);
                        endDate = startDate.plusMonths(months).withDayOfMonth(1).minusDays(1);
                    }
                    groupWorkoutRepository.findByGroupIdAndDateBetween(selectedGroupId, startDate, endDate)
                            .forEach(pwd -> possibleWorkoutDays.add(pwd.getDate()));
                }

                if (workingHoursListIterator.hasNext()) {
                    groupWorkingHours.setDaysAndHours(groupWorkingHours.getDaysAndHours()
                            .concat(weekDays.get(workingHour.getTrackWeekday().getDayOfWeek().ordinal()) + " " + "("
                                    + TimeUtil.minutesToTimeConverter(workingHour.getStartHour()) + " - "
                                    + TimeUtil.minutesToTimeConverter(workingHour.getEndHour()) + "), "));
                } else {
                    groupWorkingHours.setDaysAndHours(groupWorkingHours.getDaysAndHours()
                            .concat(weekDays.get(workingHour.getTrackWeekday().getDayOfWeek().ordinal()) + " " + "("
                                    + TimeUtil.minutesToTimeConverter(workingHour.getStartHour()) + " - "
                                    + TimeUtil.minutesToTimeConverter(workingHour.getEndHour()) + ")."));
                }
            }

            actualWorkoutDays = trackWorkingHourService.getWorkoutDatesWithoutNonWorkdays(possibleWorkoutDays);
            int workoutDaysCount = countWorkouts();
            BigDecimal grossPrice = singleWorkoutPrice.multiply(new BigDecimal(workoutDaysCount));
            final int oneHundred = 100;
            BigDecimal discountSaving = grossPrice.divide(BigDecimal.valueOf(oneHundred))
                    .multiply(new BigDecimal(discountRate));
            discountSaving = discountSaving.setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalPrice = grossPrice.subtract(discountSaving);
            Optional<LocalDate> optionalFirstDay = actualWorkoutDays.stream().min(Comparator.comparing(
                    LocalDate::toEpochDay));
            Optional<LocalDate> optionalLastDay = actualWorkoutDays.stream().max(Comparator.comparing(
                    LocalDate::toEpochDay));
            if (optionalFirstDay.isPresent()) {
                firstDay = optionalFirstDay.get();
                lastDay = optionalLastDay.get();
                groupWorkingHours.setTotalPrice(totalPrice);
                groupWorkingHours.setSavedMoney(discountSaving);
                groupWorkingHours.setWorkouts(workoutDaysCount);
                groupWorkingHours.setFirstDay(firstDay);
                groupWorkingHours.setLastDay(lastDay);
                groupWorkingHours.setSingleWorkoutPrice(singleWorkoutPrice);
                availableWorkingHours.add(groupWorkingHours);
                workingHoursStartEndDates();
            }
        }
        selectedHour = null;
        selectedGroupId = null;
    }

    
    public void collectExistingClientAttendees() {
        if (existingAttendee) {
            ClientPrincipal clientPrincipal = (ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal();
            Client client = clientRepository.getOne(clientPrincipal.getClientId());
            attendees = attendeeRepository.findByClient(client);
        } else {
            year = null;
            month = null;
            day = null;
        }

        if (periods.size() > 1) {
            period = null;
        }
    }

    
    public void disableAlreadyPaidAttendees() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();

        ClientPrincipal clientPrincipal = (ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        Client client = clientRepository.getOne(clientPrincipal.getClientId());
        for (Attendee attendee : client.getAttendees()) {
            AttendeeNonPaid nonPaidAttendee = attendeeNonPaidRepository.findByAttendee(attendee);
            if (nonPaidAttendee != null) {
                selectedAttendee = attendee;
                updateNonPaidAttendees();
            } else if (attendee.getGroup() != null) {
                Group group = attendee.getGroup();
                Group newGroup = attendee.getNewGroup();
                if (attendee.getContract() != null) {
                    ContractType contractType = attendee.getContract().getType();
                    if ((period != null && DateUtil.isBeforeOrEqual(period.getStartDate(), group.getStartDate())
                            && DateUtil.isAfterOrEqual(period.getEndDate(), group.getEndDate())) || newGroup != null) {
                        if ((DateUtil.isBeforeOrEqual(attendee.getContract().getValidTo(), period.getEndDate())
                                && (contractType.equals(ContractType.ACTIVE)
                                || contractType.equals(ContractType.NOT_ACTIVE))
                                && attendee.getContract().isSigned() != null) || newGroup != null) {
                            selectedAttendee = attendee;
                            saveRegisteredAndPaid();
                        }
                    }
                }
            }
        }
        selectedAttendee = null;
        sessionMap.put("selectedAttendee", null);
    }

    
    private void updateNonPaidAttendees() {
        List<?> nonPaidTemp = Faces.getSessionAttribute("nonPaidAttendees");
        List<Attendee> nonPaid = formAttendeeList(nonPaidTemp);
        sessionMap.put("nonPaidAttendees", nonPaid);
    }

    
    private void fillAttendeeData() {

        LocalDate attendeeBirthDate = selectedAttendee.getBirthDate();

        year = String.valueOf(attendeeBirthDate.getYear());
        month = String.valueOf(attendeeBirthDate.getMonthValue());
        day = String.valueOf(attendeeBirthDate.getDayOfMonth());

        if (selectedAttendee.getSwimmingLevel() == null) {
            swimLevel = SwimmingLevel.NOEXPERIENCE;
        } else {
            swimLevel = selectedAttendee.getSwimmingLevel();
        }

        Attendee attendee = selectedAttendee;
        if (attendee.getGroup() != null) {
            if (period != null
                && period.getStartDate().isAfter(attendee.getGroup().getEndDate()) && attendee.getNewGroup() == null) {
                attendeeHasGroup = true;
            } else if ((period != null && DateUtil.isBeforeOrEqual(
                    period.getStartDate(), attendee.getGroup().getStartDate())
                    && DateUtil.isAfterOrEqual(period.getEndDate(), attendee.getGroup().getEndDate()))
                    || attendee.getNewGroup() != null) {
                AttendeeNonPaid attendeeNonPaid = attendeeNonPaidRepository.findByAttendee(attendee);
                Contract contract = attendee.getContract();
                if (contract != null) {
                    ContractType contractType = contract.getType();
                    if (attendeeNonPaid != null) {
                        Contract attendeeContract = attendee.getContract();
                        attendeeContract.setType(ContractType.INVALID);
                        contractRepository.save(attendeeContract);
                        Invoice invoiceToInvalidate = invoiceRepository
                                .findTopByClientAndDocumentPathIsNullOrderByIdDesc(attendee.getClient());
                        if (invoiceToInvalidate != null) {
                            invoiceToInvalidate.setValid(false);
                            invoiceRepository.save(invoiceToInvalidate);
                        }
                        attendeeNonPaidRepository.deleteByAttendee(attendeeNonPaid.getAttendee());
                        attendee.setAttendeeNonPaid(null);
                        attendeeService.convertAttendeeToReservedAttendee(attendee);
                    } else if ((DateUtil.isBeforeOrEqual(contract.getValidTo(), period.getEndDate())
                            && (contractType.equals(ContractType.ACTIVE)
                            || contractType.equals(ContractType.NOT_ACTIVE))
                            && contract.isSigned() != null)
                            || attendee.getNewGroup() != null) {
                        PrimeFaces.current().executeScript("PF('alreadyRegisteredDialog').show()");
                    }
                }
                Subscription subscriptionWithNullInvoice = subscriptionRepository
                        .findByAttendeeAndInvoiceIsNull(attendee);
                if (subscriptionWithNullInvoice != null) {
                    subscriptionRepository.delete(subscriptionWithNullInvoice);
                    attendeeService.convertAttendeeToReservedAttendee(attendee);
                }
            } else {
                primeFacesWrapper.current().executeScript("PF('registerReserveDialog').show()");
            }
        } else {
            attendeeHasGroup = false;
        }
    }

    
    public String proceedWithExistingAttendee() {
        if (noGroupAvailable) {
            primeFacesWrapper.current().executeScript("PF('registerReserveDialog').show()");
            return "";
        }

        fillAttendeeData();

        if (attendeeHasGroup) {
            primeFacesWrapper.current().executeScript("PF('registerActiveAttendeeDialog').show()");
            return "";
        }

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", 2);
        return "/registration/step-2?faces-redirect=true";
    }

    
    public void checkAvailabilityForExistingAttendee() {
        attendeeWithGroup = true;
        fillAttendeeData();
        checkAvailability();
    }

    
    public String redirectToStep4FromStep1() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", FOURTH_STEP);
        return getRegStepUrl(FOURTH_STEP);
    }

    
    public String redirectToStep1() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("currentRegistrationStep", FIRST_STEP);
        return getRegStepUrl(FIRST_STEP);
    }

    
    public String redirectToStep4() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        birthdayAsString = year + "-" + month + "-" + day;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        birthday = LocalDate.parse(birthdayAsString, formatter);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();
        sessionMap.put("birthdate", birthday);
        sessionMap.put("attendeeSwimLevel", swimLevel);

        if (selectedHour == null) {
            sessionMap.put("workout_start_date", null);
        } else {
            sessionMap.put("workout_start_date", selectedHour.getFirstDay());
        }

        updateRegisteredAttendees();

        if (session.get("step2Check") == null) {
            sessionMap.put("step2Check", false);
        }

        session.put("currentRegistrationStep", FOURTH_STEP);
        return getRegStepUrl(FOURTH_STEP);
    }

    
    public Pool getSelectedPool() {
        if (poolId != null) {
            return poolRepository.getOne(poolId);
        }
        return null;
    }

    
    public int parseToInt(String value) {
        return Integer.parseInt(value);
    }

    
    public Map<Month, List<LocalDate>> getGroupWorkoutsDates() {
        if (selectedHour != null) {
            Long groupId = selectedHour.getId();
            int subscriptionMonths = parseToInt(subscription);
            startDate = selectedHour.getFirstDay().with(TemporalAdjusters.firstDayOfMonth());
            endDate = startDate.plusMonths(subscriptionMonths).withDayOfMonth(1).minusDays(1);

            return groupWorkoutService.getGroupWorkoutsDates(groupId, startDate, endDate, subscriptionMonths);
        }

        return null;
    }

    
    public int countWorkouts() {
        return groupWorkoutRepository.countByGroupIdAndDateBetween(selectedGroupId, startDate, endDate);
    }

    
    public String moveToReserve() {

        if (selectedAttendee != null) {

            Attendee attendee = selectedAttendee;

            if (attendee.getGroup() == null) {
                attendeeService.convertAttendeeToReservedAttendee(attendee);
            }
        }

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<?> registeredAttendees = Faces.getSessionAttribute("registeredAttendees");

        if (registeredAttendees == null || registeredAttendees.isEmpty()) {
            session.put("currentRegistrationStep", 1);
            return "/registration/step-1?faces-redirect=true";
        }

        session.put("currentRegistrationStep", FOURTH_STEP);
        return "/registration/step-4?faces-redirect=true";

    }

    
    public void redirectToRegStep(Integer regStepInSession) throws IOException {
        String redirectUri = "/registration/step-" + regStepInSession;
        if (regStepInSession == FIFTH_STEP || regStepInSession == SIXTH_STEP) {
            redirectUri = "/registration/step-" + regStepInSession + ".xhtml";
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(redirectUri);
    }

    
    private void workingHoursStartEndDates() {

        startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        if (startDate.isBefore(period.getStartDate())) {
            startDate = period.getStartDate();
        }

        endDate = startDate.plusMonths(Integer.parseInt(subscription)).withDayOfMonth(1).minusDays(1);
    }

    
    private List<Attendee> formAttendeeList(List<?> attendeeList) {
        List<Attendee> returnAttendees = new ArrayList<>();
        if (attendeeList != null) {
            for (Object attendee : attendeeList) {
                returnAttendees.add((Attendee) attendee);
            }
        }

        if (!returnAttendees.contains(selectedAttendee)) {
            returnAttendees.add(selectedAttendee);
        }
        return returnAttendees;
    }

    
    public void exit() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/");
    }

    
    public List<Period> getPeriods() {
        return periods;
    }

    
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    
    public String getMonth() {
        return month;
    }

    
    public void setMonth(String month) {
        this.month = month;
    }

    
    public List<SelectItem> getMonths() {
        return months;
    }

    
    public void setMonths(List<SelectItem> months) {
        this.months = months;
    }

    
    public SwimmingLevel getSwimLevel() {
        return swimLevel;
    }

    
    public void setSwimLevel(SwimmingLevel swimLevel) {
        this.swimLevel = swimLevel;
    }

    
    public LocalDate getBirthday() {
        return birthday;
    }

    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    
    public String getBirthdayAsString() {
        return birthdayAsString;
    }

    
    public void setBirthdayAsString(String birthdayAsString) {
        this.birthdayAsString = birthdayAsString;
    }

    
    public List<SelectItem> getYears() {
        return years;
    }

    
    public void setYears(List<SelectItem> years) {
        this.years = years;
    }

    
    public String getYear() {
        return year;
    }

    
    public void setYear(String year) {
        this.year = year;
    }

    
    public String getDay() {
        return day;
    }

    
    public void setDay(String day) {
        this.day = day;
    }

    
    public List<SelectItem> getDays() {
        return days;
    }

    
    public void setDays(List<SelectItem> days) {
        this.days = days;
    }

    
    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    
    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    
    public List<Group> getGroups() {
        return groups;
    }

    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    
    public List<String> getCities() {
        return cities;
    }

    
    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    
    public String getCity() {
        return city;
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public List<WorkoutsPerWeek> getWorkoutTimes() {
        return workoutTimes;
    }

    
    public void setWorkoutTimes(List<WorkoutsPerWeek> workoutTimes) {
        this.workoutTimes = workoutTimes;
    }

    
    public int getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(int workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public Long getPoolId() {
        return poolId;
    }

    
    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    
    public List<Pool> getFilteredPools() {
        return filteredPools;
    }

    
    public void setFilteredPools(List<Pool> filteredPools) {
        this.filteredPools = filteredPools;
    }

    
    public String getSubscription() {
        return subscription;
    }

    
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    
    public HoursListElement getSelectedHour() {
        return selectedHour;
    }

    
    public void setSelectedHour(HoursListElement selectedHour) {
        this.selectedHour = selectedHour;
    }

    
    public List<HoursListElement> getAvailableWorkingHours() {
        return availableWorkingHours;
    }

    
    public void setAvailableWorkingHours(List<HoursListElement> availableWorkingHours) {
        this.availableWorkingHours = availableWorkingHours;
    }

    
    public Long getSelectedGroupId() {
        return selectedGroupId;
    }

    
    public void setSelectedGroupId(Long selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

    
    public List<Integer> getSubscriptionsList() {
        return subscriptionsList;
    }

    
    public void setSubscriptionsList(List<Integer> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;
    }

    
    public boolean isTooYoung() {
        return tooYoung;
    }

    
    public void setTooYoung(boolean tooYoung) {
        this.tooYoung = tooYoung;
    }

    
    public boolean isNoGroupAvailable() {
        return noGroupAvailable;
    }

    
    public void setNoGroupAvailable(boolean noGroupAvailable) {
        this.noGroupAvailable = noGroupAvailable;
    }

    
    public Map<Integer, Integer> getSubscriptionsMap() {
        return subscriptionsMap;
    }

    
    public void setSubscriptionsMap(Map<Integer, Integer> subscriptionsMap) {
        this.subscriptionsMap = subscriptionsMap;
    }

    
    public List<Long> getGroupsIdList() {
        return groupsIdList;
    }

    
    public void setGroupsIdList(List<Long> groupsIdList) {
        this.groupsIdList = groupsIdList;
    }

    
    public List<Attendee> getAttendees() {
        return attendees;
    }

    
    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    
    public Attendee getSelectedAttendee() {
        return selectedAttendee;
    }

    
    public void setSelectedAttendee(Attendee selectedAttendee) {
        this.selectedAttendee = selectedAttendee;
    }

    
    public boolean isExistingAttendee() {
        return existingAttendee;
    }

    
    public void setExistingAttendee(boolean existingAttendee) {
        this.existingAttendee = existingAttendee;
    }

    
    public BigDecimal getSingleWorkoutPrice() {
        return singleWorkoutPrice;
    }

    
    public void setSingleWorkoutPrice(BigDecimal singleWorkoutPrice) {
        this.singleWorkoutPrice = singleWorkoutPrice;
    }

    
    public LocalDate getFirstDay() {
        return firstDay;
    }

    
    public Period getPeriod() {
        return period;
    }

    
    public Attendee getReservedAttendee() {
        return reservedAttendee;
    }

    
    public void setReservedAttendee(Attendee reservedAttendee) {
        this.reservedAttendee = reservedAttendee;
    }

    
    public void setPeriod(Period period) {
        this.period = period;
    }

    
    public void setFirstDay(LocalDate firstDay) {
        this.firstDay = firstDay;
    }

    
    public LocalDate getLastDay() {
        return lastDay;
    }

    
    public void setLastDay(LocalDate lastDay) {
        this.lastDay = lastDay;
    }

    
    public List<String> getAvailableCities() {
        return availableCities;
    }

    
    public void setAvailableCities(List<String> availableCities) {
        this.availableCities = availableCities;
    }

    
    public boolean isAttendeeWithGroup() {
        return attendeeWithGroup;
    }

    
    public void setAttendeeWithGroup(boolean attendeeWithGroup) {
        this.attendeeWithGroup = attendeeWithGroup;
    }

    
    public boolean isStateOfPeriods() {
        return stateOfPeriods;
    }

    
    public void setStateOfPeriods(boolean stateOfPeriods) {
        this.stateOfPeriods = stateOfPeriods;
    }

    
    public boolean isPeriodsAreNull() {
        return periodsAreNull;
    }

    
    public void setPeriodsAreNull(boolean periodsAreNull) {
        this.periodsAreNull = periodsAreNull;
    }

    
    public String getHealthProblems() {
        return healthProblems;
    }

    
    public void setHealthProblems(String healthProblems) {
        this.healthProblems = healthProblems;
    }

    
    public boolean getAttendeeHasGroup() {
        return attendeeHasGroup;
    }

    
    public void setAttendeeHasGroup(boolean attendeeHasGroup) {
        this.attendeeHasGroup = attendeeHasGroup;
    }

    
    public List<PeriodDatesModel> getPeriodDatesModels() {
        return periodDatesModels;
    }

    
    public void setPeriodDatesModels(List<PeriodDatesModel> periodDatesModels) {
        this.periodDatesModels = periodDatesModels;
    }
}
