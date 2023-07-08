package com.sander.wrdcounterworker.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

@Entity
@Data
@Table(name = "word_data")
public class WordData {
    @Id
    @Column(columnDefinition = "varchar(36)")
    private String id;
    @Column(columnDefinition = "JSON")
    private String words;

    public WordData(){}

    public WordData(String id, String words) {
        this.id = id;
        this.words = words;
    }

    public String getId() {
        return id;
    }

    public String getWords() {
        return words;
    }
}
