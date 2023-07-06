package com.sander.wrdcounterworker.mq;

import com.google.gson.Gson;
import com.sander.wrdcounterworker.dto.FileData;
import com.sander.wrdcounterworker.dto.WordData;
import com.sander.wrdcounterworker.repository.WordRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.sander.wrdcounterworker.tools.JsonCleaner;

import java.util.StringTokenizer;

@Controller
@Component
public class Consumer {
    @Autowired
    WordRepository wordRepository;
    @RabbitListener(queues = "wrd-counter-queue")
    public void consumeMessage(String message) {
        String cleanMessage = JsonCleaner.removeQuotesAndUnescape(message);
        Gson gson = new Gson();
        FileData fileData = gson.fromJson(cleanMessage, FileData.class);
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
                    System.out.println("Token: " + cleanWord);
                    wordRepository.save(new WordData(fileData.getId(), cleanWord));
                }
            }
            System.out.println("Message received with word count: " + count);
        }
    }
}
