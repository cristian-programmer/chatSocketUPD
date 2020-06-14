/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class Server {
    
    DatagramSocket socket;
    DatagramPacket data_package;
    DatagramPacket send_package;
    
    ArrayList<String> usersActive = new ArrayList();
    String rc_message;
    
    byte[] buffer_on = new byte[3045];
    byte[] buffer_emit = new byte[3045];
    
    public void instanceServer(){
        FileConfig config = new FileConfig();
        config.readFileConfig();
        System.out.println("Init Server: " + config.IPserver() + " " + config.getPort());
            try {
                socket = new DatagramSocket(config.getPort(),
                        InetAddress.getByName(config.IPserver()));
                while(true){
                  identifyAction();
                 }

            } catch (SocketException | UnknownHostException ex) {
                System.out.println("Error: " + ex);
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
   }
    
  public void identifyAction(){
       this.on();
       Parser parser = new Parser(rc_message);
       String action = parser.breakMessage()[0];
       
       if(action.equals( Parser.ACTION_ADD_USER )){
            this.registerUser(parser.breakMessage()[1]);
            this.emit("ok");
       }else if (action.equals(Parser.GET_ALL_USERS)){
           String users="";
           for(int i=0; i < this.usersActive.size(); i++){
               users = users + "," + this.usersActive.get(i);
           }
           System.out.println("Users: " +  users);
           this.emit(users);
       }
  }
    
   public void registerUser(String user){
       this.usersActive.add(user);
   }
    
    public void emit(String message){
        
        int PORT_CLIENT = data_package.getPort();
        InetAddress IP_CLIENT =  data_package.getAddress();
        System.out.println(message.getBytes().length);
        buffer_emit =  message.getBytes();
        
        send_package = new DatagramPacket(buffer_emit, buffer_emit.length, IP_CLIENT , PORT_CLIENT);
       
        try {
            
            socket.send(send_package);
            buffer_emit = new byte[3045];
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void on(){
        data_package = new DatagramPacket(buffer_on, buffer_on.length);
      
        try {
            socket.receive(data_package);
            rc_message = new String(data_package.getData());
            buffer_on = new byte[3045];
            System.out.println(rc_message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
