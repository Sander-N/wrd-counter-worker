package com.sander.wrdcounterworker.dto;

import com.sander.wrdcounterworker.repository.WordRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "word_data")
public class WordData {
    @Id
    private String id;
    private String word;

    public WordData(){}

    public WordData(String id, String word) {
        this.id = id;
        this.word = word;
    }
}
