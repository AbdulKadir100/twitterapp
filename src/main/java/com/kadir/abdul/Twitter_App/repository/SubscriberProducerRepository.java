package com.kadir.abdul.Twitter_App.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadir.abdul.Twitter_App.entity.SubscriberProducer;

@Repository
public interface SubscriberProducerRepository extends JpaRepository<SubscriberProducer, Long> {
    @Query(value = "select producerId from SubscriberProducer  where subscriberId=:subscriberId")
    List<Long> listProducerBySubscriber(@Param("subscriberId") Long subscriberId);

}
