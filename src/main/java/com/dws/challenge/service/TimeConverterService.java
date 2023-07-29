package com.dws.challenge.service;

import org.springframework.stereotype.Service;

@Service
public class TimeConverterService {

    private static final String[] NUMBER_WORDS = {
        "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static final String[] TENS_WORDS = {
        "", "", "twenty", "thirty", "forty", "fifty"
    };

    public String convertTimeToWords(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Invalid time format.");
        }

        if (hours == 0 && minutes == 0) {
            return "It's Midnight";
        }

        if (hours == 12 && minutes == 0) {
            return "It's Midday";
        }

        String hourWord = NUMBER_WORDS[hours <= 12 ? hours : hours - 12];
        String minuteWord = convertToWords(minutes);

        String period = hours < 12 ? "AM" : "PM";

        if (hours == 0 || hours == 12) {
            return String.format("It's twelve %s %s", minuteWord, period);
        }
        return String.format("It's %s %s %s", hourWord, minuteWord, period);
    }

    private String convertToWords(int number) {
        if (number == 0) {
            return "o'clock";
        } else if (number < 20) {
            return NUMBER_WORDS[number];
        } else {
            int tensDigit = number / 10;
            int onesDigit = number % 10;
            return TENS_WORDS[tensDigit] + (onesDigit != 0 ? " " + NUMBER_WORDS[onesDigit] : "");
        }
    }
}
