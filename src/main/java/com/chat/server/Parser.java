/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.server;

/**
 *
 * @author User
 */
public class Parser {
    final static String SYMBOL = ":::";
    final static String ACTION_ADD_USER = "addUser";
    final static String ACTION_SEND_MESSAGE = "sendMessage";
    final static String GET_ALL_USERS="getUsers";

    public String message; 
    public Parser(String message) {
       this.message =  message;
    }
    public String[] breakMessage(){
       return this.message.split(SYMBOL);
    }
    
}
