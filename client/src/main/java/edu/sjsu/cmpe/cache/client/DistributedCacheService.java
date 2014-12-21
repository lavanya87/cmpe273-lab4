package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.concurrent.Future;
import com.mashape.unirest.http.async.Callback;


/**
 * Distributed cache service
 * 
 */


public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;
    HttpResponse<JsonNode> serverResponse = null;
    int responseCode = 204;
    String responseValue = "";

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to add to the cache.");
        }
    }

   @Override
   public int getResponseCode(){
    return responseCode;
   }

   @Override
   public String getResponseValue(){
    return responseValue;
   }


   @Override
   public void delete(long key, String value){
      try {
            serverResponse = Unirest
                    .delete(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
   }

   @Override
    public void putAsync(long key, String value) {
        Future<HttpResponse<JsonNode>> future = Unirest.put(this.cacheServerUrl + "/cache/{key}/{value}")
        .header("accept", "application/json")
        .routeParam("key", Long.toString(key))
        .routeParam("value", value)
        .asJsonAsync(new Callback<JsonNode>() {

        public void failed(UnirestException e) {
          System.out.println("The request has failed");
        }
       
        public void completed(HttpResponse<JsonNode> response) {
           responseCode = response.getCode();
           System.out.println("****** Response code *****:"+responseCode);
           serverResponse = response;
        }

        public void cancelled() {
          System.out.println("The request has been cancelled");
        }

      });
  }

   @Override
    public void getAsync(long key) {
        Future<HttpResponse<JsonNode>> future = Unirest.get(this.cacheServerUrl + "/cache/{key}")
        .header("accept", "application/json")
        .routeParam("key", Long.toString(key))
        .asJsonAsync(new Callback<JsonNode>() {

        public void failed(UnirestException e) {
          System.out.println("The request has failed");
        }
       
        public void completed(HttpResponse<JsonNode> response) {
           System.out.println("****serverResponse****");
           String value = response.getBody().getObject().get("value").toString();
           System.out.println(value);
           responseValue = value;
        }

        public void cancelled() {
          System.out.println("The request has been cancelled");
        }

      });

    }
}
