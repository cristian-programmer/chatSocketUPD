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
public class Server implements Runnable{
    
    DatagramSocket socket;
    DatagramPacket data_package;
    DatagramPacket send_package;
    
    ArrayList<String> usersActive = new ArrayList();
    ArrayList<User> usersList = new ArrayList();
    User user;
    int PORT_CLIENT_TEMP;
    InetAddress IP_CLIENT_TEMP;
    
    String rc_message;
    
    byte[] buffer_on = new byte[1024];
   
    
    private  void instanceServer(){
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
            String username = this.getNeededBytes(parser.breakMessage()[1].getBytes());
            this.registerUser(username);
            String message = "ok";
            this.emit(message.getBytes());
            
       }else if (action.equals(Parser.GET_ALL_USERS)){
           String users = getAllUsers();
           this.emit(users.getBytes());
           System.out.println("Users string: " +  users.length());
           
       }else if (action.equals(Parser.ACTION_SEND_MESSAGE)){
           //format sendMessage:::user,me > 10:34:45 : hola como estas?
           String temp = parser.breakMessage()[1];
           System.out.println("temp " + temp);
           String message = preparedMessage(temp);
           
           String user = this.getNeededBytes(temp.split(",")[0].getBytes());
           sendMessage(user, message, temp);
          
       } else if (action.equals(Parser.ACTION_USER_EXIT)){
           String data = parser.breakMessage()[1];
           String _user = getNeededBytes(data.getBytes());
           if(deleteUser(findUser(_user))){
               String exit = Parser.ACTION_USER_EXIT + Parser.SYMBOL + "ok";
               this.emit(exit.getBytes());
           }else {
               System.out.println("No se pudo eliminar el usuario");
           }        
       }
  }

    private void sendMessage(String user1, String message, String temp) {
        if (user1.equals("all")) {
            for(int i=0; i < usersList.size(); i ++) {
                redirectEmitByUser(message.getBytes(),
                        findUser(usersList.get(i).getUsername()));
            }
        } else {
            String me = getNeededBytes((temp.split(",")[1]).split(">")[0].getBytes());
            System.out.println("me >> " + me);
            String[] tempUsers = {me, user1};
            System.out.println("USER " + user1 + " MESSAGE " + message);
            for(int i=0; i < tempUsers.length; i++){
                this.redirectEmitByUser(message.getBytes(), findUser(tempUsers[i]));
            }
        }
    }

    private String preparedMessage(String temp) {
        String message = Parser.ACTION_SEND_MESSAGE + Parser.SYMBOL
                + this.getNeededBytes(temp.split(",")[1].getBytes());
        return message;
    }

    private String getAllUsers() {
        // getUsers:::,all, hu, df, fr
        String users = Parser.GET_ALL_USERS + Parser.SYMBOL + ",all";
        for(int i=0; i < this.usersList.size(); i++){
            System.out.println("Users SIZE: " + usersActive.size());
            System.out.println("Users SIZE: " + usersList.get(i).getUsername());
            users =   users  + "," + this.usersList.get(i).getUsername();
        }
        return users;
    }
  
   public boolean deleteUser(User u){
       return this.usersList.remove(u);
   }
   
   public User findUser(String username){
       for(int i=0; i< usersList.size(); i++){
           if(usersList.get(i).getUsername().equals(username)){
             System.out.println("Entre");
             return usersList.get(i);
           }
       }
       return null;
   }
   
   public void registerUser(String username){
       System.out.println(PORT_CLIENT_TEMP + " " + IP_CLIENT_TEMP + " " + username);
       user = new User( PORT_CLIENT_TEMP, IP_CLIENT_TEMP, username);
        usersList.add(user);
   }    
   
   public void redirectEmitByUser(byte buffer_emit[], User user){
      System.out.println("redirect: " + user.UserFormat());
      try {
            if(user != null){
                send_package = new DatagramPacket(buffer_emit, buffer_emit.length, user.getIP(), user.getPORT());    
                socket.send(send_package);
                buffer_emit = new byte[1024];
            }else{
                System.out.println("No se encontro un usuario para enviar el mensaje");
            }
          
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
    public void emit(byte buffer_emit[]){
        
        int PORT_CLIENT = data_package.getPort();
        InetAddress IP_CLIENT =  data_package.getAddress();
        System.out.println("bytes emits: " + buffer_emit.length);
  
        try {
            send_package = new DatagramPacket(buffer_emit, buffer_emit.length, IP_CLIENT , PORT_CLIENT);    
            socket.send(send_package);
            buffer_emit = new byte[1024];
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void on(){
        data_package = new DatagramPacket(buffer_on, buffer_on.length);
        
        try {
            socket.receive(data_package);
            PORT_CLIENT_TEMP = data_package.getPort();
            IP_CLIENT_TEMP = data_package.getAddress();
            rc_message = new String(data_package.getData());
            byte test[] = rc_message.getBytes();
            System.out.println(test[900]);
            
            buffer_on = new byte[3045];
            System.out.println(rc_message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getNeededBytes(byte in[]){
        String result="";
        boolean stop =  false;
        for(int i=0; i< in.length; i++){
            if(in[i] != 0){
                char temp = (char) in[i];
                result = result + String.valueOf(temp);
               
            }else {
              break;
            }
        }
        return result;
    }

    @Override
    public void run() {
        this.instanceServer();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class User{
        private int PORT;
        private InetAddress IP;
        private String username;

        public User(int PORT, InetAddress IP, String username){
            this.PORT = PORT;
            this.IP = IP;
            this.username =  username;
        }
        
        public int getPORT() {
            return PORT;
        }

        public InetAddress getIP() {
            return IP;
        }

        public String getUsername() {
            return username;
        }
        
        public String UserFormat(){
            //ip;port;user,
            return getIP().toString() + ";" + getPORT() + ";" + getUsername();
        }
        
    }
}
