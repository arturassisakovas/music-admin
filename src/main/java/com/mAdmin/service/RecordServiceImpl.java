package com.mAdmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Record;


@Service
public class RecordServiceImpl implements RecordService {

    
    @Autowired
    private RecordRepository recordRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Override
    public List<Record> getAll() {

        return recordRepository.findAll();

    }

    
    @Override
    public Optional<Record> getById(Long id) {

        return recordRepository.findById(id);

    }

    
    @Override
    public List<Record> getByClient(Client client) {

        List<Record> result = new ArrayList<Record>();
        List<Attendee> attendees = attendeeRepository.findByClient(client);
        for (Attendee attendee : attendees) {
        List<Record> records = recordRepository.findRecordsByAttendee(attendee);
            if (records != null) {
                result.addAll(records);
            }
        }
        return result;

    }
}
