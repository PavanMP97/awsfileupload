package com.aws.awsfileupload.repository;

import com.aws.awsfileupload.domain.Status;
import com.aws.awsfileupload.domain.Tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TrackerRepository extends JpaRepository<Tracker, Long> {

    @Transactional
    @Modifying
    @Query(value = "update Tracker t  set t.status=?1 where t.trackerId=?2")
    void updateStatusById(Status status, Long trackerId);


}
