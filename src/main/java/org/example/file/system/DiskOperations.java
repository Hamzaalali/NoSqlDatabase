package org.example.file.system;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.database.Database;
import org.example.database.collection.document.DocumentSchema;
import org.example.server_client.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


public class DiskOperations {
    private static final String storageDirectoryPath="storage";
    public static void createDatabase(String databaseName) throws IOException {
        if(databaseExists(databaseName)){
            throw new IllegalArgumentException();
        }
        String dataBaseDirectoryPath= createDirectoryIfNotFound(databasePath(databaseName));
    }
    public static void deleteDatabase(String databaseName) throws IOException {
        File databaseDirectory=new File(databasePath(databaseName));
        FileUtils.deleteDirectory(databaseDirectory);
    }
    public static String createDirectoryIfNotFound(String directory) throws IOException {
        String dataBaseDirectoryPath= directory;
        Files.createDirectories(Paths.get(dataBaseDirectoryPath));
        return dataBaseDirectoryPath;
    }
    public static void createCollection(String databaseName,String collectionName,JSONObject schema) throws IOException {
        if(!databaseExists(databaseName)){
            throw new IllegalArgumentException();
        }
        createDirectoryIfNotFound(databasePath(databaseName)+"/"+collectionName);
        writeToFile(collectionPath(databaseName,collectionName),new JSONArray().toJSONString());
        writeToFile(collectionSchemaPath(databaseName,collectionName),schema.toJSONString());
    }
    public static void deleteCollection(String databaseName,String collectionName) throws IOException {
        File databaseDirectory=new File(collectionDirectoryPath(databaseName,collectionName));
        FileUtils.deleteDirectory(databaseDirectory);
    }
    public static JSONObject createDocument(String databaseName,String collectionName,JSONObject document) throws IOException, ParseException {
        if(!databaseExists(databaseName)){
            throw new IllegalArgumentException();
        }
        if(!collectionExists(databaseName,collectionName)){
            throw new IllegalArgumentException();
        }
        return appendDocument(databaseName,collectionName,document);
    }
//    public static void deleteDocument(Query query,JSONObject indexObject) throws IOException {
//        RandomAccessFile readerWriter = new RandomAccessFile(collectionPath(query), "rw");
//        FileChannel channel = readerWriter.getChannel();
//        int start= (int) indexObject.get("start");
//        int end= (int) indexObject.get("end");
//        int fileSize= (int) channel.size();
//        if(start==1&&end!=fileSize-2){
//            end++;
//        }
//        if(start!=1&&end==fileSize-2){
//            start--;
//        }
//        if(start!=-1&&end!=fileSize-2){
//            start--;
//        }
//        ByteBuffer buff = ByteBuffer.wrap((StringUtils.repeat(" ",end-start+1)).getBytes());
//        channel.write(buff, start);
//        channel.close();
//        readerWriter.close();
//    }
    private static JSONObject appendDocument(String databaseName,String collectionName,JSONObject document) throws IOException {
        RandomAccessFile readerWriter = new RandomAccessFile(collectionPath(databaseName,collectionName), "rw");
        FileChannel channel = readerWriter.getChannel();
        int index= (int) channel.size();
        ByteBuffer buff;
        int start;
        int end;
        if(index==2){
            start=index-1;
            end=start+document.toJSONString().getBytes().length-1;
            buff = ByteBuffer.wrap((document.toJSONString()+"]").getBytes());
        }else{
            start=index;
            end=start+document.toJSONString().getBytes().length-1;
            buff = ByteBuffer.wrap((","+document.toJSONString()+"]").getBytes());
        }
        channel.write(buff,index-1);
        channel.close();
        readerWriter.close();
        JSONObject indexObject=indexObject(start,end,document.get("id").toString());
        return indexObject;
    }
    public static JSONObject readDocument(String databaseName,String collectionName,JSONObject indexObject) throws IOException, ParseException {
        RandomAccessFile reader = new RandomAccessFile(collectionPath(databaseName,collectionName), "r");
        FileChannel channel = reader.getChannel();
        int start= (int) indexObject.get("start");
        int end= (int) indexObject.get("end");
        ByteBuffer buff = ByteBuffer.allocate(end-start+1);
        channel.read(buff, start);
        JSONParser jsonParser = new JSONParser();
        JSONObject document = (JSONObject) jsonParser.parse(new String(buff.array(), StandardCharsets.UTF_8));
        channel.close();
        reader.close();
        return document;
    }
    public static DocumentSchema getSchema(String databaseName,String collectionName) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader schemaReader = new FileReader(collectionSchemaPath(databaseName,collectionName));
        Object obj = jsonParser.parse(schemaReader);
        JSONObject schema = (JSONObject) obj;
        DocumentSchema documentSchema=new DocumentSchema(schema);
        schemaReader.close();
        return documentSchema;
    }
    public static JSONArray readCollection(String databaseName,String collectionName, List<JSONObject> indexesList) throws IOException, ParseException {
        JSONArray jsonArray=new JSONArray();
        RandomAccessFile reader = new RandomAccessFile(collectionPath(databaseName,collectionName), "r");
        FileChannel channel = reader.getChannel();
        for(JSONObject indexObject:indexesList){
            int start= (int) indexObject.get("start");
            int end= (int) indexObject.get("end");
            ByteBuffer buff = ByteBuffer.allocate(end-start+1);
            channel.read(buff, start);
            JSONParser jsonParser = new JSONParser();
            JSONObject document = (JSONObject) jsonParser.parse(new String(buff.array(), StandardCharsets.UTF_8));
            jsonArray.add(document);
        }
        channel.close();
        reader.close();
//        JSONParser jsonParser = new JSONParser();
//        FileReader collectionDataReader = new FileReader(collectionPath(query));
//        Object obj = jsonParser.parse(collectionDataReader);
//        JSONArray data = (JSONArray) obj;
//        collectionDataReader.close();
//        return data;
        return jsonArray;
    }
    private static void writeToFile(String filePath,String data) throws IOException {
        FileWriter fileWriter=new FileWriter(filePath);
        fileWriter.write(data);
        fileWriter.flush();
        fileWriter.close();
    }
    private static JSONObject indexObject(int start,int end,String id){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("start",start);
        jsonObject.put("end",end);
        jsonObject.put("id",id);
        return jsonObject;
    }

    private static boolean collectionExists(String databaseName,String collectionName){
        return directoryOrFileExists(collectionPath(databaseName,collectionName));
    }
    private static boolean databaseExists(String databaseName){
        return directoryOrFileExists(databasePath(databaseName));
    }
    private static boolean directoryOrFileExists(String pathForFileOrDirectory){
        Path path = Paths.get(pathForFileOrDirectory);
        return Files.exists(path);
    }


    private static String databasePath(String databaseName){
        return storageDirectoryPath+"/"+databaseName;
    }
    private static String collectionPath(String databaseName,String collectionName){
        return databasePath(databaseName)+"/"+collectionName+"/"+collectionName+".json";
    }
    private static String collectionSchemaPath( String databaseName,String collectionName){
        return databasePath(databaseName)+"/"+collectionName+"/"+collectionName+".schema"+".json";
    }
    private static String collectionDirectoryPath(String databaseName,String collectionName){
        return databasePath(databaseName)+"/"+collectionName;
    }

}
