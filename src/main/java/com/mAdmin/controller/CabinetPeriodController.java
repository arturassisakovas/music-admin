package com.mAdmin.controller;

import com.mAdmin.enumerator.DayOfWeek;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.repository.CabinetRepository;
import com.mAdmin.repository.CabinetWeekdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Track;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.model.CabinetPeriodLazyDataModel;
import com.mAdmin.service.TrackPeriodService;
import com.mAdmin.util.ReloadUtil;
import com.mAdmin.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


@Controller
@Scope(value = "view")
@Transactional
public class CabinetPeriodController {

    
    private final StringBuilder print = new StringBuilder();

    
    private Integer k;

    
    private List<Long> selectedTracksIds = new ArrayList<>();

    
    private final List<TrackWeekday> removableTrackWeekdays = new ArrayList<>();

    
    private final List<TrackWorkingHour> removableTrackWorkingHours = new ArrayList<>();

    
    private TrackWeekday selectedTrackDay;

    
    private Track oneTrack;

    
    private List<TrackPeriod> tempTrackPeriods = new ArrayList<>();

    
    private final List<TrackWeekday> trackWeekdays = new ArrayList<>();

    
    private List<Period> periods = new ArrayList<>();

    
    private Period selectedPeriod = null;

    
    private String selectedNumbersTracks;

    
    private static final int MAX_MINUTES_BEFORE_LUNCH = 690;

    
    private static final int MIN_MINUTES_AFTER_LUNCH = 720;

    
    private static final int MAX_MINUTES_AFTER_LUNCH = 1410;

    
    private static final int MINUTES_INTERVAL = 30;

    
    private TrackPeriod trackPeriod;

    
    private Track track;
    
