package com.mAdmin.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.entity.Attendee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.GroupWorkout;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.TrackWorkingHour;


@Service
public class GroupWorkoutServiceImpl implements GroupWorkoutService {

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    private int lessonCount;

    @Override
    public List<GroupWorkout> getAllGroupWorkouts(LocalDate startDate, LocalDate endDate,
            List<PoolNonWorkday> nonWorkdays, Set<TrackWorkingHour> trackWorkingHours, Group group) {

        List<LocalDate> allDates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
        List<LocalDate> workingDays = new ArrayList<>();
        List<GroupWorkout> groupWorkdays = new ArrayList<>();

        for (LocalDate date : allDates) {
            for (TrackWorkingHour workingHour : trackWorkingHours) {
                if (date.getDayOfWeek().toString().equals(workingHour.getDayOfWeek().toString())) {
                    workingDays.add(date);
                }
            }
        }

        for (PoolNonWorkday date : nonWorkdays) {
            workingDays.remove(date.getDate());
        }

        for (LocalDate date : workingDays) {
            GroupWorkout groupWorkout = new GroupWorkout(group, date);
            groupWorkdays.add(groupWorkout);
        }

        return groupWorkdays;

    }

    @Override
    public Map<Month, List<LocalDate>> getGroupWorkoutsDates(Long groupId, LocalDate startDate, LocalDate endDate,
            int subscriptionMonths) {

        List<LocalDate> groupWorkoutsDates = new ArrayList<>();
        List<GroupWorkout> groupWorkouts = groupWorkoutRepository.findByGroupIdAndDateBetween(groupId, startDate,
                endDate);
        Month currentMonth = null;
        Map<Month, List<LocalDate>> groupWorkoutsMap = new LinkedHashMap<>(subscriptionMonths);

        groupWorkouts.forEach(gw -> groupWorkoutsDates.add(gw.getDate()));

        for (LocalDate date : groupWorkoutsDates) {
            if (!date.getMonth().equals(currentMonth)) {

                List<LocalDate> monthDates = new ArrayList<>();
                currentMonth = date.getMonth();

                for (LocalDate d : groupWorkoutsDates) {
                    if (d.getMonth().equals(currentMonth)) {
                        monthDates.add(d);
                    }
                }

                monthDates = monthDates.stream().distinct().collect(Collectors.toList());
                groupWorkoutsMap.put(currentMonth, monthDates);
            }
        }

        return groupWorkoutsMap;
    }

    @Override
    public int getLessonCount(Attendee attendee, LocalDate endDate, LocalDate startDate) {

        List<GroupWorkout> groupWorkouts = groupWorkoutRepository
                .findGroupWorkoutsByGroupId(attendee.getGroup());

        lessonCount = 0;
        groupWorkouts.forEach(gw -> {
            if ((gw.getDate().isAfter(startDate) || gw.getDate().isEqual(startDate))
                    && ((gw.getDate().isBefore(endDate)) || (gw.getDate().isEqual(endDate)))) {
                lessonCount++;
            }
        });

        return lessonCount;
    }
}
