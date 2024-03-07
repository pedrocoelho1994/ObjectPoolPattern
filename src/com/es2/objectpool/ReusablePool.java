package com.es2.objectpool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReusablePool {
    static ReusablePool instance = null;
    int poolSize = 10;
    ArrayList<HttpURLConnection> connections = new ArrayList<>();
    ArrayList<HttpURLConnection> isAvailable = new ArrayList<>();

    private ReusablePool(){}

    public static ReusablePool getInstance(){
        if(instance == null){
            instance = new ReusablePool();
        }

        return instance;
    }

    public synchronized HttpURLConnection acquire() throws PoolExhaustedException, IOException {
        URL url = new URL("https://www.ipv.pt/");

        if (connections.size() >= poolSize) {
            throw new PoolExhaustedException("The object pool has no empty spaces!");
        }

        HttpURLConnection connection = null;

        if(isAvailable.isEmpty()){
            connection = (HttpURLConnection) url.openConnection();
        } else {
            connection = isAvailable.get(0);
            isAvailable.remove(0);
            connection.connect();
        }
        connections.add(connection);
        return connection;
    }

    public synchronized void release(HttpURLConnection conn) throws ObjectNotFoundException {
        if (!connections.contains(conn)){
            throw new ObjectNotFoundException("No object pool was found...");
        }

        conn.disconnect();
        isAvailable.add(conn);
        connections.remove(conn);
    }

    public synchronized void resetPool(){
        isAvailable.clear();

        for (HttpURLConnection connection : connections){
            connection.disconnect();
        }

        connections.clear();
    }

    public synchronized void setMaxPoolSize(int size){
        poolSize = size;
    }
}
