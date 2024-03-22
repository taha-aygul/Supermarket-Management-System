import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static MyShopHashTable<String,ShopData<String> > hashMap;
    public static PrintWriter debugWriter , hashWriter , searchWriter;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        double loadFactor = 0.5d;
        int hash = 0;
        int size = 11;
        int probing = 1;
        int debugfile = 0;
        int hashfile = 0;
        long rt;
        long st;
        String sett;
        String filePath = "C:\\Users\\asus\\IdeaProjects\\CME2201_Assignment1_SupermarketManagementSystem\\src";
//C:\Users\asus\IdeaProjects\CME2201_Assignment1_SupermarketManagementSystem
        //C:\Users\emrek\Desktop\src\
        boolean exitProgram = false;
        while (!exitProgram) {
            try {
                System.out.print("[TABLE SIZE] \tInput table size: ");
                //size = console.nextInt();
                String temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) { size = 17; }
                else{ size = Integer.parseInt(temp);
                    if (size < 11) { size = 11;}}
                System.out.print("# Table Size = "+size+"\n");

                System.out.print("\n# (\"paf\": Polynomial Accumulation Function, \"ssf\": Simple Summation Function)\n");
                System.out.print("[HASH FUNCTION] Input (\"SSF\" or \"PAF\"): ");
                temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) { hash = 0; System.out.print("# PAF\n");}
                else{
                    if (temp.equalsIgnoreCase("SSF")) { hash = 1; System.out.print("# SSF\n");}
                    else if (temp.equalsIgnoreCase("PAF")) { hash = 0; System.out.print("# PAF\n");}
                    else { throw new IllegalArgumentException("Invalid input for hash function. Use \"SSF\" or \"PAF\"."); }
                }

                System.out.print("\n[LOAD FACTOR] \tInput load factor: ");
                temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) {
                    loadFactor = 0.5;
                } else {
                    loadFactor = Double.parseDouble(temp);
                    if (loadFactor >= 1 || loadFactor <= 0) {
                        loadFactor = 0.5;
                    }
                }
                System.out.print("# λ = " + loadFactor +"\n");

                System.out.print("\n# (\"dh\": Double Hashing, \"lp\": Linear Probing) \n");
                System.out.print("[PROBING] \t\tInput (\"DH\" or \"LP\"): ");
                temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) {
                    probing = 1;
                    System.out.print("# Double Hashing will be used in case of a collision.\n");}
                else {
                    if (temp.equalsIgnoreCase("DH")) {
                        probing = 1;
                        System.out.print("# Double Hashing will be used in case of a collision.\n");
                    } else if (temp.equalsIgnoreCase("LP")) {
                        probing = 0;
                        System.out.print("# Linear Probing will be used in case of a collision.\n");
                    } else {
                        throw new IllegalArgumentException("Invalid input for probing method. Use \"DH\" or \"LP\".");
                    }
                }
                System.out.print("\n# (0:nowhere, 1:console, 2:text file)\n");
                System.out.print("[DEBUG] \t\tWhere do you want to output debug logs? : ");
                temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) {
                    debugfile = 0;
                    System.out.print("# Debug outputs will be logged to console.\n");
                } else {
                    if (temp.equalsIgnoreCase("nowhere") || temp.equalsIgnoreCase("0")) {
                        debugfile = 0;
                        System.out.print("# Debug outputs won't be logged.\n");
                    } else if (temp.equalsIgnoreCase("console") || temp.equalsIgnoreCase("1")) {
                        debugfile = 1;
                        System.out.print("# Debug outputs will be logged to console.\n");
                    } else if (temp.equalsIgnoreCase("text file") || temp.equalsIgnoreCase("2")) {
                        debugfile = 2;
                        System.out.print("# Debug outputs will be logged to [debug.txt].\n");
                    } else {
                        throw new IllegalArgumentException("Invalid input. Please input \"yes\" or \"no\".");
                    }
                }
                System.out.print("\n# (0: nowhere, 1: console, 2: text file)\n");
                System.out.print("[HASH] \t\tWhere do you want to output hash table logs? : ");
                temp = console.nextLine();
                if (temp.equalsIgnoreCase("")) {
                    hashfile = 1;
                    System.out.print("# Hash Table outputs will be logged to console.\n");
                } else {
                    if (temp.equalsIgnoreCase("nowhere") || temp.equalsIgnoreCase("0")) {
                        hashfile = 0;
                        System.out.print("# Hash Table will not log outputs.\n");
                    } else if (temp.equalsIgnoreCase("console") || temp.equalsIgnoreCase("1")) {
                        hashfile = 1;
                        System.out.print("# Hash Table outputs will be logged to console.\n");
                    } else if (temp.equalsIgnoreCase("text file") || temp.equalsIgnoreCase("2")) {
                        hashfile = 2;
                        System.out.print("# Hash Table outputs will be logged to [debug.txt].\n");
                    } else {
                        throw new IllegalArgumentException("Invalid input. Please input \"yes\" or \"no\".");
                    }
                }
                sett =init(size, hash, loadFactor, probing, debugfile, hashfile);
                rt = ReadCSV(filePath);
                Search(filePath,rt,sett);


            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                // İsterseniz hata durumunda programın devam etmesine karar verebilirsiniz.
                // Ancak burada bir hatanın olması durumunda kullanıcıya bir geri bildirim vermek önemlidir.
            }

            // Devam eden kod...

            System.out.print("Do you want to restart the program? (yes): ");
            //console.nextLine();
            String restartInput = console.nextLine();

            if (!restartInput.equals("yes")) {
                exitProgram = true;
                System.out.println();
                //console.next();
                console.close();
            }
        }


    }

    private static void Search(String filePath, long rt, String sett) {
        String search       = filePath + "\\Datasets\\customer_1K.txt";
        String searchResult       = filePath + "\\Results\\SearchResult.txt";
        String dataset      = filePath + "supermarket_dataset_50k.csv";
        try {
            long searchTime = System.nanoTime();
            System.out.println("# Searching ["+ search.replace(filePath,"") +"]...");
            //System.out.print("# ");
            var scanner = new Scanner(new File(search));//new scanner
            searchWriter = new PrintWriter(searchResult);
            long maxTime = -50;
            long minTime = Long.MAX_VALUE;
            long temp;
            while (scanner.hasNextLine()) {
                temp = System.nanoTime();
                String id = scanner.nextLine();

                hashMap.search(id);
                ShopData searched = hashMap.get(id);
                ShopData tempData = searched;
                if (tempData != null){
                    searchWriter.println("\n[Customer] : '"+ tempData.getName() + "'  [Key] : '" + tempData.getKey()+"'");
                    while(tempData.hasNext()){
                        searchWriter.println(tempData);
                        tempData = (ShopData) tempData.getNext();
                    }

                }else {
                    searchWriter.println("\n[Customer] :  [CUSTOMER DATA NOT FOUND] : " + id);
                }

                temp =  System.nanoTime() - temp ;
                if(temp<minTime) { minTime = temp;}
                if(temp>maxTime) { maxTime = temp;}


            }
            System.out.println("\n# Search completed ["+ dataset.replace(filePath,"") +"]...");
            searchTime = System.nanoTime() - searchTime;
            hashMap.printAllEntries();
            System.out.println(sett);
            System.out.println("# hashtable total runtime                : "+ rt/1000000+"ms or " + rt + " nano seconds");
            System.out.println("# runtime without outputting to txt/cmd  : "+ ((double)(rt-hashMap.getExcludeTime())/1000000)+"ms or " +(rt-hashMap.getExcludeTime()) +" nano seconds");
            System.out.println("# collisions                             : "+ hashMap.collisions);

            System.out.println("# search time                            : "+ searchTime/1000000+"ms or "+searchTime+" nano seconds");
            System.out.println("# max search time                        : "+ maxTime/1000000+"ms or "+maxTime +"nano seconds");
            System.out.println("# min search time                        : "+ minTime/1000000+"ms or " + minTime+"nano seconds");

            debugWriter.close();
            hashWriter.close();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + dataset.replace(filePath,""));
            e.printStackTrace();
        }

    }

    public static String init(int size, int type, double loadFactor, int probing, int debugFile,int hashFile){
        //System.out.print("# [size:"+size+"],[ssf:"+type+"],[lambda:"+loadFactor+"],[dh:"+probing+"]");
        //System.out.println();

        hashMap = new MyShopHashTable<>(size, type, loadFactor, probing,debugFile,hashFile);
        System.out.println( "\n" +
                "# Hash Table initiated.");

        String settings = ("\n# [size:"+size+"],");
        if(type==0) settings += ("[paf],"); else settings += ("[ssf],");
        if(probing==1) settings += ("[dh],"); else settings += ("[lp],");
        if(hashFile==0) settings += ("[hash:no],"); else if(hashFile==1)settings += ("[hash:cmd],"); else settings += ("[hash:txt],");
        if(debugFile==0) settings += ("[debug:no],"); else if(debugFile==1)settings += ("[debug:cmd],"); else settings += ("[debug:txt],");
        settings += ("[λ:" + loadFactor+"]\n");
        System.out.print(settings);
        return settings;
        /*
        System.out.print("\n# [size:"+size+"],");
        if(type==0) System.out.print("[paf],"); else System.out.print("[ssf],");
        if(probing==1) System.out.print("[dh],"); else System.out.print("[lp],");
        if(hashFile==0) System.out.print("[hash:no],"); else if(hashFile==1)System.out.print("[hash:cmd],"); else System.out.print("[hash:txt],");
        if(debugFile==0) System.out.print("[debug:no],"); else if(debugFile==1)System.out.print("[debug:cmd],"); else System.out.print("[debug:txt],");
        System.out.print("[λ:" + loadFactor+"]\n");*/

    }


    public static long ReadCSV(String filePath){
        String dataset      = filePath+ "\\Datasets\\supermarket_dataset_50k.csv";
        String debug        = filePath+ "\\Datasets\\debugLog.txt";
        String hashtable    = filePath+ "\\Results\\HashTable.txt";
        long runtime = System.nanoTime();
        try {

            System.out.println("# Importing ["+ dataset.replace(filePath,"") +"]...");
            System.out.print("# ");
            var scanner = new Scanner(new File(dataset));//new scanner
            scanner.nextLine();//skips the column names

            debugWriter = new PrintWriter(debug);
            hashWriter = new PrintWriter(hashtable);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");

                ShopData<String> newShopData = new ShopData<String>(fields[0],fields[1],CreateDate(fields),fields[3] );
                hashMap.put(fields[0],newShopData );

            }
            System.out.println("\n# Imported ["+ dataset.replace(filePath,"") +"]...");
            runtime = System.nanoTime()-runtime;
            hashMap.printAllEntries();

            //System.out.println("# hashtable runtime                      : "+ runtime+"ms");
            //System.out.println("# runtime without outputting to txt/cmd  : "+ (runtime-hashMap.getExcludeTime())+"ms");
            //.out.println("# collisions                             : "+ hashMap.collisions);
            debugWriter.close();
            hashWriter.close();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + dataset.replace(filePath,""));
            e.printStackTrace();
        }
        return runtime;
    }

    /*public void printAllEntries(MyHashTable<K,V> hashTable)  {

        System.out.println("# Number of Entries : "+ hashMap.getNumberOfEntries);
        MyHashTable.Entry<K, V> temp = null;
        for (MyHashTable.Entry<K, V> entry : hashTable) {
            if (entry != null) {
                temp=entry;
                System.out.println("\nCustomer : "+ ((ShopData<String>)entry.getValue()).getName() +"\nKey : " + entry.getKey() );
                while(temp.hasNext())
                {
                    System.out.println( " └── " + temp.value);
                    temp=temp.getNext();
                }
            }
        }
        System.out.println("End of Hash Table Entries");
    }*/
    private static Date CreateDate(String[] value) {

        int[] dateInt= new int[3];
        String[] dateString = value[2].split("-");
        for (int i= 0; i < dateString.length ; i++)
        {
            dateInt[i]= Integer.parseInt(dateString[i]);
        }

        return new Date(dateInt[0], dateInt[1],dateInt[2] );
    }
}


