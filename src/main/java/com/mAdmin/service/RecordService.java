package com.mAdmin.service;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Record;

import java.util.List;
import java.util.Optional;


public interface RecordService {

    
    List<Record> getAll();

    
    Optional<Record> getById(Long id);

    
	List<Record> getByClient(Client client);

}
