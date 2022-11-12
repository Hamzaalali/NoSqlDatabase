package org.example.server_client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;

public class ServerClientCommunicator {


    public static String readString(Socket socket) throws IOException {
        return getBufferReader(socket).readLine();
    }

    public static int readInteger(Socket socket) throws IOException {
        return Integer.parseInt(getBufferReader(socket).readLine());
    }


    public static void sendObject(Socket socket, Object obj) throws IOException {
        ObjectOutputStream toServer =
                new ObjectOutputStream(socket.getOutputStream());
        toServer.writeObject(obj);
    }

    public static Object readObj(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream inputFromClient =
                new ObjectInputStream(socket.getInputStream());
        return inputFromClient.readObject();
    }

    public static void sendMessage(Socket socket, String message) throws IOException {
        PrintWriter printWriter = getPrintWriter(socket);
        printWriter.println(message);
        printWriter.flush();
    }
    public static void sendJson(Socket socket,JSONObject jsonObject) throws IOException {
        sendMessage(socket,jsonObject.toJSONString());
    }
    public static void sendJsonArray(Socket socket,JSONArray jsonArray) throws IOException {
        sendMessage(socket,jsonArray.toJSONString());
    }
    public static JSONArray readJsonArray(Socket socket) throws IOException, ClassNotFoundException, ParseException {
        String jsonString=(String) readObj(socket);
        JSONParser jsonParser=new JSONParser();
        JSONArray jsonArray= (JSONArray) jsonParser.parse(jsonString);
        return jsonArray;

    }
    public static JSONObject readJson(Socket socket) throws IOException, ParseException {
        String jsonString= readString(socket);
        JSONParser jsonParser=new JSONParser();
        JSONObject json= (JSONObject) jsonParser.parse(jsonString);
        return json;
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