package com.mAdmin.controller;

import com.mAdmin.enumerator.AgeGroup;
import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.enumerator.WorkoutsPerWeek;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.DepartmentNonWorkdayRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.RoleRepository;
import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.repository.CabinetRepository;
import com.mAdmin.repository.CabinetWeekdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.Role;
import com.mAdmin.entity.Season;
import com.mAdmin.entity.Track;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.service.GroupWorkoutService;
import com.mAdmin.util.PrimeFacesWrapper;
import com.mAdmin.util.TimeUtil;
import com.mAdmin.view.CoachTakenDaysModel;
import com.mAdmin.view.CoachTakenTimeModel;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Controller
@Scope(value = "view")
public class TimetableController {

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Autowired
    private PeriodRepository seasonPeriodRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private RoleRepository roleRepository;

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private DepartmentNonWorkdayRepository departmentNonWorkdayRepository;

    
    @Autowired
    private GroupWorkoutService groupWorkoutService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private List<Season> seasons;

    
    private List<Period> seasonsPeriods;

    
    private List<Pool> pools;

    
    private List<Track> tracks;

    
    private List<TrackPeriod> trackPeriods;

    
    private Collection<Employee> coaches;

    
    private final Set<TrackWorkingHour> trackWorkingHours = new HashSet<>();

    
    private List<GroupWorkout> groupWorkouts = new ArrayList<>();

    
    private List<AgeGroup> ageGroups;

    
    private List<TrackWeekday> weekdays;

    
    private List<WorkoutsPerWeek> workoutsPerWeek;

    
    private List<Group> groups;

    
    private List<Employee> busyCoaches;

    
    private SwimmingLevel[] swimmingLevels;

    
    private Season selectedSeason;

    
    private Period selectedSeasonPeriod;

    
    private Pool selectedPool;

    
    private Track selectedTrack;

    
    private TrackPeriod selectedTrackPeriod;

    
    private Set<Employee> selectedCoaches;

    
    private final Set<TrackWorkingHour> registeredGroupsWorkingHours = new HashSet<>();

    
    private AgeGroup selectedAgeGroup;

    
    private SwimmingLevel selectedSwimmingLevel;

    
    private WorkoutsPerWeek selectedWorkoutsPerWeek;

    
    private int selectedGroupSize;

    
    private boolean enabledSelectMenu = false;

    
    private boolean enabledField = false;

    
    private boolean multipleWorkoutPerWeek = false;

    
    private boolean firstGroupSelected = false;

    
    private int selectedTimeId;

    
    private static final int SIX = 6;

    
    private static final int TEN = 10;
    
    private List<Integer> groupSize = new ArrayList<>(Arrays.asList(SIX, TEN));

    
    private final Map<Integer, String> groupsColors = new HashMap<>();

    
    private final String[] colors = new String[] {"#800000", "#9A6324", "#808000", "#469990", "#000075", "#e6194B",
            "#f58231", "#ffe119", "#bfef45", "#42d4f4", "#4363d8", "#911eb4", "#f032e6", "#911eb4", "#fabebe",
            "#ffd8b1", "#aaffc3" };
    
