package com.mAdmin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Record;
import com.mAdmin.enumerator.RecordStatus;


public interface RecordRepository extends JpaRepository<Record, Long> {

    
    @Query("select r from Record r where r.attendee = ?1 and (r.status = 'COMPLETED' or r.status = null)")
    List<Record> findRecordsByAttendee(Attendee attendee);

    
    Page<Record> findByClientAndStatusOrderByCreatedAtDesc(Client client, RecordStatus status, Pageable pageable);

    
    Page<Record> findByStatusNotOrderByCreatedAtDesc(RecordStatus status, Pageable pageable);

    
    @Query("select r from Record r where r.status != 'COMPLETED' and r.status != null")
    List<Record> findAllTasks();

    
    Page<Record> findByStatusNot(RecordStatus status, Pageable pageable);


    
    Page<Record> findByClientAndStatus(Client client, RecordStatus status, Pageable pageable);
}
