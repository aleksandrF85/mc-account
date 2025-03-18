package com.example.mc_account.exception;

public class AlreadyExistException extends RuntimeException{

    public AlreadyExistException(String message){

        super(message);
    }
}
