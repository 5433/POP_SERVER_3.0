package pop.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Communicator extends Thread {

    private ArrayList messagesToSend = new ArrayList();
    private MessageLogger messageLogger;
    private PrintWriter printer;
    private Socket socket;
    private BufferedReader br;
    String receivedM = "";

    public Communicator(Socket s, MessageLogger ml) throws IOException {
        messageLogger = ml;
        socket = s;
        printer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Listens listens = new Listens();
        listens.start();
    }

    public String getMessage() {
        return receivedM;
    }

    //adds String message to arraylist messagesToSend.
    public synchronized void addToMessages(String text) {
        messagesToSend.add(text);
        notify();
    }

    //removes message from arraylist, and returns String message.
    private synchronized String nextElementToSend() throws InterruptedException {
        while (messagesToSend.size() == 0) {
            wait();
        }

        String message = (String) messagesToSend.get(0);
        messagesToSend.remove(0);
        return message;
    }

    //send String message to Device using PrintWriter.
    private void sendMessageToDevice(String text) {
        printer.println(text);
        printer.flush();
    }

    public void run() {
        try {
            while (!isInterrupted()) {
                String message = nextElementToSend();
                sendMessageToDevice(message);
            }
        } catch (Exception e) {
            //
        }

        messageLogger.getCommunicator(socket).interrupt();
        messageLogger.removeCommunicator(socket);
    }

    class Listens extends Thread {

        public void run() {
            try {
                while (!isInterrupted()) {
                    String message = br.readLine();
                    //messageM += message + "\r\n";
                    receivedM = message;
                    if (message == null) {
                        break;
                    }
                    messageLogger.postMessage(message);
                }
            } catch (IOException ioex) {
                // could not read from socket
            }

            // Broken. Interrupt both listener and sender threads.
            messageLogger.getCommunicator(socket).interrupt();
            messageLogger.removeCommunicator(socket);

        }
    }
}
