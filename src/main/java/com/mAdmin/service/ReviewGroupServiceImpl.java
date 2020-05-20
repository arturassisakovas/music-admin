package com.mAdmin.service;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ReviewGroupServiceImpl implements ReviewGroupService {

    
    @Autowired
    private ContractRepository contractRepository;

    @Override
    public Boolean isContractSignedInGroupWorkoutTime(Attendee attendee, Group group, boolean abilityToChangeGroup) {

        LocalDate startDate = group.getStartDate();
        LocalDate endDate = group.getEndDate();

        List<Contract> attendeesContractsInCurrentGroupPeriod = contractRepository
                .findByValidToAfterAndValidFromBeforeAndAttendeeOrderByIdDesc(startDate, endDate, attendee);
        Group attendeesNewGroup = attendee.getNewGroup();

        for (Contract contract : attendeesContractsInCurrentGroupPeriod) {
            if (contract.getType() == ContractType.ACTIVE && (attendeesNewGroup == null
                    || !attendeesNewGroup.getId().equals(group.getId()))) {
                return true;
            }
        }
        if (!abilityToChangeGroup) {
            Contract pastActiveContract = contractRepository
                    .findTopByAttendeeAndTypeOrderByIdDesc(attendee, ContractType.ACTIVE);
            if (pastActiveContract != null) {
                return null;
            }
        }
        return false;
    }
}
