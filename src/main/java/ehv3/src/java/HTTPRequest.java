package ehv3.src.java;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class HTTPRequest implements Runnable {

    final static String CRLF = "\r\n";
    final static byte[] badRequestResponse =  ("HTTP/1.1 404 Not Found" + CRLF
                                              +"Content-type: text/html" + CRLF + CRLF
                                              +"<HTML>" +
                                               "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                                               "<BODY>404 Not Found</BODY></HTML>").getBytes();

    private Socket socket;
    private String[] params;
    private OutputStream outputStream;
    private int numVisits = 0;

    public HTTPRequest(Socket socket, String[] params) {
        this.socket = socket;
        this.params = params;
    }

    public Socket getSocket() {
        return socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method whose sole responsibility is to send an html file and buffering each input in bytes.
     * @param fis
     * @throws IOException
     */
    private void sendBytes(FileInputStream fis) throws IOException {
        //Buffer the input to the output
        byte[] buffer = new byte[1000];
        int bytes;

        while((bytes = fis.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytes);
        }
    }

    /**
     * Initial method that takes the input stream converted into a BufferedReader as a an input. Calls the appropriate
     * next methods and prints the http request to the screen.
     * @param reader
     * @throws IOException
     */
    private void getResponseFor(BufferedReader reader) throws IOException {
        String request = reader.readLine();
        System.out.println(request);

        //Always the path to the requested object
        String path = request.split(" ")[1];

        String headerLine = null;
        List<String> headerLines = new LinkedList<>();
        while((headerLine = reader.readLine()).length() > 0) {
            System.out.println(headerLine);
            headerLines.add(headerLine);
        }

        findNumVisits(headerLines);

        //Legal paths only start with /ehv3/
        if(path.startsWith("/ehv3/")) {
            goodRequest(path);
        } else {
            badRequest();
        }
    }

    /**
     * Method that parses the header lines and determines the number of previous visits by searching for the
     * "Cookie" line and parsing the first entry. The cookie "numVisits" will always be the first and only entry.
     * @param headerLines
     */
    private void findNumVisits(List<String> headerLines) {
        String headerType;
        String[] headerArray;
        for(String s : headerLines) {
            headerArray = s.split(" ");
            headerType = headerArray[0];
            if(headerType.contains("Cookie:")) {
                String[] cookies = headerArray[1].split(" ");
                for(String val : cookies) {
                    if(val.startsWith("numVisits")) {
                        String[] numVisitsCookie = val.split("=");
                        numVisits = Integer.parseInt(numVisitsCookie[1]);
                    }
                }

            }
        }
    }

    /**
     * Method that parses what the output of a good request should be and calls the appropriate method
     * to service the request.
     * @param path
     * @throws IOException
     */
    private void goodRequest(String path) throws IOException {
        if(path.compareTo("/ehv3/test1.html") == 0) {
            normalHtmlRequest(1);
        } else if (path.compareTo("/ehv3/test2.html") == 0) {
            normalHtmlRequest(2);
        } else if (path.compareTo("/ehv3/visits.html") == 0) {
            specialVisitsRequest();
        } else {
            badRequest();
        }
    }

    /**
     * Method to service requests for the html files (test1 and test2)
     * @param fileNum
     */
    private void normalHtmlRequest(int fileNum) {
        try {
            outputStream.write(("HTTP/1.1 200 OK" + CRLF).getBytes());
            outputStream.write(("ContentType: text/html" + CRLF).getBytes());
            outputStream.write(("Set-Cookie: numVisits=" + ++numVisits + CRLF + CRLF).getBytes());
            FileInputStream fis = new FileInputStream(params[fileNum]);
            sendBytes(fis);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that handles the requests for visits.html. Dynamically generates the page from the numVisits variable
     */
    private void specialVisitsRequest() {
        try {
            outputStream.write(("HTTP/1.1 200 OK" + CRLF).getBytes());
            outputStream.write(("ContentType: text/html" + CRLF).getBytes());
            outputStream.write(("Set-Cookie: numVisits=" + ++numVisits + CRLF + CRLF).getBytes());
            outputStream.write((
                    "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>You have visited pages" +
                            "on this site " + numVisits +" times</BODY><HTML>"
                    ).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that writes the pre-made static 404 response to the output stream
     * @throws IOException
     */
    private void badRequest() throws IOException {
        outputStream.write(badRequestResponse);
    }

    /**
     * Method to process the HTTP request from the socket, basically opens the streams, calls another method,
     * and then closes the streams.
     * @throws IOException
     */
    private void processRequest() throws IOException {
        InputStream inputStream = getSocket().getInputStream();
        outputStream = getSocket().getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        //Calls the main request method
        getResponseFor(reader);

        inputStream.close();
        outputStream.close();
        reader.close();
    }
}