    private boolean multiplePoolsSameDay;

    
    private List<CoachTakenDaysModel> coachTakenDaysModels;

    
    private List<CoachTakenTimeModel> coachTakenTimesModel;

    
    @PostConstruct
    public void init() {
        List<Season> sortedSeasons = seasonRepository.findAll();
        Collections.reverse(sortedSeasons);
        setSeasons(sortedSeasons);
        setAgeGroups(Arrays.asList(AgeGroup.values()));
        setWorkoutsPerWeek(Arrays.asList(WorkoutsPerWeek.values()));
        setSwimmingLevels(SwimmingLevel.values());
        Role coachRole = roleRepository.findByName("ROLE_COACH");

        List<Employee> sortedCoaches = new ArrayList<>();
        sortedCoaches.addAll(coachRole.getEmployees());
        Collections.sort(sortedCoaches);
        setCoaches(sortedCoaches);
    }

    
    public void selectSeason() {
        List<Period> sortedPeriods = seasonPeriodRepository.findBySeasonId(selectedSeason.getId());
        Collections.reverse(sortedPeriods);
        setSeasonsPeriods(sortedPeriods);
    }

    
    public void selectSeasonPeriod() {
        List<Pool> sortedPools = poolRepository.findAll();
        Collections.reverse(sortedPools);
        setPools(sortedPools);
        selectedPool = null;
        selectedTrack = null;
        tracks = new ArrayList<>();
        selectedTrackPeriod = null;
        trackPeriods = new ArrayList<>();
    }

    
    public void selectPool() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<Track> emptyTracks = new ArrayList<>();
        tracks = cabinetRepository.findByPoolIdAndSeasonPeriod(getSelectedPool().getId(),
                getSelectedSeasonPeriod().getStartDate(), getSelectedSeasonPeriod().getEndDate());
        for (Track track : tracks) {
            if (track.getTrackPeriods().isEmpty()) {
                emptyTracks.add(track);
            }
        }

        tracks.removeAll(emptyTracks);

