package LZWCompression;
/* Shreya Sainathan created on 4/14/2020 inside the package - LZWCompression */

/**
 * This class implements HashMap using an array of linkedlists. LinkedList is implemented as an inner class of HashMap
 * The size of the array is 127, the map data is stored in chains in each index
 */

public class HashMap {

    //Table size required
    static private int TABLE_SIZE = 127;
    //Array of linkedlist
    private LinkedList[] chain;


    //Defining linked list as an inner class
    class LinkedList
    {
        //Key, value and next
        String key;
        int value;
        LinkedList next;

        //constructor for linked list class
        LinkedList(String k, int v)
        {
            this.key = k;
            this.value = v;
            this.next = null;
        }
    }

    //constructor of hashmap class
    HashMap()
    {
        chain = new LinkedList[TABLE_SIZE];
    }

    /**
     * Returns the value associated with the key , -1 if key not found
     * @param k String for which value needs to be found
     * @return the value associated with key, -1 if key not found
     */
    int get(String k)
    {
        int i = fetchIndex(k);  //index for the key mentioned
        LinkedList list = chain[i]; //linkedlist in that index
        while(list!=null)   //until we find the key given, we iterate through the list
        {
            if(list.key.equals(k))  //if they key is found, return the corresponding value
            {
                return list.value;
            }
            list=list.next; //if key not found, skip to next node in the list
        }

        return -1;  //-1 if the key not found in the list
    }

    /**
     * Given key, k, the method searches the hashmap if the key is present
     * @param k key to look-up
     * @return true if key is present, false if key is not present
     */
    boolean containsKey(String k)
    {
        int i = fetchIndex(k);  //index for the key mentioned
        LinkedList list = chain[i]; //list in the index specified
        while(list!=null)   //iterate through the list
        {
            if(list.key.equals(k))  //if key is found, return true
            {
                return true;
            }
            list=list.next; //if key not found, skip to next node of the list
        }
        return false;   //false when key not found or when the chain in that index is null
    }

    /**
     * This method puts the key, value in the map, if key already present, it replaces the value
     * @param k String value which is key
     * @param v the integer value representative pf the 12 bit representation of the String
     */
    void put(String k, int v)
    {
        int i = fetchIndex(k);  //index for given key

        if(chain[i]==null)  //if chain in that index is null, add to the chain's first node
        {
            chain[i]= new LinkedList(k,v);
            return;
        }

        LinkedList list = chain[i]; //if chain is not empty, get chain[i] in list object
        while(list!=null)   //iterate through the list until it becomes null
        {
            if(list.key.equals(k)) //if key matches, replace the value with given value
            {
                list.value = v;
                return;
            }
            else    //if key does not match
            {
                if(list.next ==null)    //if the next node is null, add the given key value pair to the next node
                {
                    list.next = new LinkedList(k,v);
                    return;
                }
            }
            list = list.next;   //point list to next node
        }

    }

    /**
     * This method returns the index in which the key could be present
     * @param key key to be looked for in the table
     * @return the index at which the key could be present/added
     */
    private int fetchIndex(String key) {

        //hashvalue of the key
        int hashValue = key.hashCode();
        //Finding the modulus of the absolute value of hash(hashCode method returns negative values too)
        //returning index
        return Math.abs(hashValue) % TABLE_SIZE;
    }

}
