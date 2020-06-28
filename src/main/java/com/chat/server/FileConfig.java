/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author User
 */
public class FileConfig {
    
    private int PORT;
    private String IPServer;
    
    private void  setConfig(int PORT, String IPServer){
        this.PORT = PORT;
        this.IPServer =  IPServer;
    }
    public void readFileConfig(String path){
        File config = new File(path);
        try {
              FileReader fileReader = new FileReader(config);
              BufferedReader buffer =  new BufferedReader(fileReader);
              String line = buffer.readLine();
              while(line != null){
                  String temp [] = line.split(",");
                if(!temp[0].equals("IP") && !temp[1].equals("PORT")){
                   setConfig(Integer.parseInt(temp[1]), temp[0]);
              }
             
              line  = buffer.readLine();
              }
        } catch (IOException error) {
            System.out.println("Error IOException: " + error);
        }
    }
    
    public int getPort(){
        return PORT;
    }
    
    public String IPserver(){
        return IPServer;
    }
}
