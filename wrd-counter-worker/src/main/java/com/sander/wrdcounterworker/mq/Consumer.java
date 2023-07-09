package com.sander.wrdcounterworker.mq;

import com.google.gson.Gson;
import com.sander.wrdcounterworker.dto.*;
import com.sander.wrdcounterworker.repository.WordRepository;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.sander.wrdcounterworker.tools.JsonCleaner;
import smile.nlp.dictionary.EnglishStopWords;

import java.util.*;

@Controller
@Component
public class Consumer {
    @Autowired
    WordRepository wordRepository;
    public static final String QUEUE = "wrd-counter-queue";
    @RabbitListener(queues = QUEUE)
    public void consumeMessage(String message) {
        Gson gson = new Gson();
        Words wordsObj;

        //Clean up JSON
        String cleanMessage = JsonCleaner.removeQuotesAndUnescape(message);
        //Create MQData object from JSON
        MQData mqData = gson.fromJson(cleanMessage, MQData.class);
        //Get variables from MQData
        FileData fileData = mqData.fileData;
        Boolean lastMsg = mqData.lastMsg;
        ProcessingFlags processingFlags = mqData.processingFlags;
        String fileString = fileData.getFileContent();
        String id = fileData.getId();

        //Clean file contents
        fileString = fileString.replaceAll("[^a-zA-Z]", " ");
        fileString = fileString.toLowerCase();

        //Check for existing word data in DB
        Optional<WordData> existingWordData = wordRepository.findById(id);
        if (existingWordData.isPresent()) {
            System.out.println("Got existing data from DB: " + existingWordData.get().getWords());
            wordsObj = gson.fromJson(existingWordData.get().getWords(), Words.class);
        } else {
            System.out.println("No entry by this UUID yet");
            wordsObj = new Words();
        }

        //Tokenize text and send each token to MQ
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument doc = new CoreDocument(fileString);
        pipeline.annotate(doc);
        for (CoreLabel token: doc.tokens()) {
            if(EnglishStopWords.DEFAULT.contains(token.word()) && processingFlags.ignoreStopWords) continue; //Pass stopword if user has chosen to filter out stopwords
            Word existingWord = wordsObj.findByWord(token.word());
            if(existingWord != null) {
                wordsObj.updateCountByWord(token.word());
            } else {
                wordsObj.add(new Word(token.word(), 1));
            }
        }
        if(processingFlags.ignoreOutliers && lastMsg) { //Remove outliers from the end result
            Optional<WordData> wordData = wordRepository.findById(id);
            Words tempWords = new Words();
            if(wordData.isPresent()) {
                wordsObj = gson.fromJson(wordData.get().getWords(), Words.class);
                if (wordsObj != null) {
                    for (Word word: wordsObj.words) {
                        if (word.getCount() > 3) tempWords.add(word);
                    }
                    wordsObj = tempWords;
                }
            } else if (wordsObj != null) {
                for (Word word: wordsObj.words) {
                    if (word.getCount() > 3) tempWords.add(word);
                }
                wordsObj = tempWords;
            }
        }
        System.out.println("Message received and processed!");
        wordRepository.save(new WordData(id, gson.toJson(wordsObj)));
    }
}
