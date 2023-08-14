package com.dws.challenge.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dws.challenge.service.TimeConverterService;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/timeConverter")
@Slf4j
public class TimeConverterController {

    private final com.dws.challenge.service.TimeConverterService timeConverterService;

    @Autowired
    public TimeConverterController(TimeConverterService timeConverterService) {
        this.timeConverterService = timeConverterService;
    }

    @GetMapping("/getCurrentTime")
    public ResponseEntity<String> convertTime() {
        try {
        	String currTime = String.valueOf(LocalDateTime.now().getHour()).concat(":").concat(String.valueOf(LocalDateTime.now().getMinute()));
            String result = timeConverterService.convertTimeToWords(currTime);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid time format.");
        }
    }
    
    @PostMapping("/getCurrentTime2")
    public ResponseEntity<String> convertTime2(@RequestBody String time ) {
        try {
        	log.info("input time : " + time);
            String result = timeConverterService.convertTimeToWords(time);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid time format.");
        }
    }
}
