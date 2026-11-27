package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotFoundInDictionaryException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleDictionary {
    private List<String> words;
    private Random random = new Random();
    private final PrintWriter log;

    public WordleDictionary(List<String> words, PrintWriter log) {
        this.words = new ArrayList<>(words);
        this.log = log;
    }

    public String getRandomWord() {
        String word = words.get(random.nextInt(words.size()));
        log.println("Выбрано слово: " + word);
        return word;
    }

    public static String normalizeWord(String word) {
        return word.toLowerCase().replace('ё', 'е').trim();
    }

    public boolean containsWord(String word) throws WordNotFoundInDictionaryException {
        if (word == null) {
            return false;
        }
        log.println("Проверка наличия слова '" + word + "' в словаре: " + words.contains(normalizeWord(word)));
        return words.contains(normalizeWord(word));
    }

    public static String compareWords(String guess, String answer) {
        if (guess == null || guess.length() != 5) {
            throw new IllegalArgumentException("Слово может состоять только из 5 букв ");
        }
        char[] result = new char[5];
        boolean[] answerUsed = new boolean[5];
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
                answerUsed[i] = true;
            }
        }
        for (int i = 0; i < 5; i++) {
            if (result[i] == '+') {
                continue;
            }
            char guessChar = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < 5; j++) {
                if (!answerUsed[j] && answer.charAt(j) == guessChar) {
                    result[i] = '^';
                    answerUsed[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                result[i] = '-';
            }
        }
        return new String(result);
    }

    public List<String> getWords() {
        return words;
    }
}