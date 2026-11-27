package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import ru.yandex.practicum.exceptions.*;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

class WordleTest {
    private PrintWriter log;
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void makeGame() {
        log = new PrintWriter(System.out, true);
        List<String> words = Arrays.asList("книга", "песок", "волна");
        dictionary = new WordleDictionary(words, log);
    }

    @Test
    void testCreateNewGame() {
        game = new WordleGame(dictionary, log);
        assertNotNull(game.getAnswer());
        assertEquals(6, game.getSteps());
        assertFalse(game.isGameOver());
        assertFalse(game.isWon());
    }

    @Test
    void testNormalize() {
        assertEquals("книга", WordleDictionary.normalizeWord("КНИГА"));
        assertEquals("песок", WordleDictionary.normalizeWord("Песок"));
        assertEquals("волна", WordleDictionary.normalizeWord("  волна   "));
    }

    @Test
    void testContainsWord() throws WordNotFoundInDictionaryException {
        assertTrue(dictionary.containsWord("книга"));
        assertTrue(dictionary.containsWord("ПЕСОК"));
        assertFalse(dictionary.containsWord("якорь"));
    }

    @Test
    void testCompareWords() {
        assertEquals("+++++", WordleDictionary.compareWords("книга", "книга"));
        assertEquals("++-^+", WordleDictionary.compareWords("вобла", "волна"));
        assertEquals("-----", WordleDictionary.compareWords("абвгд", "ежзик"));
    }

    @Test
    void testCorrectGuess() throws InvalidWordException, WordNotFoundInDictionaryException {
        game = new WordleGame(dictionary, log, "книга");
        String result = game.makeGuess("книга");
        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertTrue(game.isGameOver());
        assertEquals(5, game.getSteps());
    }

    @Test
    void testWrongGuess() throws InvalidWordException, WordNotFoundInDictionaryException {
        game = new WordleGame(dictionary, log, "книга");
        String result = game.makeGuess("волна");
        assertNotEquals("+++++", result);
        assertFalse(game.isWon());
        assertFalse(game.isGameOver());
        assertEquals(5, game.getSteps());
    }

    @Test
    void testGameOverAfterSixAttempts() throws InvalidWordException, WordNotFoundInDictionaryException {
        game = new WordleGame(dictionary, log, "волна");
        for (int i = 0; i < 6; i++) {
            game.makeGuess("песок");
            if (i < 5) {
                assertFalse(game.isGameOver(), "Игра должна продолжаться после " + (i + 1) + " попытки");
            } else {
                assertTrue(game.isGameOver(), "Игра должна окончиться после 6 попыток");
                assertFalse(game.isWon(), "Нельзя победить после траты всех попыток");
            }
        }
        assertEquals(0, game.getSteps());
    }

    @Test
    void testGetHint() throws WordNotFoundInDictionaryException {
        game = new WordleGame(dictionary, log, "книга");
        assertEquals(5, game.getCountHints());
        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(4, game.getCountHints());
    }
}