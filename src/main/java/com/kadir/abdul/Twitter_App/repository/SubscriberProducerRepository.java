package com.kadir.abdul.Twitter_App.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadir.abdul.Twitter_App.entity.SubscriberProducer;
import com.kadir.abdul.Twitter_App.response.ApiResponse;

@Repository
public interface SubscriberProducerRepository extends JpaRepository<SubscriberProducer, Long> {
    
    @Query(value = "select producerId from SubscriberProducer  where subscriberId=:subscriberId")
    CompletableFuture<List<Long>> listProducerBySubscriber(@Param("subscriberId") Long subscriberId);

    @Query(value = "select * from SubscriberProducer where subscriberId=:subscriberId and producerId=:producerId", nativeQuery = true)
    CompletableFuture<ApiResponse<SubscriberProducer>> checkSubscriptionStatus(@Param("subscriberId") Long subscriberId,
                                               @Param("producerId") Long producerId);
}
