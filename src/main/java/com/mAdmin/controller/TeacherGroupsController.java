package com.mAdmin.controller;

import com.mAdmin.enumerator.DayOfWeek;
import com.mAdmin.repository.AttendanceRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.repository.CabinetWeekdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Attendance;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.TrackWeekday;
import com.mAdmin.entity.TrackWorkingHour;
import com.mAdmin.model.GroupOfTheDay;
import com.mAdmin.security.EmployeePrincipal;
import com.mAdmin.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


@Controller
@Scope("session")
public class TeacherGroupsController {

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    @Autowired
    private AttendanceRepository attendanceRepository;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    private Map<String, Object> sessionMap;

    
    private Employee coach;

    
    private Long coachId;

    
    private LocalDate selectedDate;

    
    private LocalDate firstStartDate, lastEndDate;

    
    private List<GroupOfTheDay> groupsOfTheDay;

    
    private String workoutsTime;

    
    private Integer workoutHour;

    
    private boolean showCalendar = true;

    
    private int weekDay;

    
    private DayOfWeek dayOfWeek;

    
    private Long trackPeriodId;

    
    private TrackWeekday trackWeekday;

    
    private TrackWorkingHour trackWorkingHour;

    
    private String formattedUnmarkedDaysOfCoach;

    
    @PostConstruct
    public void init() {
        formattedUnmarkedDaysOfCoach = checkUnmarkedAttendance();
    }

    
    public void onLoad() {
        FacesContext context = FacesContext.getCurrentInstance();
        coachId = ((EmployeePrincipal) authenticationFacade.getAuthentication().getPrincipal()).getEmployeeId();
        coach = employeeRepository.getOne(coachId);
        workoutHour = null;

        if (!coach.getGroups().isEmpty()) {
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }
            groupsOfTheDay = getListOfGroupsByDateAndCoach();
            noGroupsMessage();
            List<Group> coachGroupsList = new ArrayList<>(coach.getGroups());
            coachGroupsList.sort(Comparator.comparing(Group::getStartDate));
            firstStartDate = coachGroupsList.get(0).getStartDate();
            coachGroupsList.sort(Comparator.comparing(Group::getEndDate));
            lastEndDate = coachGroupsList.get(coachGroupsList.size() - 1).getEndDate();
        } else {
            showCalendar = false;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN,
                    "coach.panel.attendees.noGroups", context);
        }
    }

    
    public void setDate() {
        groupsOfTheDay = getListOfGroupsByDateAndCoach();
        noGroupsMessage();
    }

    
    public List<Attendee> attendeesByGroupAndWorkoutDay(Group group, LocalDate workoutDate) {
        return attendeeRepository.findByGroupAndWorkoutStartDateLessThanQuery(group, workoutDate);
    }

    
    public int attendanceCountForSpecificDayAndWorkoutHour(Group group, LocalDate workoutDate) {
        return attendanceRepository.countByGroupAndWorkoutDateAndWorkoutHour(group, workoutDate, workoutHour);
    }

    
    public boolean determineOutputTypeForAttendeesCount() {
        if (selectedDate.isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }

    
    public String getWorkoutsTime(GroupOfTheDay groupOfTheDay) {

        workoutHour = getWorkoutStartHour(groupOfTheDay);

        TrackWorkingHour trackWorkingHour = groupOfTheDay.getTrackWorkingHour();

        constructWorkoutsTime(trackWorkingHour);

        return workoutsTime;
    }

    
    public Integer sortByWorkoutsTime(GroupOfTheDay groupOfTheDay) {
        if (groupOfTheDay != null) {
            workoutHour = getWorkoutStartHour(groupOfTheDay);
            return workoutHour;
        } else {
            return null;
        }
    }

    
    private Integer getWorkoutStartHour(GroupOfTheDay groupOfTheDay) {

        weekDay = selectedDate.getDayOfWeek().getValue() - 1;
        dayOfWeek = DayOfWeek.values()[weekDay];
        trackPeriodId = groupOfTheDay.getGroup().getTrackPeriod().getId();
        trackWeekday = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(dayOfWeek, trackPeriodId);

        TrackWorkingHour trackWorkingHour = groupOfTheDay.getTrackWorkingHour();

        workoutHour = trackWorkingHour.getStartHour();

        return workoutHour;

    }

    
    public void noGroupsMessage() {
        if (groupsOfTheDay.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "",
                    MessageFormat.format(msg.getString("coach.panel.groups.noDailyGroups"), selectedDate)));
        }
    }

    
    public String checkUnmarkedAttendance() {
        coachId = ((EmployeePrincipal) authenticationFacade.getAuthentication().getPrincipal()).getEmployeeId();
        coach = employeeRepository.getOne(coachId);
        List<Attendee> attendeesOfGivenCoach = attendeeRepository.findByCoachOrderByFirstWorkoutDateAscending(coach);

        if (attendeesOfGivenCoach.isEmpty()) {
            return "";
        }

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<GroupWorkout> workoutDatesOfCoach = new ArrayList<GroupWorkout>();
        List<String> unmarkedGroupWorkouts = new ArrayList<String>();
        StringBuilder allUnmarkedDatesAsString = new StringBuilder();
        StringBuilder fullErrorMessage = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();
        Attendee attendeeWithEarliestWorkout = attendeeRepository.findByCoachOrderByFirstWorkoutDateAscending(coach)
                .get(0);
        LocalDate firstWorkoutOfCoachDate = attendeeWithEarliestWorkout.getWorkoutStartDate();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
        workoutDatesOfCoach.addAll(groupWorkoutRepository.findOlderWorkoutDatesByCoachId(firstWorkoutOfCoachDate,
                coach.getId(), yesterday));

        for (int i = 0; i < workoutDatesOfCoach.size(); i++) {
            GroupWorkout tempGroupWorkout = workoutDatesOfCoach.get(i);
            LocalDate date = tempGroupWorkout.getDate();
            Group tempGroup = tempGroupWorkout.getGroup();
            if (checkIfUnmarked(tempGroup, date)
                    && (unmarkedGroupWorkouts.isEmpty()
                            || !unmarkedGroupWorkouts.contains(tempGroupWorkout.getDate().toString()))
                    && attendeesByGroupAndWorkoutDay(tempGroup, date).size() > 0) {
                unmarkedGroupWorkouts.add(tempGroupWorkout.getDate().toString());
            }
        }

        if (!unmarkedGroupWorkouts.isEmpty()) {
            Collections.sort(unmarkedGroupWorkouts);
        }
        for (int i = 0; i < unmarkedGroupWorkouts.size(); i++) {
            if (i < unmarkedGroupWorkouts.size() - 1) {
                allUnmarkedDatesAsString.append(unmarkedGroupWorkouts.get(i) + ", ");
            } else {
                allUnmarkedDatesAsString.append(unmarkedGroupWorkouts.get(i));
            }
        }
        if (unmarkedGroupWorkouts.size() > 0 || !unmarkedGroupWorkouts.isEmpty()) {
            if (unmarkedGroupWorkouts.size() == 1) {
                String msgLabel = msg.getString("coach.panel.groups.unmarkedAttendee.oneDay");
                fullErrorMessage.append(MessageFormat.format(msgLabel, allUnmarkedDatesAsString));
            } else if (unmarkedGroupWorkouts.size() > 1) {
                String msgLabel = msg.getString("coach.panel.groups.unmarkedAttendee.severalDays");
                fullErrorMessage.append(MessageFormat.format(msgLabel, allUnmarkedDatesAsString));
            }
        }
        return fullErrorMessage.toString();
    }

    
    public boolean checkIfUnmarked(Group group, LocalDate date) {
        weekDay = date.getDayOfWeek().getValue() - 1;
        dayOfWeek = DayOfWeek.values()[weekDay];
        trackPeriodId = group.getTrackPeriod().getId();
        trackWeekday = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(dayOfWeek, trackPeriodId);
        List<TrackWorkingHour> trackWorkingHours = cabinetWorkingHourRepository.findByGroupAndTrackWeekday(group,
                trackWeekday);

        List<Attendance> markedAttendance = new ArrayList<>();
        boolean markedAttendanceFlag = false;
        int markedWorkoutsCount = 0;

        for (TrackWorkingHour trackWorkingHour : trackWorkingHours) {

            workoutHour = trackWorkingHour.getStartHour();
            markedAttendance
                    .addAll(attendanceRepository.findByGroupAndWorkoutDateAndWorkoutHour(group, date, workoutHour));
            markedAttendanceFlag = markedAttendance.stream().anyMatch(a -> a.getWorkoutHour() == workoutHour);

            if (markedAttendanceFlag) {
                markedWorkoutsCount++;
            }

        }

        LocalDateTime startTime = date.atStartOfDay().plusMinutes(trackWorkingHours.get(0).getStartHour());

        boolean unmarked = trackWorkingHours.size() > markedWorkoutsCount;

        return !startTime.isAfter(LocalDateTime.now()) && unmarked;
    }

    
    public String markAttendance(GroupOfTheDay groupOfTheDay) {
        workoutHour = getWorkoutStartHour(groupOfTheDay);

        TrackWorkingHour trackWorkingHour = groupOfTheDay.getTrackWorkingHour();
        constructWorkoutsTime(trackWorkingHour);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        sessionMap = externalContext.getSessionMap();
        sessionMap.put("group", groupOfTheDay.getGroup());
        sessionMap.put("workoutsDate", selectedDate);
        sessionMap.put("workoutTime", workoutsTime);
        sessionMap.put("workoutHour", workoutHour);
        return "/teacher/attendance?faces-redirect=true";
    }

    
    public boolean checkIfMarked(GroupOfTheDay groupOfTheDay) {

        TrackWorkingHour trackWorkingHour = groupOfTheDay.getTrackWorkingHour();

        String startHour = TimeUtil.minutesToTimeConverter(groupOfTheDay.getWorkoutHour());
        constructWorkoutsTime(startHour, trackWorkingHour);
        LocalDateTime startTime = selectedDate.atStartOfDay().plusMinutes(trackWorkingHour.getStartHour());
        List<Attendance> markedAttendance = attendanceRepository.findByGroupAndWorkoutDateAndWorkoutHour(
                groupOfTheDay.getGroup(), selectedDate, groupOfTheDay.getWorkoutHour());
        return !startTime.isAfter(LocalDateTime.now()) && markedAttendance.isEmpty();
    }

    
    public List<GroupOfTheDay> getListOfGroupsByDateAndCoach() {

        List<GroupOfTheDay> groupsOfTheDay = new ArrayList<>();
        List<Group> groups = groupRepository.findByCoachIdAndWorkoutDate(coachId, selectedDate);

        for (Group g : groups) {

            weekDay = selectedDate.getDayOfWeek().getValue() - 1;
            dayOfWeek = DayOfWeek.values()[weekDay];
            trackPeriodId = g.getTrackPeriod().getId();
            trackWeekday = cabinetWeekdayRepository.findByDayOfWeekAndTrackPeriodId(dayOfWeek, trackPeriodId);
            List<TrackWorkingHour> workingHours = cabinetWorkingHourRepository.findByGroupAndTrackWeekday(g,
                    trackWeekday);

            for (TrackWorkingHour w : workingHours) {
                workoutHour = w.getStartHour();
                constructWorkoutsTime(w);

                LocalDateTime startTime = selectedDate.atStartOfDay().plusMinutes(w.getStartHour());

                GroupOfTheDay group = new GroupOfTheDay();
                group.setGroup(g);
                group.setLessonTime(workoutsTime);
                group.setWorkoutHour(w.getStartHour());
                group.setTrackWorkingHour(w);
                groupsOfTheDay.add(group);
            }
        }

        return groupsOfTheDay;
    }

    
    public void constructWorkoutsTime(String startHour, TrackWorkingHour trackWorkingHour) {
        String endHour = TimeUtil.minutesToTimeConverter(trackWorkingHour.getEndHour());
        workoutsTime = startHour + " - " + endHour;
    }

    
    public void constructWorkoutsTime(TrackWorkingHour trackWorkingHour) {
        String endHour = TimeUtil.minutesToTimeConverter(trackWorkingHour.getEndHour());
        workoutsTime = TimeUtil.minutesToTimeConverter(workoutHour) + " - " + endHour;
    }

    
    public boolean isAttendanceDisable(GroupOfTheDay groupOfTheDay) {
        TrackWorkingHour trackWorkingHour = groupOfTheDay.getTrackWorkingHour();
        LocalDateTime startTime = selectedDate.atStartOfDay().plusMinutes(trackWorkingHour.getStartHour());
        return startTime.isAfter(LocalDateTime.now());
    }

    
    public Employee getCoach() {
        return coach;
    }

    
    public void setCoach(Employee coach) {
        this.coach = coach;
    }

    
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    
    public String getFormattedUnmarkedDaysOfCoach() {
        return formattedUnmarkedDaysOfCoach;
    }

    
    public void setFormattedUnmarkedDaysOfCoach(String formattedUnmarkedDaysOfCoach) {
        this.formattedUnmarkedDaysOfCoach = formattedUnmarkedDaysOfCoach;
    }

    
    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    
    public List<GroupOfTheDay> getGroupsOfTheDay() {
        return groupsOfTheDay;
    }

    
    public void setGroupsOfTheDay(List<GroupOfTheDay> groupsOfTheDay) {
        this.groupsOfTheDay = groupsOfTheDay;
    }

    
    public LocalDate getFirstStartDate() {
        return firstStartDate;
    }

    
    public void setFirstStartDate(LocalDate firstStartDate) {
        this.firstStartDate = firstStartDate;
    }

    
    public LocalDate getLastEndDate() {
        return lastEndDate;
    }

    
    public void setLastEndDate(LocalDate lastEndDate) {
        this.lastEndDate = lastEndDate;
    }

    
    public String getWorkoutsTime() {
        return workoutsTime;
    }

    
    public void setWorkoutsTime(String workoutsTime) {
        this.workoutsTime = workoutsTime;
    }

    
    public int getWorkoutHour() {
        return workoutHour;
    }

    
    public void setWorkoutHour(Integer workoutHour) {
        this.workoutHour = workoutHour;
    }

    
    public boolean isShowCalendar() {
        return showCalendar;
    }

    
    public void setShowCalendar(boolean showCalendar) {
        this.showCalendar = showCalendar;
    }

    
    public int getWeekDay() {
        return weekDay;
    }

    
    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    
    public Long getTrackPeriodId() {
        return trackPeriodId;
    }

    
    public void setTrackPeriodId(Long trackPeriodId) {
        this.trackPeriodId = trackPeriodId;
    }

    
    public TrackWeekday getTrackWeekday() {
        return trackWeekday;
    }

    
    public void setTrackWeekday(TrackWeekday trackWeekday) {
        this.trackWeekday = trackWeekday;
    }

    
    public TrackWorkingHour getTrackWorkingHour() {
        return trackWorkingHour;
    }

    
    public void setTrackWorkingHour(TrackWorkingHour trackWorkingHour) {
        this.trackWorkingHour = trackWorkingHour;
    }

}
