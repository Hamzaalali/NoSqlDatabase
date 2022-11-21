package org.example.services;

import org.example.index.IndexManager;
import org.example.file.DiskOperations;

import java.io.IOException;

public class DatabaseServices {
    public static void createDatabase(String databaseName) throws IOException {
        IndexManager.getInstance().getDatabaseLock().lock();
        try {
            DiskOperations.createDatabase(databaseName);
            IndexManager.getInstance().addDatabase(databaseName);
        }catch (Exception e){
            IndexManager.getInstance().getDatabaseLock().unlock();
            throw new RuntimeException(e);
        }
        IndexManager.getInstance().getDatabaseLock().unlock();
    }
    public static void deleteDatabase(String databaseName) throws IOException {
        IndexManager.getInstance().getDatabaseLock().lock();
        try {
            DiskOperations.deleteDatabase(databaseName);//hard delete
            IndexManager.getInstance().deleteDatabase(databaseName);//delete from the index structure
        }catch (Exception e){
            IndexManager.getInstance().getDatabaseLock().unlock();
            throw new RuntimeException(e);
        }
        IndexManager.getInstance().getDatabaseLock().unlock();

    }
}
