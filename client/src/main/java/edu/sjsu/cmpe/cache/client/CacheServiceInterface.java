package edu.sjsu.cmpe.cache.client;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);
    public void put(long key, String value);
    public void putAsync(long key,String value);
    //public void write(long key, String value);
    public int getResponseCode();
    public String getResponseValue();
    public void delete(long key, String value);
    public void getAsync(long key);
}
