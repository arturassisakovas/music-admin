package com.mAdmin.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.mAdmin.enumerator.WorkoutsPerWeek;


@Converter(autoApply = true)
public class LessonsPerWeekConverter implements AttributeConverter<WorkoutsPerWeek, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WorkoutsPerWeek workouts) {
        if (workouts == null) {
            return null;
        } else {
            return workouts.getValue();
        }
    }

    @Override
    public WorkoutsPerWeek convertToEntityAttribute(Integer dbData) {
        return WorkoutsPerWeek.values()[dbData - 1];
    }

}
