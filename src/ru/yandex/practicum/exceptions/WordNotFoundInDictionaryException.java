package ru.yandex.practicum.exceptions;

public class WordNotFoundInDictionaryException extends Exception {
    public WordNotFoundInDictionaryException(final String message){
        super(message);
    }
}
