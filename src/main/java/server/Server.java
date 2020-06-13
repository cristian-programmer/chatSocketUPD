/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    
    byte[] buffer_on = new byte[1024];
    byte[] buffer_emit = new byte[1024];
    
    public void instanceServer(){
        FileConfig config = new FileConfig();
        config.readFileConfig();
        System.out.println("Init Server: " + config.IPserver() + " " + config.getPort());
            try {
                socket = new DatagramSocket(config.getPort(),
                        InetAddress.getByName(config.IPserver()));
                while(true){
                    this.on();
                    this.emit();
                 }

            } catch (SocketException | UnknownHostException ex) {
                System.out.println("Error: " + ex);
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
   }
    
    public void emit(){
        String message = new String(data_package.getData());
        int PORT_CLIENT = data_package.getPort();
        InetAddress IP_CLIENT =  data_package.getAddress();
        buffer_emit =  message.getBytes();
        send_package = new DatagramPacket(buffer_emit, buffer_emit.length, IP_CLIENT , PORT_CLIENT);
       
        try {
            socket.send(send_package);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void on(){
        data_package = new DatagramPacket(buffer_on, buffer_on.length);
        String message;
        try {
            socket.receive(data_package);
            message = new String(data_package.getData());
            System.out.println(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
