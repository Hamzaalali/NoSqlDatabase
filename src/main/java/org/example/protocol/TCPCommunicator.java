package org.example.protocol;

import org.example.exception.ConnectionTerminatedException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

public class TCPCommunicator {
    public static void sendJson(Socket socket,JSONObject jsonObject) throws IOException {
        sendMessage(socket,jsonObject.toJSONString());
    }
    public static JSONObject readJson(Socket socket) throws IOException, ParseException, ConnectionTerminatedException {
        Optional<String> jsonString= readMessage(socket);
        JSONParser jsonParser=new JSONParser();
        JSONObject json= (JSONObject) jsonParser.parse(jsonString.orElseThrow(ConnectionTerminatedException::new));
        return json;
    }
    public static Optional<String> readMessage(Socket socket) throws IOException {
        String message=getBufferReader(socket).readLine();
        return Optional.ofNullable(message);
    }
    public static void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter printWriter = getPrintWriter(socket);
        printWriter.println(message);
        printWriter.flush();
    }
    private static BufferedReader getBufferReader(Socket socket) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader;
    }

    private static PrintWriter getPrintWriter(Socket socket) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        PrintWriter printWriter = new PrintWriter(outputStreamWriter);
        return printWriter;
    }
}