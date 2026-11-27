package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;
import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private PrintWriter log;
    private List<String> userInputs;
    private List<String> results;
    private boolean isGameOver;
    private boolean isWon;
    private Set<String> usedHints;
    private static final int MAX_TRIES = 6;
    private static final int MAX_HINTS = 5;
    private int hints;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = MAX_TRIES;
        this.userInputs = new ArrayList<>();
        this.results = new ArrayList<>();
        this.usedHints = new HashSet<>();
        this.isGameOver = false;
        this.isWon = false;
        this.hints = MAX_HINTS;

        log.println("Новая игра. Загадано слово —  " + answer);
    }

    public String makeGuess(String guess) throws InvalidWordException, WordNotFoundInDictionaryException {
        String normalizedGuess = WordleDictionary.normalizeWord(guess);

        if (normalizedGuess.length() != 5) {
            throw new InvalidWordException("Слово должно состоять из 5 букв");
        }
        if (!dictionary.containsWord(normalizedGuess)) {
            throw new WordNotFoundInDictionaryException("Слова " + normalizedGuess + " нет в словаре");
        }
        if (isGameOver) {
            throw new IllegalStateException("Игра завершена");
        }
        userInputs.add(normalizedGuess);

        String result = WordleDictionary.compareWords(normalizedGuess, answer);
        results.add(result);
        steps--;

        if (normalizedGuess.equals(answer)) {
            isWon = true;
            isGameOver = true;
        } else if (steps == 0) {
            isGameOver = true;
        }
        log.println("Ход: слово='" + normalizedGuess + "', результат='" + result +
                "', осталось попыток=" + steps);
        return result;
    }

    public String getHint() {
        if (isGameOver) {
            return "Игра завершена.";
        }

        if (hints <= 0) {
            return "подсказки закончились";
        }

        List<String> possibleWords = dictionary.getWords();


        for (int i = 0; i < userInputs.size(); i++) {
            String guess = userInputs.get(i);
            String result = results.get(i);
            possibleWords = filterWordsByResult(possibleWords, guess, result);
        }

        possibleWords.removeAll(usedHints);

        String hint;
        if (possibleWords.isEmpty()) {
            hint = dictionary.getRandomWord();
        } else {
            Random random = new Random();
            hint = possibleWords.get(random.nextInt(possibleWords.size()));
        }

        hints--;
        usedHints.add(hint);

        log.println("Дана подсказка: " + hint +
                " (осталось подсказок: " + hints +
                ", подходящих слов: " + possibleWords.size() + ")");

        return hint + " (осталось подсказок: " + hints + ")";
    }

    private List<String> filterWordsByResult(List<String> words, String guess, String result) {
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (matchesPattern(word, guess, result)) {
                filteredWords.add(word);
            }
        }
        return filteredWords;
    }

    private boolean matchesPattern(String word, String guess, String result) {
        if (word.length() != guess.length()) {
            return false;
        }

        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '+') {
                if (word.charAt(i) != guess.charAt(i)) {
                    return false;
                }
            }
        }

        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '^') {
                char c = guess.charAt(i);
                if (word.charAt(i) == c || word.indexOf(c) == -1) {
                    return false;
                }
            }
        }

        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '-') {
                char c = guess.charAt(i);

                boolean foundElsewhere = false;
                for (int j = 0; j < result.length(); j++) {
                    if (j != i && (result.charAt(j) == '+' || result.charAt(j) == '^') &&
                            guess.charAt(j) == c) {
                        foundElsewhere = true;
                        break;
                    }
                }
                if (!foundElsewhere && word.indexOf(c) != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isExitCommand(String cmd) {
        String normalized = WordleDictionary.normalizeWord(cmd);
        return normalized.equals("стоп");
    }

    public int getCountHints() {
        return hints;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isWon() {
        return isWon;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String testAnswer) {
        this.answer = testAnswer;
        this.steps = MAX_TRIES;
        this.dictionary = dictionary;
        this.log = log;
        this.userInputs = new ArrayList<>();
        this.results = new ArrayList<>();
        this.isGameOver = false;
        this.isWon = false;
        this.usedHints = new HashSet<>();
        this.hints = MAX_HINTS;
    }
}