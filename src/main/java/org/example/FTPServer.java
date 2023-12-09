package org.example;

import org.apache.commons.net.ftp.*;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.PropertiesUserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class FTPServer {

    public void startServer(int port) throws FtpException {

        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        serverFactory.addListener("default", listenerFactory.createListener());

        UserManager userManager = new PropertiesUserManager(new ClearTextPasswordEncryptor(), new File("C:\\Users\\user\\IdeaProjects\\ftpServer\\src\\main\\resources\\users.properties"), "admin");
        BaseUser user = new BaseUser();
        user.setName("admin");
        user.setPassword("admin");
        user.setHomeDirectory("C:\\Users\\user\\Desktop\\w\\abc.txt");
        userManager.save(user);

        serverFactory.setUserManager(userManager);

        FtpServer server = serverFactory.createServer();
        try {
            server.start();
            System.out.println("FTP Server is running on port " + port);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
}