        if (tracks.isEmpty()) {
            trackPeriods = null;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "admin.tracks.empty", context);
        } else {
            Collections.reverse(tracks);
            setTracks(tracks);
        }
    }

    
    public void selectTrack() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<TrackPeriod> emptyTrackPeriods = new ArrayList<>();
        trackPeriods = cabinetPeriodRepository.findByTrackIdAndSeasonPeriod(selectedTrack.getId(),
                getSelectedSeasonPeriod().getStartDate(), getSelectedSeasonPeriod().getEndDate());
        for (TrackPeriod trackPeriod : trackPeriods) {
            if (trackPeriod.getTrackWeekdays().isEmpty()) {
                emptyTrackPeriods.add(trackPeriod);
            } else {
                for (TrackWeekday trackWeekday : trackPeriod.getTrackWeekdays()) {
                    if (trackWeekday.getTrackWorkingHours().isEmpty()) {
                        emptyTrackPeriods.add(trackPeriod);
                    }
                }
            }

        }

        trackPeriods.removeAll(emptyTrackPeriods);

        if (trackPeriods.isEmpty()) {
            messageBundleComponent.generateMessage(
                    FacesMessage.SEVERITY_ERROR, "admin.trackPeriods.empty", context);
        } else {
            Collections.reverse(trackPeriods);
            setTrackPeriods(trackPeriods);
        }
    }

    
    public void selectTrackPeriod() {
        FacesContext context = FacesContext.getCurrentInstance();
        setEnabledSelectMenu(true);
        if (departmentNonWorkdayRepository.findByPoolAndSeason(getSelectedPool(), getSelectedSeason()).isEmpty()) {
            messageBundleComponent.generateMessage(
                    FacesMessage.SEVERITY_WARN, "timetable.season.has.no.nonworkdays", context);
        }
    }

    
    public void filterWorkdaysActivities() {
        if (selectedWorkoutsPerWeek.getValue() > 1) {
            setMultipleWorkoutPerWeek(true);
        } else {
            setMultipleWorkoutPerWeek(false);
        }
        setWeekdays(cabinetWeekdayRepository.findByTrackPeriodId(getSelectedTrackPeriod().getId()));
        prepareGroupsData();
        setEnabledField(true);
    }

    
    public void activateSettingsChanging() {
        setEnabledField(false);
    }

    
    public void prepareGroupDataObject(Long id) {
        trackWorkingHours.clear();
        if (multipleWorkoutPerWeek) {
            if (getSelectedTimeId() != 0 && id == getSelectedTimeId()) {
                setFirstGroupSelected(false);
                setSelectedTimeId(0);
                prepareGroupsData();
            } else if (firstGroupSelected) {
                setFirstGroupSelected(false);
                TrackWorkingHour trackWorkingHour = cabinetWorkingHourRepository.findById(id).orElse(null);
                Long selectedTimeId = (long) getSelectedTimeId();
                TrackWorkingHour secondTrackWorkingHour = cabinetWorkingHourRepository
                        .findById(selectedTimeId).orElse(null);
                trackWorkingHours.add(trackWorkingHour);
                trackWorkingHours.add(secondTrackWorkingHour);
                boolean overlapping = isTimeOverlapping();
                if (overlapping) {
                    reportTimeOverlapping();
                } else {
                    if (multiplePoolsSameDay) {
                        notifyMultiplePoolsSameDay();
                    } else {
                        createGroup();
                        setSelectedTimeId(0);
                    }
                }
            } else {
                setFirstGroupSelected(true);
                setSelectedTimeId(Math.toIntExact(id));
                prepareGroupsData();
            }
        } else {
            Optional<TrackWorkingHour> trackWorkingHour = cabinetWorkingHourRepository.findById(id);
            if (trackWorkingHour.isPresent()) {
                trackWorkingHours.add(trackWorkingHour.get());
                boolean overlapping = isTimeOverlapping();
                if (overlapping) {
                    reportTimeOverlapping();
                } else {
                    if (multiplePoolsSameDay) {
                        notifyMultiplePoolsSameDay();
                    } else {
                        createGroup();
                    }
                }
            }
        }
    }

    
    private void reportTimeOverlapping() {
        String commaSeparatedCoaches = busyCoaches.stream().map(Employee::getFullName)
                .collect(Collectors.joining(", "));
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                MessageFormat.format(msg.getString("timetable.group.notCreated"), "msg") + commaSeparatedCoaches));
    }

    
    public void notifyMultiplePoolsSameDay() {
        PrimeFaces current = primeFacesWrapper.current();
        current.executeScript("PF('confirmDlg').show();");
    }

    
    private boolean isTimeOverlapping() {
        multiplePoolsSameDay = false;
        busyCoaches = new ArrayList<>();
        coachTakenTimesModel = new ArrayList<>();
        for (Employee coach : selectedCoaches) {
            List<Group> groupsOfCoach = groupRepository.findByCoachId(coach.getId());
            List<TrackWorkingHour> coachTakenWorkingHours = new ArrayList<>();
            boolean coachIsBusy = false;
            for (Group group : groupsOfCoach) {
                if (!group.getStartDate().isBefore(selectedSeasonPeriod.getStartDate())
                        && (!group.getEndDate().isAfter(selectedSeasonPeriod.getEndDate()))) {
                    coachTakenWorkingHours.addAll(cabinetWorkingHourRepository.findByGroup(group));
                }
            }

            for (TrackWorkingHour coachTakenWorkingHour : coachTakenWorkingHours) {
                for (TrackWorkingHour trackWorkingHour : trackWorkingHours) {
                    if (trackWorkingHour.getTrackWeekday().getDayOfWeek().ordinal() == coachTakenWorkingHour
                            .getTrackWeekday().getDayOfWeek().ordinal()) {

                        if (!coachTakenWorkingHour.getGroup().getPool().getId().equals(selectedPool.getId())) {

                            CoachTakenTimeModel coachTakenTime = new CoachTakenTimeModel(coach.getFullName(),
                                    coachTakenWorkingHour.getGroup().getPool().getName(),
                                    coachTakenWorkingHour.getDayOfWeek(), coachTakenWorkingHour.getStartHour(),
                                    coachTakenWorkingHour.getEndHour());
                            if (!checkCoachTakenTimePresence(coachTakenTime)) {
                                coachTakenTimesModel.add(coachTakenTime);
                            }
                            multiplePoolsSameDay = true;
                        }

                        if (((trackWorkingHour.getStartHour() < coachTakenWorkingHour.getEndHour())
                                && trackWorkingHour.getEndHour() > coachTakenWorkingHour.getStartHour())
                                || (trackWorkingHour.getStartHour() == coachTakenWorkingHour.getStartHour()
                                        && trackWorkingHour.getEndHour() == coachTakenWorkingHour.getEndHour())) {
                            coachIsBusy = true;
                        }
                    }
                }
            }
            if (coachIsBusy) {
                busyCoaches.add(coach);
            }
        }
        if (!coachTakenTimesModel.isEmpty()) {
            formBusyWeekdaysForCoachesMessage(coachTakenTimesModel);
        }
        return !busyCoaches.isEmpty(); 
    }

    
    private void formBusyWeekdaysForCoachesMessage(List<CoachTakenTimeModel> coachTakenTimes) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle calendarLabel = context.getApplication().getResourceBundle(context, "calendarLabel");
        String[] weekdaysArray = calendarLabel.getString("weekdaysNamesInBold").split(",");

        coachTakenDaysModels = new ArrayList<>();

        coachTakenTimes = coachTakenTimes.stream().sorted(Comparator.comparing(CoachTakenTimeModel::getDayOfWeek))
                .collect(Collectors.toList());

        CoachTakenDaysModel coachTakenDaysModel = new CoachTakenDaysModel(coachTakenTimes.get(0)
                .getDayOfWeek().ordinal());
        coachTakenDaysModel.setWeekDayName(weekdaysArray[coachTakenDaysModel.getWeekdayOrdinal()] + ":");
        coachTakenDaysModels.add(coachTakenDaysModel);

        for (CoachTakenTimeModel coachTakenTime : coachTakenTimes) {
            if (coachTakenTime.getDayOfWeek().ordinal() != coachTakenDaysModel.getWeekdayOrdinal()) {
                coachTakenDaysModel = new CoachTakenDaysModel(coachTakenTime.getDayOfWeek().ordinal());
                coachTakenDaysModel.setWeekDayName(weekdaysArray[coachTakenDaysModel.getWeekdayOrdinal()] + ":");
                coachTakenDaysModels.add(coachTakenDaysModel);
            }

                coachTakenDaysModel.getCoachTakenTimes().add(coachTakenTime);
        }
    }

    
    private boolean checkCoachTakenTimePresence(CoachTakenTimeModel coachTakenTime) {
        for (CoachTakenTimeModel coachTakenTimeModel : coachTakenTimesModel) {
            if (coachTakenTime.getWorkTimeStart().equals(coachTakenTimeModel.getWorkTimeStart())
                    && coachTakenTime.getWorkTimeEnd().equals(coachTakenTimeModel.getWorkTimeEnd())
                    && coachTakenTime.getCoachName().equals(coachTakenTimeModel.getCoachName())
                    && coachTakenTime.getCoachTakenPool().equals(coachTakenTimeModel.getCoachTakenPool())) {
                return true;
            }
        }
        return false;
    }

    
    public void createGroup() {

        Group group = new Group();
        group.setAgeGroup(getSelectedAgeGroup());
        group.setPool(getSelectedPool());
        group.setEmployees(getSelectedCoaches());
        group.setTrackPeriod(getSelectedTrackPeriod());
        group.setSwimmingLevel(getSelectedSwimmingLevel());
        group.setNumberOfAttendees(getSelectedGroupSize());
        group.setMaxAttendees(getSelectedGroupSize() + 2);
        group.setWorkoutsPerWeek(getSelectedWorkoutsPerWeek());
        group.setIsPublic(false);
        groupWorkouts.clear();

        List<PoolNonWorkday> nonWorkdays = departmentNonWorkdayRepository.findByPoolAndSeason(
                getSelectedPool(), getSelectedSeason());

        groupWorkouts = groupWorkoutService.getAllGroupWorkouts(getSelectedTrackPeriod().getStartDate(),
                getSelectedTrackPeriod().getEndDate(), nonWorkdays, trackWorkingHours, group);

        group.setGroupWorkouts(groupWorkouts);

        Optional<GroupWorkout> groupWorkout = groupWorkouts.stream().findFirst();
        if (groupWorkout.isPresent()) {
            group.setStartDate(groupWorkout.get().getDate());
        }
        groupWorkout = groupWorkouts.stream().reduce((first, second) -> second);
        if (groupWorkout.isPresent()) {
            group.setEndDate(groupWorkout.get().getDate());
        }

        group.setName(getSelectedPool().getAbbreviation() + "-" + getSelectedTrack().getNumber() + "/"
                + group.getAgeGroup().getValue() + "/" + groupWorkoutDaysAndHoursFormatted());
        group = groupRepository.save(group);

        for (TrackWorkingHour trackWorkingHour : trackWorkingHours) {
            trackWorkingHour.setGroup(group);
        }
        cabinetWorkingHourRepository.saveAll(trackWorkingHours);

        prepareGroupsData();

        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "",
                MessageFormat.format(msg.getString("timetable.group.created"), group.getName())));
    }

    
    public boolean checkIfGroupExist(Long id) {
        Optional<TrackWorkingHour> currentWorkingHour = cabinetWorkingHourRepository.findById(id);
        return currentWorkingHour.filter(registeredGroupsWorkingHours::contains).isPresent();
    }

    
    public String getFormattedGroupName(Long id, String action) {
        CabinetPeriodController periodController = new CabinetPeriodController();
        for (Group group : groups) {
            for (TrackWorkingHour trackWorkingHour : cabinetWorkingHourRepository.findByGroup(group)) {
                if (trackWorkingHour.getId().equals(id)) {
                    if (action.equals("show")) {
                        return group.getName();
                    }
                    if (action.equals("delete")) {
                        return "\u201E" + group.getName() + "\u201C" + " ("
                                + periodController.minutesToTimeConverter(trackWorkingHour.getStartHour()) + " - "
                                + periodController.minutesToTimeConverter(trackWorkingHour.getEndHour()) + ") ";
                    }
                }
            }
        }
        return "unavailable";
    }

    
    public String groupWorkoutDaysAndHoursFormatted() {
        CabinetPeriodController periodController = new CabinetPeriodController();
        StringBuilder workdaysWorkhours = new StringBuilder();

        List<TrackWorkingHour> trackWorkingsHours = new ArrayList<>(trackWorkingHours);

        if (trackWorkingHours.size() == 2) {
            if (trackWorkingsHours.get(0).getStartHour() > trackWorkingsHours.get(1).getStartHour()) {
                TrackWorkingHour temp = trackWorkingsHours.get(0);
                TrackWorkingHour temp1 = trackWorkingsHours.get(1);

                trackWorkingsHours.clear();
                trackWorkingsHours.add(temp1);
                trackWorkingsHours.add(temp);
            }
        }

        int index = 1;
        for (TrackWorkingHour trackWorkingHour : trackWorkingsHours) {
            String[] firstPartOfHour = periodController.minutesToTimeConverter(trackWorkingHour.getStartHour())
                    .split(":");
            if (index < trackWorkingHours.size()) {
                if (trackWorkingHour.getStartHour() % TimeUnit.HOURS.toMinutes(1) == 0) {
                    workdaysWorkhours.append(TimeUtil.dayOfWeekInRoman(trackWorkingHour.getDayOfWeek().ordinal()))
                            .append("-").append(firstPartOfHour[0])
                            .append("/");
                    index++;
                } else {
                    workdaysWorkhours.append(TimeUtil.dayOfWeekInRoman(trackWorkingHour.getDayOfWeek().ordinal()))
                            .append("-")
                            .append(periodController.minutesToTimeConverter(trackWorkingHour.getStartHour()))
                            .append("/");
                    index++;
                }
            } else if (trackWorkingHour.getStartHour() % TimeUnit.HOURS.toMinutes(1) == 0) {
                workdaysWorkhours.append(TimeUtil.dayOfWeekInRoman(trackWorkingHour.getDayOfWeek().ordinal()))
                        .append("-").append(firstPartOfHour[0]);
            } else {
                workdaysWorkhours.append(TimeUtil.dayOfWeekInRoman(trackWorkingHour.getDayOfWeek().ordinal()))
                        .append("-").append(periodController.minutesToTimeConverter(trackWorkingHour.getStartHour()));
            }
        }
        return workdaysWorkhours.toString();
    }

    
    private void prepareGroupsData() {
        setGroups(groupRepository.findByTrackPeriodId(getSelectedTrackPeriod().getId()));
        int index = 0;
        for (Group group : groups) {
            registeredGroupsWorkingHours.addAll(cabinetWorkingHourRepository.findByGroup(group));
            if (cabinetWorkingHourRepository.findByGroup(group).size() > 1) {
                Integer groupId = Math.toIntExact(group.getId());
                groupsColors.put(groupId, String.valueOf(colors[index++]));
            }
        }
    }

    
    public String getGroupColor(Long id) {
        Optional<TrackWorkingHour> trackWorkingHour = cabinetWorkingHourRepository.findById(id);
        if (trackWorkingHour.isPresent()) {
            Long trackWorkingHourGroupId = trackWorkingHour.get().getGroup().getId();
            return groupsColors.get(Math.toIntExact(trackWorkingHourGroupId));
        }
        return "";
    }

    
    public void deleteGroup(Long id) {

        FacesContext context = FacesContext.getCurrentInstance();
        Optional<TrackWorkingHour> trackWorkingHour = cabinetWorkingHourRepository.findById(id);
        if (trackWorkingHour.isPresent()) {
            Group group = trackWorkingHour.get().getGroup();

            if (!group.getIsPublic()) {
                List<TrackWorkingHour> trackWorkingHours;
                trackWorkingHours = cabinetWorkingHourRepository.findByGroup(group);
                trackWorkingHours.forEach(hour -> hour.setGroup(null));
                cabinetWorkingHourRepository.saveAll(trackWorkingHours);
                groupRepository.delete(group);
                registeredGroupsWorkingHours.clear();
                prepareGroupsData();

                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO,
                        "timetable.group.deleted", context);
            } else {
                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                        "timetable.group.isPublic.cannot.be.deleted", context);
            }
        }
    }

    
    public List<Season> getSeasons() {
        return seasons;
    }

    
    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    
    public List<Period> getSeasonsPeriods() {
        return seasonsPeriods;
    }

    
    public void setSeasonsPeriods(List<Period> seasonsPeriods) {
        this.seasonsPeriods = seasonsPeriods;
    }

    
    public Season getSelectedSeason() {
        return selectedSeason;
    }

    
    public void setSelectedSeason(Season selectedSeason) {
        this.selectedSeason = selectedSeason;
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public Period getSelectedSeasonPeriod() {
        return selectedSeasonPeriod;
    }

    
    public void setSelectedSeasonPeriod(Period selectedSeasonPeriod) {
        this.selectedSeasonPeriod = selectedSeasonPeriod;
    }

    
    public Pool getSelectedPool() {
        return selectedPool;
    }

    
    public void setSelectedPool(Pool selectedPool) {
        this.selectedPool = selectedPool;
    }

    
    public List<Track> getTracks() {
        return tracks;
    }

    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    
    public Track getSelectedTrack() {
        return selectedTrack;
    }

    
    public void setSelectedTrack(Track selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    
    public List<TrackPeriod> getTrackPeriods() {
        return trackPeriods;
    }

    
    public void setTrackPeriods(List<TrackPeriod> trackPeriods) {
        this.trackPeriods = trackPeriods;
    }

    
    public TrackPeriod getSelectedTrackPeriod() {
        return selectedTrackPeriod;
    }

    
    public void setSelectedTrackPeriod(TrackPeriod selectedTrackPeriod) {
        this.selectedTrackPeriod = selectedTrackPeriod;
    }

    
    public Collection<Employee> getCoaches() {
        return coaches;
    }

    
    public void setCoaches(Collection<Employee> coaches) {
        this.coaches = coaches;
    }

    
    public boolean isEnabledSelectMenu() {
        return enabledSelectMenu;
    }

    
    public void setEnabledSelectMenu(boolean enabledSelectMenu) {
        this.enabledSelectMenu = enabledSelectMenu;
    }

    
    public int getSelectedGroupSize() {
        return selectedGroupSize;
    }

    
    public void setSelectedGroupSize(int selectedGroupSize) {
        this.selectedGroupSize = selectedGroupSize;
    }

    
    public List<AgeGroup> getAgeGroups() {
        return ageGroups;
    }

    
    public void setAgeGroups(List<AgeGroup> ageGroups) {
        this.ageGroups = ageGroups;
    }

    
    public AgeGroup getSelectedAgeGroup() {
        return selectedAgeGroup;
    }

    
    public void setSelectedAgeGroup(AgeGroup selectedAgeGroup) {
        this.selectedAgeGroup = selectedAgeGroup;
    }

    
    public SwimmingLevel[] getSwimmingLevels() {
        return swimmingLevels;
    }

    
    public void setSwimmingLevels(SwimmingLevel[] swimmingLevels) {
        this.swimmingLevels = swimmingLevels;
    }

    
    public SwimmingLevel getSelectedSwimmingLevel() {
        return selectedSwimmingLevel;
    }

    
    public void setSelectedSwimmingLevel(SwimmingLevel selectedSwimmingLevel) {
        this.selectedSwimmingLevel = selectedSwimmingLevel;
    }

    
    public List<TrackWeekday> getWeekdays() {
        return weekdays;
    }

    
    public void setWeekdays(List<TrackWeekday> weekdays) {
        this.weekdays = weekdays;
    }

    
    public boolean isEnabledField() {
        return enabledField;
    }

    
    public void setEnabledField(boolean enabledField) {
        this.enabledField = enabledField;
    }

    
    public List<Integer> getGroupSize() {
        return groupSize;
    }

    
    public void setGroupSize(List<Integer> groupSize) {
        this.groupSize = groupSize;
    }

    
    public List<WorkoutsPerWeek> getWorkoutsPerWeek() {
        return workoutsPerWeek;
    }

    
    public void setWorkoutsPerWeek(List<WorkoutsPerWeek> workoutsPerWeek) {
        this.workoutsPerWeek = workoutsPerWeek;
    }

    
    public WorkoutsPerWeek getSelectedWorkoutsPerWeek() {
        return selectedWorkoutsPerWeek;
    }

    
    public void setSelectedWorkoutsPerWeek(WorkoutsPerWeek selectedWorkoutsPerWeek) {
        this.selectedWorkoutsPerWeek = selectedWorkoutsPerWeek;
    }

    
    public Set<Employee> getSelectedCoaches() {
        return selectedCoaches;
    }

    
    public void setSelectedCoaches(Set<Employee> selectedCoaches) {
        this.selectedCoaches = selectedCoaches;
    }

    
    public List<Group> getGroups() {
        return groups;
    }

    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    
    public boolean getMultipleWorkoutPerWeek() {
        return multipleWorkoutPerWeek;
    }

    
    public void setMultipleWorkoutPerWeek(boolean multipleWorkoutPerWeek) {
        this.multipleWorkoutPerWeek = multipleWorkoutPerWeek;
    }

    
    public boolean getFirstGroupSelected() {
        return firstGroupSelected;
    }

    
    public void setFirstGroupSelected(boolean firstGroupSelected) {
        this.firstGroupSelected = firstGroupSelected;
    }

    
    public int getSelectedTimeId() {
        return selectedTimeId;
    }

    
    public void setSelectedTimeId(int selectedTimeId) {
        this.selectedTimeId = selectedTimeId;
    }

    
    public List<CoachTakenDaysModel> getCoachTakenDaysModels() {
        return coachTakenDaysModels;
    }

    
    public boolean isMultiplePoolsSameDay() {
        return multiplePoolsSameDay;
    }

}
