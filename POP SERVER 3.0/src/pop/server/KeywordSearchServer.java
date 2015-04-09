package pop.server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

public class KeywordSearchServer {

    private Lock lock = new ReentrantLock();
    static volatile String temp = "";
    JFrame frame = new JFrame("Server");
    JPanel panel = new JPanel();
    JTextArea textAr = new JTextArea();
    static volatile boolean newForum = false;
    MessageLogger messageLogger = new MessageLogger();
    ServerSocket server;
    Communicator communicator;

    public KeywordSearchServer(int num, String name) {
        connector(num, name);
    }

    public synchronized void setForum(boolean bool) {
        newForum = bool;
    }

    public boolean getForum() {
        return newForum;
    }

    private void connector(int i, String name) {
        drawWindow();
        try {
            server = new ServerSocket(i);
            System.out.println("This is the" + name + " server, listening on port: " + i);
        } catch (IOException e) {
            //
        }

        messageLogger.start();

        Thread thread = new Thread(new ThreadText());

        thread.start();

        while (true) {
            try {
                Socket socket = server.accept();
                
                communicator = new Communicator(socket, messageLogger);

                messageLogger.addCommunicator(socket, communicator);
                
                communicator.start();
            } catch (IOException e) {
                //
            }

        }
    }

    public void drawWindow() {
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textAr.setText("PORT_OPEN");
        panel.add(textAr);
        panel.setPreferredSize(new Dimension(400, 600));
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);

    }

    class ThreadText implements Runnable {

        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    //if(!clientListener.messageM.isEmpty())
                    //textAr.setText(clientListener.messageM);
                    textAr.setText(messageLogger.castMessage);

                    if (communicator.receivedM.contains("create")) {
                        setForum(true);
                        temp = communicator.receivedM;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            }
        }
    }

}
