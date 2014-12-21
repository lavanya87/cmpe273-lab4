package edu.sjsu.cmpe.cache.client;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");
        CRDTClient crdt = new CRDTClient();

        System.out.println("#######Writing value a to all servers#########");
        System.out.println(crdt.write(1, "a"));
        //System.out.println("put(1 => foo)");

        //String value = cache.get(1);
        //System.out.println("get(1) => " + value);
        
        System.out.println("###### Bringing down server one #########");
        Thread.sleep(30000);

        System.out.println("####### Writing value b  ########"); 
        System.out.println(crdt.write(1, "b"));

        System.out.println("###### Bringing up server one #########"); 
        Thread.sleep(30000);

        System.out.println("###### reading values from all servers #########"); 
        System.out.println(crdt.read(1));
                
        System.out.println("Existing Cache Client...");
    }

}
