package ehv3.src.java;

import java.io.*;
import java.net.*;

public class MultithreadedHTTPServer implements Runnable {

    private ServerSocket socket;
    private int portNum;
    private boolean serverStopped;
    private String[] properties;

    public MultithreadedHTTPServer(String[] propValues) {
        this.portNum = Integer.parseInt(propValues[0]);
        this.properties = propValues;
        this.serverStopped = false;
    }

    private boolean serverStopped() {
        return this.serverStopped;
    }

    private void setServerStopped(boolean stop) {
        this.serverStopped = stop;
    }

    public ServerSocket getServerSocket() {
        return socket;
    }

    public int getPortNum() {
        return portNum;
    }

    public String[] getProperties() {
        return properties;
    }

    /**
     * Starts a new socket on the port number
     */
    public void startServerSocket()  {
        try {
            socket = new ServerSocket(getPortNum());
        } catch (IOException e) {
            throw new RuntimeException("Cannot open the port: " + portNum);
        }

    }

    public synchronized void stop() {
        try {
            socket.close();
            setServerStopped(true);
        } catch (IOException e) {
            throw new RuntimeException("Error closing socket");
        }
    }

    /**
     * Run method that basically starts the server and keeps it running.
     */
    public void run() {

        startServerSocket();

        while(!serverStopped()) {
            Socket clientSocket;
            try {
                clientSocket = getServerSocket().accept();
            } catch (IOException e) {
                if(serverStopped()) {
                    throw new RuntimeException("Server stopped before connection accepted");
                } else {
                    throw new RuntimeException("Error accepting connection");
                }
            }

            //Process the request on a new thread
            new Thread (
                    new HTTPRequest(clientSocket, getProperties())
            ).start();
        }
    }

    /**
     * The main method that runs the entire server. No arguments needed. The server will run for
     * 100 seconds, assuming no interruptions or exceptions. I didn't really know how long it was supposed to
     * run and I didn't really want it to run indefinitely.
     * @param args
     */
    public static void main(String[] args) {

        //Read from the config file
        PropertiesReader reader = new PropertiesReader();
        String[] propValues = reader.getPropValues();

        MultithreadedHTTPServer server = new MultithreadedHTTPServer(propValues);
        new Thread(server).start();
        try {
            //Set the timeout at 100 seconds
            Thread.sleep(100 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Stopping server...");
            server.stop();
        }

    }
}
