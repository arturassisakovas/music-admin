package com.mAdmin.service;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Group;


public interface ReviewGroupService {

    
    Boolean isContractSignedInGroupWorkoutTime(Attendee attendee, Group group, boolean abilityToChangeGroup);
}
