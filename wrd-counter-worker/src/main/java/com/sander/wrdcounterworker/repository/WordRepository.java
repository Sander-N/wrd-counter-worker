package com.sander.wrdcounterworker.repository;

import com.sander.wrdcounterworker.dto.WordData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<WordData, String> {
}