    private Pool pool;

    
    private Long poolId;

    
    private TrackWorkingHour trackWorkingHour;

    
    private TrackWeekday trackWeekday;

    
    private TrackWeekday selectedTrackWeekday;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private TrackPeriodService trackPeriodService;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Autowired
    private CabinetPeriodLazyDataModel model;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    private List<TrackPeriod> trackPeriods;

    
    private List<Track> tracks;

    
    private List<Pool> pools;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private LocalDate minDate;

    
    private DayOfWeek dayOfWeek;

    
    private DayOfWeek[] weekdays;

    
    private DayOfWeek selectedDayOfWeek;

    
    private List<DayOfWeek> selectedWeekdays = new ArrayList<>();

    
    private boolean trackPeriodExist = false;

    
    private boolean create;

    
    private boolean emptyPool;

    
    private boolean startHourSet = false;

    
    private boolean available = true;

    
    private boolean trackWeekdayHasData = false;

    
    private List<Integer> hoursBeforeLunch = new ArrayList<>();

    
    private List<Integer> hoursAfterLunch = new ArrayList<>();

    
    private List<TrackWorkingHour> trackWorkingHours = new ArrayList<>();

    
    private int startHour, endHour;

    
    private int index;

    
    private boolean trackPeriodEditable;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    @PostConstruct
    public void init() {
        trackPeriods = cabinetPeriodRepository.findAll();
        pools = poolRepository.findAll();
        setWeekdays(DayOfWeek.values());
        track = new Track();
        periods = periodRepository.findAll();
        Collections.reverse(periods);
        for (int i = 0; i <= MAX_MINUTES_BEFORE_LUNCH; i += MINUTES_INTERVAL) {
            hoursBeforeLunch.add(i);
        }

        for (int i = MIN_MINUTES_AFTER_LUNCH; i <= MAX_MINUTES_AFTER_LUNCH; i += MINUTES_INTERVAL) {
            hoursAfterLunch.add(i);
        }
        setTrackPeriodEditable(true);
    }

    
    public void save() throws IOException {
        k = 0;
        FacesContext context = FacesContext.getCurrentInstance();
        LocalDate startDateBefore = trackPeriod.getStartDate();
        LocalDate endDateBefore = trackPeriod.getEndDate();
        tempTrackPeriods = new ArrayList<>();
        trackPeriod.setStartDate(startDate);
        trackPeriod.setEndDate(endDate);
        

        selectedNumbersTracks = "";
        
        print.setLength(0);
        if (!create) {
            List<TrackPeriod> trackPeriodsToCheck = cabinetPeriodRepository.findByTrack(track);
            trackPeriodsToCheck.removeIf(tp -> tp.getId().equals(trackPeriod.getId()));

            if ((!startDateBefore.equals(startDate) || !endDateBefore.equals(endDate))
                    && trackPeriodService.isPeriodOverlapping(trackPeriod, trackPeriodsToCheck)) {
                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                        "track.period.overlapping", context);
                trackPeriod.setStartDate(startDateBefore);
                trackPeriod.setEndDate(endDateBefore);
            } else {
                if (selectedTracksIds == null) {
                    trackPeriods.remove(index);
                    trackPeriods.add(index, cabinetPeriodRepository.saveAndFlush(trackPeriod));
                }
            }
            trackPeriodExist = true;
            ReloadUtil.reload();
        } else {
            selectedTracksIds.forEach(sti -> {
                int size = selectedTracksIds.size();
                cabinetRepository.findById(sti).ifPresent(value -> oneTrack = value);
                if (trackPeriodService.isPeriodOverlapping(trackPeriod, cabinetPeriodRepository.findByTrack(oneTrack))) {
                    cabinetRepository.findById(sti).ifPresent(val -> track = val);

                    k++;
                    print.append(track.getNumber());
                    print.append(" ");
                    if (k == size) {
                        FacesContext ctx = FacesContext.getCurrentInstance();
                        ResourceBundle msg = ctx.getApplication().getResourceBundle(ctx, "msg");
                        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                                msg.getString("track.period.overlapping") + print.toString()));
                        setCreate(true);
                    }
                    trackPeriodExist = false;
                } else {
                    if (selectedTracksIds != null && !selectedTracksIds.isEmpty()) {
                        tempTrackPeriods.add(new TrackPeriod(cabinetRepository.getOne(sti), trackPeriod.getStartDate(),
                                trackPeriod.getEndDate()));
                        trackPeriods.add(new TrackPeriod(cabinetRepository.getOne(sti), trackPeriod.getStartDate(),
                                trackPeriod.getEndDate()));
                        trackPeriodExist = true;
                        k++;
                        cabinetRepository.findById(sti).ifPresent(val -> track = val);
                        
                        print.setLength(0);
                        print.append(track.getNumber());
                        if (k == size) {
                            print.append(" ");
                        } else {
                            print.append(", ");
                        }
                    }
                    cabinetPeriodRepository.saveAll(tempTrackPeriods);
                    this.index = trackPeriods.size() - 1;
                    selectedNumbersTracks += print.toString();
                    setCreate(false);

                }
            });
        }
        saveChangesToDatabase();
    }

    
    private void saveChangesToDatabase() {
        List<TrackWorkingHour> savableTrackWorkingHours = new ArrayList<>();

        cabinetWorkingHourRepository.deleteAll(removableTrackWorkingHours);
        cabinetWeekdayRepository.deleteAll(removableTrackWeekdays);
        cabinetWeekdayRepository.saveAll(trackWeekdays);

        for (TrackWeekday trackWeekday : trackWeekdays) {
            savableTrackWorkingHours.addAll(trackWeekday.getTrackWorkingHours());
        }

        cabinetWorkingHourRepository.saveAll(savableTrackWorkingHours);
    }

    
    public void edit(Long id, int index) {
        selectedTracksIds = null;
        removableTrackWeekdays.clear();
        removableTrackWorkingHours.clear();
        this.index = index;
        Optional<TrackPeriod> optionalTrackPeriod = cabinetPeriodRepository.findById(id);
        if (optionalTrackPeriod.isPresent()) {
            this.trackPeriod = optionalTrackPeriod.get();
            this.startDate = this.trackPeriod.getStartDate();
            this.endDate = this.trackPeriod.getEndDate();
            minDate = this.trackPeriod.getStartDate();
            this.track = this.trackPeriod.getTrack();
            this.pool = track.getPool();
            selectedWeekdays.clear();
            trackWorkingHours.clear();
            
            print.setLength(0);
            print.append(track.getNumber());
            selectedNumbersTracks = print.toString();
            List<TrackWeekday> workingDays = cabinetWeekdayRepository.findByTrackPeriodId(id);
            for (TrackWeekday trackWeekday : workingDays) {
                selectedWeekdays.add(trackWeekday.getDayOfWeek());
            }
            selectedWeekdays.sort(Comparator.naturalOrder());
            trackPeriodExist = true;
            selectedDayOfWeek = null;
            setCreate(false);
            setEmptyPool(false);
            setTrackPeriodEditable(true);
            if (!groupRepository.findByTrackPeriodId(id).isEmpty()) {
                setTrackPeriodEditable(false);
            }
        }
    }

    
    public void add() {
        trackPeriod = new TrackPeriod();
        startDate = null;
        endDate = null;
        track = null;
        poolId = null;
        selectedWeekdays.clear();
        selectedDayOfWeek = null;
        trackPeriodExist = false;
        trackWorkingHours.clear();
        setCreate(true);
        setEmptyPool(false);
        setTrackPeriodEditable(true);
        if (selectedTracksIds != null) {
            selectedTracksIds.clear();
        }
    }

    
    public void clearTrackWorkdaysData() {
        trackWeekdays.clear();
        trackWorkingHours.clear();
        removableTrackWorkingHours.clear();
        removableTrackWeekdays.clear();
    }

    
    public void delete(Long id, int index) {
        cabinetPeriodRepository.deleteById(id);
        this.index = index;
        trackPeriods.remove(index);
        trackPeriodExist = false;
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "admin.trackPeriod.deleted.success",
                FacesContext.getCurrentInstance());
    }

    
    public void setMinDate() {
        minDate = startDate;
        if (endDate != null && startDate != null) {
            if (endDate.compareTo(startDate) < 0) {
                endDate = null;
            }
        }
    }

    
    public void saveDay(DayOfWeek selectedDay) {
        setDayOfWeek(selectedDay);
        if (selectedTracksIds != null) {
            if (selectedWeekdays.contains(selectedDay)) {
                tempTrackPeriods.forEach(ttp -> selectedTrackDay = cabinetWeekdayRepository
                        .findByDayOfWeekAndTrackPeriodId(selectedDay, ttp.getId()));

                if (selectedTrackDay != null) {
                    if (!selectedTrackDay.getTrackWorkingHours().isEmpty()) {
                        trackWeekdayHasData = true;
                    } else {
                        deleteDay();
                    }
                } else {
                    deleteDay();
                }
            } else {
                selectedWeekdays.add(selectedDay);
                tempTrackPeriods.forEach(ttp -> {
                    trackWeekday = new TrackWeekday();
                    trackWeekday.setTrackPeriod(ttp);
                    trackWeekday.setDayOfWeek(selectedDay);
                    trackWeekdays.add(trackWeekday);
                });
                trackWeekdayHasData = false;
            }
            selectedWeekdays.sort(Comparator.naturalOrder());
            selectedDayOfWeek = null;
            trackWorkingHours.clear();
        } else {
            if (selectedWeekdays.contains(selectedDay)) {
                TrackWeekday selectedTrackDay = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(selectedDay,
                        trackPeriod.getId());
                if (selectedTrackDay != null) {
                    if (!selectedTrackDay.getTrackWorkingHours().isEmpty()) {
                        trackWeekdayHasData = true;
                    } else {
                        deleteDay();
                    }
                } else {
                    deleteDay();
                }
            } else {
                selectedWeekdays.add(selectedDay);
                trackWeekday = new TrackWeekday();
                trackWeekday.setTrackPeriod(trackPeriod);
                trackWeekday.setDayOfWeek(selectedDay);
                trackWeekdays.add(trackWeekday);
                trackWeekdayHasData = false;
            }
            selectedWeekdays.sort(Comparator.naturalOrder());
            selectedDayOfWeek = null;
            trackWorkingHours.clear();
        }
    }

    
    public List<TrackWorkingHour> displayTrackWorkingHours() {
        trackWorkingHours.sort(comparing(TrackWorkingHour::getStartHour));
        if (selectedTracksIds != null) {
            List<TrackWorkingHour> trackWorkingHoursNonRepeating = trackWorkingHours;
            trackWorkingHoursNonRepeating = trackWorkingHoursNonRepeating.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                    new TreeSet<>(comparing(TrackWorkingHour::getStartHour)
                            .thenComparing(TrackWorkingHour::getEndHour))), ArrayList::new));
            return trackWorkingHoursNonRepeating;
        }
        return trackWorkingHours;
    }

    
    public void deleteDay() {
        List<TrackWeekday> removableTrackWeekdaysList = new ArrayList<>();
            for (TrackWeekday trackWeekday : trackWeekdays) {
                if (trackWeekday.getDayOfWeek() == dayOfWeek) {
                    removableTrackWeekdaysList.add(trackWeekday);
                    if (trackWeekday.getId() != null) {
                        removableTrackWeekdays.add(trackWeekday);
                    }
                }
            }
            trackWeekdays.removeAll(removableTrackWeekdaysList);
            selectedWeekdays.remove(dayOfWeek);
            trackWeekdayHasData = false;
        if (removableTrackWeekdaysList.isEmpty()) {
            TrackWeekday selectedTrackDay = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(dayOfWeek,
                    trackPeriod.getId());
            if (selectedTrackDay != null) {
                removableTrackWeekdays.add(selectedTrackDay);
            }
            selectedWeekdays.remove(dayOfWeek);
            trackWeekdayHasData = false;
        }
    }

    
    public void selectTime(int minutes) {
        if (startHourSet) {
            endHour = minutes;
            startHourSet = false;
            saveTrackWorkingHour();
        } else {
            startHour = minutes;
            startHourSet = true;
        }
    }

    
    public void saveTrackWorkingHour() {
        if (selectedTracksIds != null) {
            trackWeekdays.forEach(twd -> {
                if (twd.getDayOfWeek() == selectedTrackWeekday.getDayOfWeek()) {
                    trackWorkingHour = new TrackWorkingHour();
                    trackWorkingHour.setStartHour(startHour);
                    trackWorkingHour.setEndHour(endHour);
                    trackWorkingHour.setTrackWeekday(twd);
                    twd.getTrackWorkingHours().add(trackWorkingHour);

                    trackWorkingHours.add(trackWorkingHour);
                    trackWorkingHours.sort(comparing(TrackWorkingHour::getStartHour));
                }
            });
        } else {
            trackWorkingHour = new TrackWorkingHour();
            trackWorkingHour.setStartHour(startHour);
            trackWorkingHour.setEndHour(endHour);
            trackWorkingHour.setTrackWeekday(selectedTrackWeekday);
            for (TrackWeekday trackWeekday : trackWeekdays) {
                if (trackWorkingHour.getDayOfWeek() == trackWeekday.getDayOfWeek()) {
                    trackWeekday.getTrackWorkingHours().add(trackWorkingHour);
                    trackWorkingHours.add(trackWorkingHour);
                    trackWorkingHours.sort(comparing(TrackWorkingHour::getStartHour));
                }
            }
        }
        startHour = -1;
    }

    
    public boolean containsDay(DayOfWeek day) {
        return selectedWeekdays.contains(day);
    }

    
    public String minutesToTimeConverter(int minutes) {
        return TimeUtil.minutesToTimeConverter(minutes);
    }

    
    public void selectDay(DayOfWeek dayOfWeek) {
        selectedDayOfWeek = dayOfWeek;
        startHour = -1;
        startHourSet = false;
        trackWorkingHours.clear();
        selectedTrackWeekday = null;
        for (TrackWeekday trackWeekday : trackWeekdays) {
            if (trackWeekday.getDayOfWeek() == dayOfWeek) {
                selectedTrackWeekday = trackWeekday;
                selectedTrackWeekday.setTrackWorkingHours(selectedTrackWeekday
                        .getTrackWorkingHours());
                trackWorkingHours.addAll(new ArrayList<>(selectedTrackWeekday
                        .getTrackWorkingHours()));
            }
        }
        if (selectedTrackWeekday == null) {
            trackWeekday = cabinetWeekdayRepository
                    .findByDayOfWeekAndTrackPeriodId(dayOfWeek, trackPeriod.getId());
            selectedTrackWeekday = trackWeekday;
            trackWorkingHours = new ArrayList<>(trackWeekday.getTrackWorkingHours());
                trackWeekdays.add(this.trackWeekday);
                trackWeekdayHasData = false;
        }
    }

    
    public void deleteWorkingHour(TrackWorkingHour trackWorkingHour) {
        List<TrackWorkingHour> removableTrackWorkingHours = new ArrayList<>();
        if (selectedTracksIds != null) {
            for (TrackWorkingHour twh : trackWorkingHours) {
                if (twh.getStartHour() == trackWorkingHour.getStartHour()
                && twh.getEndHour() == trackWorkingHour.getEndHour()) {
                    removableTrackWorkingHours.add(twh);
                }
            }
            trackWorkingHours.removeAll(removableTrackWorkingHours);

            for (TrackWeekday trackWeekday : trackWeekdays) {
                if (trackWorkingHour.getDayOfWeek() == trackWeekday.getDayOfWeek()) {
                    trackWeekday.setTrackWorkingHours(new HashSet<>(trackWorkingHours));
                }
            }
        } else {
            trackWorkingHours.remove(trackWorkingHour);
            if (trackWorkingHour.getId() != null) {
                this.removableTrackWorkingHours.add(trackWorkingHour);
            }
            for (TrackWeekday trackWeekday : trackWeekdays) {
                if (trackWorkingHour.getDayOfWeek() == trackWeekday.getDayOfWeek()) {
                    trackWeekday.setTrackWorkingHours(new HashSet<>(trackWorkingHours));
                }
            }
        }
    }

    
    public boolean checkAvailability(int minutes) {
        if (minutes <= startHour) {
            return true;
        }

        for (TrackWorkingHour workingHour : trackWorkingHours) {
            if (startHourSet && workingHour.getStartHour() == minutes) {
                return false;
            } else if (workingHour.getStartHour() <= minutes && startHour >= 0
                    && startHour < workingHour.getStartHour()) {
                return true;
            } else if (workingHour.getStartHour() <= minutes && minutes < workingHour.getEndHour()) {
                return true;
            }
        }
        return false;
    }

    
    public void cancelHourSelection() {
        startHourSet = false;
        startHour = -1;
    }

    
    public void tracksFiller() {
        poolRepository.findById(poolId).ifPresent(value -> pool = value);
        tracks = cabinetRepository.findByPoolId(poolId);
        if (tracks.size() == 0) {
            setEmptyPool(true);
        } else {
            setEmptyPool(false);
        }
    }

    
    public boolean checkIfGroupExistWithTrackHour(TrackWorkingHour trackWorkingHour) {
        return trackWorkingHour.getGroup() == null;
    }

    
    public boolean checkIfGroupExistWithDay(DayOfWeek dayOfWeek) {
        if (containsDay(dayOfWeek)) {
            if (selectedTracksIds != null) {
                trackPeriod = tempTrackPeriods.get(0);
            }
            TrackWeekday trackWeekday = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(dayOfWeek,
                    trackPeriod.getId());
            if (trackWeekday == null) {
                return false;
            }
            for (TrackWorkingHour trackWorkingHour : trackWeekday.getTrackWorkingHours()) {
                if (trackWorkingHour.getGroup() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public boolean checkIfGroupExistWithTrackPeriod(TrackPeriod trackPeriod) {
        return groupRepository.findByTrackPeriodId(trackPeriod.getId()).isEmpty();
    }

    
    public TrackPeriod getTrackPeriod() {
        return trackPeriod;
    }

    
    public void setTrackPeriod(TrackPeriod trackPeriod) {
        this.trackPeriod = trackPeriod;
    }

    
    public Track getTrack() {
        return track;
    }

    
    public void setTrack(Track track) {
        this.track = track;
    }

    
    public Pool getPool() {
        return pool;
    }

    
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    
    public Long getPoolId() {
        return poolId;
    }

    
    public void setPoolId(Long poolId) {
        this.poolId = poolId;
    }

    
    public List<TrackPeriod> getTrackPeriods() {
        return trackPeriods;
    }

    
    public void setTrackPeriods(List<TrackPeriod> trackPeriods) {
        this.trackPeriods = trackPeriods;
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    public LocalDate getMinDate() {
        return minDate;
    }

    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public boolean isEmptyPool() {
        return emptyPool;
    }

    
    public void setEmptyPool(boolean emptyPool) {
        this.emptyPool = emptyPool;
    }

    
    public CabinetPeriodLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(CabinetPeriodLazyDataModel model) {
        this.model = model;
    }

    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    
    public DayOfWeek[] getWeekdays() {
        return weekdays;
    }

    
    public void setWeekdays(DayOfWeek[] weekdays) {
        this.weekdays = weekdays;
    }

    
    public List<Integer> getHoursBeforeLunch() {
        return hoursBeforeLunch;
    }

    
    public void setHoursBeforeLunch(List<Integer> hoursBeforeLunch) {
        this.hoursBeforeLunch = hoursBeforeLunch;
    }

    
    public List<Integer> getHoursAfterLunch() {
        return hoursAfterLunch;
    }

    
    public void setHoursAfterLunch(List<Integer> hoursAfterLunch) {
        this.hoursAfterLunch = hoursAfterLunch;
    }

    
    public DayOfWeek getSelectedDayOfWeek() {
        return selectedDayOfWeek;
    }

    
    public void setSelectedDayOfWeek(DayOfWeek selectedDayOfWeek) {
        this.selectedDayOfWeek = selectedDayOfWeek;
    }

    
    public boolean isTrackPeriodExist() {
        return trackPeriodExist;
    }

    
    public void setTrackPeriodExist(boolean trackPeriodExist) {
        this.trackPeriodExist = trackPeriodExist;
    }

    
    public int getStartHour() {
        return startHour;
    }

    
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    
    public int getEndHour() {
        return endHour;
    }

    
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    
    public boolean isStartHourSet() {
        return startHourSet;
    }

    
    public void setStartHourSet(boolean startHourSet) {
        this.startHourSet = startHourSet;
    }

    
    public List<TrackWorkingHour> getTrackWorkingHours() {
        return trackWorkingHours;
    }

    
    public void setTrackWorkingHours(List<TrackWorkingHour> trackWorkingHours) {
        this.trackWorkingHours = trackWorkingHours;
    }

    
    public TrackWeekday getSelectedTrackWeekday() {
        return selectedTrackWeekday;
    }

    
    public void setSelectedTrackWeekday(TrackWeekday selectedTrackWeekday) {
        this.selectedTrackWeekday = selectedTrackWeekday;
    }

    
    public List<DayOfWeek> getSelectedWeekdays() {
        return selectedWeekdays;
    }

    
    public void setSelectedWeekdays(List<DayOfWeek> selectedWeekdays) {
        this.selectedWeekdays = selectedWeekdays;
    }

    
    public boolean isAvailable() {
        return available;
    }

    
    public void setAvailable(boolean available) {
        this.available = available;
    }

    
    public boolean getTrackWeekdayHasData() {
        return trackWeekdayHasData;
    }

    
    public void setTrackWeekdayHasData(boolean trackWeekdayHasData) {
        this.trackWeekdayHasData = trackWeekdayHasData;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

    
    public List<Track> getTracks() {
        return tracks;
    }

    
    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    
    public List<Period> getPeriods() {
        return periods;
    }

    
    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    
    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

    
    public List<Long> getSelectedTracksIds() {
        return selectedTracksIds;
    }

    
    public void setSelectedTracksIds(List<Long> selectedTracksIds) {
        this.selectedTracksIds = selectedTracksIds;
    }

    
    public String getSelectedNumbersTracks() {
        return selectedNumbersTracks;
    }

    
    public void setSelectedNumbersTracks(String selectedNumbersTracks) {
        this.selectedNumbersTracks = selectedNumbersTracks;
    }

    
    public boolean isTrackPeriodEditable() {
        return trackPeriodEditable;
    }

    
    public void setTrackPeriodEditable(boolean trackPeriodEditable) {
        this.trackPeriodEditable = trackPeriodEditable;
    }
}
