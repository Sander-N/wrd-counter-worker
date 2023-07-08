package com.sander.wrdcounterworker.mq;

import com.google.gson.Gson;
import com.sander.wrdcounterworker.dto.FileData;
import com.sander.wrdcounterworker.dto.Word;
import com.sander.wrdcounterworker.dto.WordData;
import com.sander.wrdcounterworker.dto.Words;
import com.sander.wrdcounterworker.repository.WordRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.sander.wrdcounterworker.tools.JsonCleaner;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Controller
@Component
public class Consumer {
    @Autowired
    WordRepository wordRepository;
    @RabbitListener(queues = "wrd-counter-queue")
    public void consumeMessage(String message) {
        Gson gson = new Gson();
        Words wordsObj;

        String cleanMessage = JsonCleaner.removeQuotesAndUnescape(message);
        FileData fileData = gson.fromJson(cleanMessage, FileData.class);

        //Check for existing word data in DB
        Optional<WordData> existingWordData = wordRepository.findById(fileData.getId());
        if (existingWordData.isPresent()) {
            System.out.println("Got existing data from DB: " + existingWordData.get().getWords());
            wordsObj = gson.fromJson(existingWordData.get().getWords(), Words.class);
        } else {
            System.out.println("No entry by this UUID yet");
            wordsObj = new Words();
        }

        if(message == null || message.isEmpty()) {
            System.out.println("Empty message");
        } else {
            StringTokenizer tokens = new StringTokenizer(fileData.getFileContent());
            int count = tokens.countTokens();
            while(tokens.hasMoreTokens()) {
                String cleanWord = tokens.nextToken();
                if (cleanWord != null) {
                    cleanWord = cleanWord.replaceAll("\\s+", "");
                    cleanWord = cleanWord.replaceAll("[^a-zA-Z0-9_ ]", "");
                    cleanWord = cleanWord.trim();
                    cleanWord = cleanWord.toLowerCase();
                    Word existingWord = wordsObj.findByWord(cleanWord);
                    if(existingWord != null) {
                        wordsObj.updateCountByWord(cleanWord);
                    } else {
                        wordsObj.add(new Word(cleanWord, 1));
                    }
                }
            }
        System.out.println("Message received and processed!");
        wordRepository.save(new WordData(fileData.getId(), gson.toJson(wordsObj)));}
    }
}
