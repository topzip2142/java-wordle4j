package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import ru.yandex.practicum.exceptions.*;

public class Wordle {

    public static void main(String[] args) {
        PrintWriter log = null;

        try {
            log = createLogFile();
            log.println("=======");
            log.println("Запуск игры");
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
            WordleGame game = new WordleGame(dictionary, log);
            playGame(game, log);
        } catch (DictionaryUploadException e) {
            System.out.println("Ошибка загрузки словаря " + e.getMessage());
            if (log != null) {
                log.println("Ошибка " + e.getMessage());
                e.printStackTrace(log);
            }
        } catch (Exception e) {
            System.out.println("Ошибка " + e.getMessage());
            if (log != null) {
                log.println("Ошибка " + e.getMessage());
                e.printStackTrace(log);
            }
        } finally {
            if (log != null) {
                log.println("Завершение работы");
                log.println("=======");
                log.close();
            }
        }
    }

    private static PrintWriter createLogFile() throws IOException {
        String logFileName = "wordle_log.txt";
        FileWriter fileWriter = new FileWriter(logFileName, StandardCharsets.UTF_8);
        return new PrintWriter(fileWriter, true);
    }

    private static void playGame(WordleGame game, PrintWriter log) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Отгадайте слово из 5 букв за 6 попыток");
            System.out.println("Команды:");
            System.out.println("  Enter - подсказка (осталось: " + game.getCountHints() + ")");
            System.out.println("  'стоп' - выйти из игры");
            log.println("Начало серии");

            while (!game.isGameOver()) {
                System.out.println("Введите слово");
                String input = scanner.nextLine().trim();

                if (game.isExitCommand(input)) {
                    System.out.println("Игра остановлена");
                    log.println("Игра принудительно завершена " + input);
                    break;
                }
                try {
                    if (input.isEmpty()) {
                        String hint = game.getHint();
                        System.out.println("Подсказка - " + hint);
                        log.println("Запрошена подсказка " + hint);
                        continue;
                    }
                    String result = game.makeGuess(input);

                    System.out.println("Результат " + result);
                    System.out.println("осталось попыток " + game.getSteps());

                    if (game.isWon()) {
                        System.out.println("Вы отгадали слово " + game.getAnswer());
                        log.println("Игра выиграна. Загаданное слово - " + game.getAnswer());
                        break;
                    } else if (game.isGameOver()) {
                        System.out.println("Игра проиграна. Загаданное слово " + game.getAnswer());
                        log.println("Игра проиграна. Загаданное слово - " + game.getAnswer());
                        break;
                    }
                } catch (ru.yandex.practicum.exceptions.InvalidWordException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    log.println("Введено некорректное слово: " + input);
                } catch (ru.yandex.practicum.exceptions.WordNotFoundInDictionaryException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    log.println("Введено слово отсутствующее в словаре - " + input);
                }
            }
        }
    }
}