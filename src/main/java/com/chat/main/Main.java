/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chat.main;

import com.chat.server.FileConfig;
import com.chat.server.Server;

/**
 *
 * @author User
 */
public class Main {
    public static void main(String[] args) {
        Server serverSocketUDP = new Server(args[0]);
        Thread multiServer = new Thread(serverSocketUDP);
        multiServer.start();
    }
}
