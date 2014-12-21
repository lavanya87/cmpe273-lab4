package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;



/**
 * Distributed cacheServers service
 * 
 */


public class CRDTClient {
    private final ArrayList<CacheServiceInterface> cacheServers;
    private final HashMap<String,Integer> valueMap = new HashMap <String,Integer>();
 
    public CRDTClient() {
       cacheServers = new ArrayList<CacheServiceInterface>();
       cacheServers.add(new DistributedCacheService("http://localhost:3000"));
       cacheServers.add(new DistributedCacheService("http://localhost:3001"));
       cacheServers.add(new DistributedCacheService("http://localhost:3002"));
    }

    public String write(long key, String value) {
        HttpResponse<JsonNode> response;
        int validResponseCount = 0;
        int code;
        System.out.println("********Writing to caches asynchronously*****");
        for(CacheServiceInterface cache : cacheServers ){
          cache.putAsync(key,value);    
        }
        try{ 
          Thread.sleep(4000);
        }catch(InterruptedException e){
            e.printStackTrace(System.out);
        }
        //countDownLatch.await();
        System.out.println("*******Checking if write was successful*******");
        for(CacheServiceInterface cache : cacheServers ){
          code = cache.getResponseCode();
          if(code == 200){
            validResponseCount++;
          }
        }
        if(validResponseCount<2){
          System.out.println("*******Rollback in progress!*********");
          for(CacheServiceInterface cache : cacheServers ){
            if(cache.getResponseCode()==200)
              cache.delete(key,value);
          }
          return("Write Failed:RollBack");
        }
       return("Write Success");
   }

  
    public String read(long key) {
        String value;
        int count=0;
        String majorityValue ="";

        System.out.println("********reading caches asynchronously*****");
        for(CacheServiceInterface cache : cacheServers ){
          cache.getAsync(key);    
        }
        try{ 
          Thread.sleep(4000);
        }catch(InterruptedException e){
            e.printStackTrace(System.out);
        }
        //countDownLatch.await();
        System.out.println("*******Checking read values*******");
        for(CacheServiceInterface cache : cacheServers ){
          value = cache.getResponseValue();
          if(valueMap.containsKey(value)){
            count = valueMap.get(value);
            valueMap.replace(value,count+1);
            majorityValue = value;
          } 
          else
            valueMap.put(value,1);
        } 
       
        if(valueMap.get(majorityValue)<3){
          System.out.print("*********Read Repair**********\n");
          for(CacheServiceInterface cache : cacheServers ){
            if(cache.getResponseValue()!=majorityValue){
                cache.put(key,majorityValue); 
            }
          }
        }
        return(majorityValue);   
    }
}
