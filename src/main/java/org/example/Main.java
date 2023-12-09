package org.example;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static int port=21;
    private static String pathToSave="C:\\Users\\user\\Desktop\\w\\";
    private static String username=null;
    private static String password=null;
    private static String homeDirectory=null;
    private static String pathUsersProperties="C:\\Users\\user\\IdeaProjects\\ftpServer\\src\\main\\resources\\users.properties";
    public static void main(String[] args) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port);// set the port of the listener (choose your desired port, not 1234)
        serverFactory.addListener("default", factory.createListener());
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File(pathUsersProperties));//choose any. We're telling the FTP-server where to read its user list
        userManagerFactory.setPasswordEncryptor(new PasswordEncryptor() {//We store clear-text passwords in this example

            @Override
            public String encrypt(String password) {
                return password;
            }

            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
                return passwordToCheck.equals(storedPassword);
            }
        });
        //Let's add a user, since our myusers.properties file is empty on our first test run

        UserManager um = userManagerFactory.createUserManager();
        serverFactory.setUserManager(um);
        Map<String, Ftplet> m = new HashMap<>();
        serverFactory.setFtplets(m);
        FtpServer server = serverFactory.createServer();
        m.put("miaFtplet", new Ftplet()
        {
            @Override
            public void init(FtpletContext ftpletContext){
                //System.out.println("init");
                //System.out.println("Thread #" + Thread.currentThread().getId());
            }
            @Override
            public void destroy() {
                //System.out.println("destroy");
                //System.out.println("Thread #" + Thread.currentThread().getId());
            }
            @Override
            public FtpletResult beforeCommand(FtpSession session, FtpRequest request)
            {
                //System.out.println("beforeCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine());
                //System.out.println("Thread #" + Thread.currentThread().getId());
                if(username==null){
                    username=session.getUserArgument();
                }
                if(password==null){
                    password=request.getArgument();
                }
                //do something
                return FtpletResult.DEFAULT;//...or return accordingly
            }
            @Override
                public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply)
            {
                //System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                //System.out.println("afterCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine() + " | " + reply.getMessage() + " : " + reply.toString());
                //System.out.println("Thread #" + Thread.currentThread().getId());
                //String username = session.getUserArgument();//session.getUserArgument();
                //String password = session.getUserArgument();//request.getArgument();
                // יצירת משתמש חדש והוספתו

              //  createUser(um,"1","1","C:\\Users\\user\\Desktop\\w\\1");
                //do something
                return FtpletResult.DEFAULT;//...or return accordingly
            }
            @Override
            public FtpletResult onConnect(FtpSession session)
            {
                //System.out.println("onConnect " + session.getUserArgument() + " : " + session.toString());
                //System.out.println("Thread #" + Thread.currentThread().getId());
                //do something
                return FtpletResult.DEFAULT;//...or return accordingly
            }
            @Override
            public FtpletResult onDisconnect(FtpSession session)
            {
                homeDirectory =  pathToSave+username;
                createUser(um, username, password, homeDirectory);
                username=null;
                password=null;
                homeDirectory=null;
                return FtpletResult.DEFAULT;
            }
        });
        try
        {
            server.start();
        }
        catch (FtpException ex)
        {
            System.out.println(ex);
        }
    }
    private static void createUser(UserManager userManager, String username, String password, String homeDirectory) {
        BaseUser user = new BaseUser();
        user.setName(username);
        user.setPassword(password);
        //user.setHomeDirectory(homeDirectory);
        user.setHomeDirectory("/");
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        try {
            User users=userManager.getUserByName(username);
            if(users!=null){
                return;
            }
            try {
                    userManager.save(user);
                System.out.println("User added: " + user.getName());
            }catch (Exception e){
                System.out.println(e);
            }

            String userSpecificDirectory = homeDirectory;
            boolean created = new File(userSpecificDirectory).mkdir();
            if (created) {
                System.out.println("User directory created: " + userSpecificDirectory);
            } else {
                System.out.println("Failed to create user directory: " + userSpecificDirectory);
            }

        } catch (FtpException e) {
            System.out.println("Failed to add user: " + e.getMessage());
        }
    }
}
