import jdk.jshell.spi.ExecutionControl;

public class MyShopHashTable<K extends Comparable<K>, V extends Comparable<V> & Node<V>> implements MyHashTable<K,V>  {
    private static int DEFAULT_CAPACITY = 17;  // Varsayılan kapasite
    private static double MAX_LOAD_FACTOR = 0.5d;  // Varsayılan kapasite
    private Entry<K, V>[] hashTable;  // Anahtar-değer çiftlerini saklayan dizi
    private int numberOfEntries;  // Hash tablosundaki toplam öğe sayısı
    private boolean integrityOK = false;
    private static final int PRIME_CONSTANT = 33;
    private int type = 0;           //(1)SSF or (0)PAF
    public long collisions = 0;
    int TABLE_SIZE = 0;
    int counter = 0;
    long excludeTime=0;
    int debugFile = 0;
    int hashFile = 0;

    private int probing = 1;//(1)Double Hashing or (0)Linear Probing
    // Anahtarın hash değerini hesaplar

    public MyShopHashTable() {
        hashTable = new Entry[DEFAULT_CAPACITY];
        numberOfEntries = 0;
        integrityOK = true;
    }

    public MyShopHashTable(int size) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;

    }

    public MyShopHashTable(int size, double loadFactor) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;
        MAX_LOAD_FACTOR = loadFactor;

    }

    public MyShopHashTable(int size, int type) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;
        this.type=type;

    }



    public MyShopHashTable(int size, int type, double loadFactor) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;
        this.type=type;
        MAX_LOAD_FACTOR = loadFactor;

    }

    public MyShopHashTable(int size, int type, double loadFactor, int probing) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        TABLE_SIZE=size;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;
        this.type=type;
        this.probing=probing;
        MAX_LOAD_FACTOR = loadFactor;

    }
    public MyShopHashTable(int size, int type, double loadFactor, int probing, int debugFile, int hashFile) {
        if (size<DEFAULT_CAPACITY) size = DEFAULT_CAPACITY;
        TABLE_SIZE=size;
        hashTable = new Entry[size];
        numberOfEntries = 0;
        integrityOK = true;
        this.type=type;
        this.probing=probing;
        this.hashFile = hashFile;
        this.debugFile=debugFile;
        MAX_LOAD_FACTOR = loadFactor;
    }

    private void checkIntegrity()
    {
        if (!integrityOK)
            throw new SecurityException ("HashedDictionary object is corrupt.");
    } // end checkIntegrity



    public void put(K key, V value) {

            checkIntegrity();

        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else {


            int index =  GetHash(key) % TABLE_SIZE;
            debugFile("New Entry index " + index+" "+ value+ "   key " + key );

            // if chain is empty, insert the first entry
            if (hashTable[index] == null) {
                hashTable[index] = new Entry<>(key, value);
                numberOfEntries++;
            } else {
                // Search for the key in the chain

               /*Entry<K, V > current = hashTable[index];

                Entry<K, V> lastNode = null;*/
                //Entry<K ,V> newNode = new Entry<K,V>(key,value);
                V current = hashTable[index].getValue();
                V lastNode = null;
                V newNode= new Entry<K,V>(key,value).getValue();

                // if the key in the index is equals to new key
                // insert to linkedList
                if (hashTable[index].key.equals(key)){
                        PutToTail(key,lastNode,current,newNode,index);
                }else
                {
                    // if keys not matches
                    // Probe Functions
                    switch (probing){
                        case 1: // DOUBLE HASHING
                            index = DoubleHashingMethod(index,key);
                            break;
                        case 0: // LINEAR PROBING
                            index = LinearProbingMethod(index,key);
                            break;
                    }

                    // reached empty location after probe
                    if (hashTable[index] == null)
                    {
                        hashTable[index] = new Entry<>(key, value);
                        numberOfEntries++;
                    }
                    else
                    {
                        // found existing value adding to list
                        current = hashTable[index].getValue();
                        PutToTail(key,lastNode,current,newNode,index);
                    }
                }
            }
            counter++;
            if (counter%50000 ==0) // PROGRESS BAR
            {
                System.out.print("▊");
            }
            float oran = (float) numberOfEntries /hashTable.length;
            debugFile("HashTable doluluk oranı : "+ oran +" " + numberOfEntries +" "+ hashTable.length);
            debugFile("counter = " + counter);
            debugFile("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
            if (isHashTableTooFull())
                resize();


        }
    } // end add

    private void PutToTail(K key,V lastNode,V current,V newNode,int index) {


        while(   current!=null   ) {
            if (newNode.compareTo(current) == 0) {
                debugFile("ESIT TARIH");

                if (lastNode != null) {
                    lastNode.setNext(newNode);
                    newNode.setNext(current);
                } else {
                    current.setNext(newNode);
                }

                break;
            } else if (newNode.compareTo(current) > 0) {
                debugFile("BUYUK TARIH");
                if (lastNode != null) {

                    lastNode.setNext(newNode);
                    newNode.setNext(current);

                } else {
                    Entry<K,V> entry = new Entry<>(key,newNode);
                    hashTable[index] = entry;
                    newNode.setNext(current);
                }
                break;
            } else if (!current.hasNext() && newNode.compareTo(current) < 0) {
                debugFile("KUCUK TARIH");
                current.setNext(newNode);
                newNode.setNext(null);
                break;
            }
            lastNode = current;
            current = (V) current.getNext();
        }
    }


    public int  DoubleHash(int key){return 31-key%31;}
    public boolean isHashTableTooFull() {
        return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
    }
    public void resize() {

        int newCapacity = findNextPrime(hashTable.length * 2);
        TABLE_SIZE = newCapacity;
        debugFile("Resized " + newCapacity );
        Entry<K, V>[] newTable = new Entry[newCapacity];

        // Rehash all existing entries into the new table
        for (Entry<K, V> entry : hashTable) {
            if (entry != null) {
                int index = GetHash(entry.key) % TABLE_SIZE ;
                switch (probing){
                    case 0:
                        while (newTable[index] != null) {
                            index = (index + 1) % newTable.length;
                        }
                        break;
                    case 1:
                        int j = 1;
                        while (newTable[index] != null) {
                            index = (index + j*DoubleHash(GetHash(entry.getKey())))% TABLE_SIZE;//!
                            j++;
                        }
                        break;
                }

                newTable[index] = entry;
            }
        }


        hashTable = newTable;
        debugFile("End of Rehash");

    }
    public static int findNextPrime(int number) {
        while (true) {
            number++;
            if (isPrime(number)) {
                return number;
            }
        }
    }
    private static boolean isPrime(int num) {

        // Corner case
        if (num <= 1)
            return false;

        // Check from 2 to n-1
        for (int i = 2; i < num; i++)
            if (num % i == 0)
                return false;

        return true;
    }
    // Anahtara ait veriyi döndürür
    public V get(K key) {
        //getting entry
        int index = GetHash(key)%TABLE_SIZE;
        Entry<K, V> entry = hashTable[index];

        // if entry null data not found
        if(entry==null || entry.getKey()==null) {
            //hashFile.println("Data Not Found");
            return null;
        }

        // Searching (Probing) the key

        while(!entry.getKey().equals(key)) {
            index = (getProbing(index,key));
            //index = index % hashTable.length;
            entry = hashTable[index];
            if(entry==null || entry.getKey()==null) {
                //hashFile.println("Data Not Found");
                return null;
            }
        }

        return entry.getValue();
    }

    public String search(K key){
        if(get(key) == null) return "[CUSTOMER NOT FOUND]";
        else return get(key).toString();
    }

    // Anahtarın verisini siler
    public void remove(K key)  {
        /*int index = GetHash(key);
        Entry<K, V> entry = hashTable[index];
        Entry<K, V> prev = null;

        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) {
                    hashTable[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                numberOfEntries--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }*/
    }

    public void debugFile(String temp){
        switch (debugFile) {
            case 1 -> debugFile(temp);
            case 2 -> Main.debugWriter.println(temp);
            default -> {
            }
        }
    }

    public void hashFile(String temp){
        switch (hashFile) {
            case 1 -> debugFile(temp);
            case 2 -> Main.hashWriter.println(temp);
            default -> {
            }
        }
    }

    public int GetHash(K key){
        return switch (type) {
            case 0 -> GetHashPAF(key);
            case 1 -> GetHashSSF(key);
            default -> GetHashPAF(key);
        };
    }

    public int GetHashPAF(K key) {
        int hash = 0;

        String keyString = key.toString();
        int n = keyString.length()-1;

        for (int i = 0; i< keyString.length() ; i++)
        {
            char ch = keyString.charAt(i);
            hash += (int)ch * (int)Math.pow(PRIME_CONSTANT, n );
            hash %= TABLE_SIZE;
            n--;
        }

        return  hash;

    }

    private int GetHashSSF(K key){

        int hash = 0;
        String keyString = key.toString();

        for (int i = 0; i< keyString.length() ; i++)
        {
            char ch = keyString.charAt(i);
            hash+=ch;
            hash %= TABLE_SIZE;
        }
        return  hash;
    }
    public int getProbing (int index, K key){
        switch(probing){
            case 0:
                return LinearProbingMethod(index,key);
            case 1:
                return DoubleHashingMethod(index,key);
            default:
                return -1;
        }
    }
    public int DoubleHashingMethod(int index,K key){
        int j = 1;
        while (hashTable[index]!=null)
        {
            if (hashTable[index].key.equals(key)) {
                break;
            }
            index = (index + j*DoubleHash(GetHash(key))) % TABLE_SIZE;
            j++;
            collisions++;
            index %= hashTable.length;

           /* if (hashTable[index] != null)
                debugFile("****"+hashTable[index].getValue());
            debugFile(" DoubleHashed : index " + index+ "   key " + key + ",  : "+ collisions);*/

        }
        return index;
    }

    public int LinearProbingMethod(int index,K key)
    {
        while (hashTable[index]!=null ) {
            if (hashTable[index].key.equals(key))
                break;
            index++;
            collisions++;
            index %= hashTable.length;
            //debugFile(" LinearProbed : index " + index+ "   key " + key + ", collisions : "+ collisions);

        }
        return index;
    }
    // Hash tablosundaki öğe sayısını döndürür
    public int size() {
        return numberOfEntries;
    }

    public static int getDefaultCapacity() {
        return DEFAULT_CAPACITY;
    }

    public static double getMaxLoadFactor() {
        return MAX_LOAD_FACTOR;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }


    public void printAllEntries() {
        excludeTime-=System.nanoTime();
        hashFile("# Number of Entries : " + numberOfEntries);
        hashFile("# Collision Count : " + collisions);
        for (Entry<K, V> entry : hashTable) {
            if (entry != null) {
                printEntry(entry);
            }
        }
        hashFile("End of Hash Table Entries");
        excludeTime += System.nanoTime();
    }

    private void printEntry(Entry<K, V> entry) {
        excludeTime -= System.nanoTime();
        //Main.myWriter.println("\nCustomer : " + entry.getValue() + "\nKey : " + entry.getKey());
        hashFile("\n┣──────────────────────────────────────────────────────────────────────────────────────────────────────────┫");
        hashFile("\n ► Customer Key : " + entry.getKey());
        V temp = entry.getValue();
        while (temp.hasNext()) {
            hashFile(" └── " + temp);
            temp = (V) temp.getNext();
        }
        excludeTime += System.nanoTime();
    }


    /*
        public void printAllEntries() {

            Main.myWriter.println("# Number of Entries : "+ numberOfEntries);
            Entry<K, V> temp = null;
            for (Entry<K, V> entry : hashTable) {
                if (entry != null) {
                    temp=entry;
                    Main.myWriter.println("\nCustomer : "+ ((ShopData)entry.getValue()).getName() +"\nKey : " + entry.getKey() );
                    while(temp.hasNext())
                    {
                        Main.myWriter.println( " └── " + temp.value);
                        temp=temp.getNext();
                    }
                }
            }
            Main.myWriter.println("End of Hash Table Entries");
        }
    */
    private static class Entry<K,V extends Comparable<V> & Node<V> >  {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        public K getKey()  { return key; }
        public void setKey(K key) { this.key = key; }
        public V getValue() { return value; }
        public void setValue(V value) { this.value = value;}
        public Entry<K, V> getNext() { return next; }
        public void setNext(Entry<K, V> next) { this.next = next; }

        public boolean hasNext(){
            return next != null;
        }

    }
    public long getExcludeTime(){
        long temp = excludeTime;
        excludeTime = 0;
        return excludeTime;}
}
// if ((((ShopData)hashTable[index].getValue()).getKey()).equals(((ShopData)value).getKey())){
//if (value.equals(current.getValue())){
//if (current.getValue().compareTo(value) == 0){