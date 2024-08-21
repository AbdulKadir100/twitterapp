package com.kadir.abdul.Twitter_App.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kadir.abdul.Twitter_App.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "select m from Message m where m.uid=:uid")
    CompletableFuture<List<Message>> listMessageByProducerId(@Param("uid") Long uid);
    // @Query(value="select * from T_messages where uid in (:uid)")
    // Flux<Message> listMessageByProducerIdIn(@Param("uid") List<Integer> uid);

}
