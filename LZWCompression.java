package LZWCompression;
/* Shreya Sainathan created on 4/17/2020 inside the package - LZWCompression */


/** Commands for zipping and unzipping
 * shortwords.txt
 * java LZWCompression -c  shortwords.txt zippedFile.txt
 * java LZWCompression -d  zippedFile.txt trial.txt
 * words.txt
 * java LZWCompression -c  words.txt zippedFileWords.txt
 * java LZWCompression -d  zippedFileWords.txt trial2.txt
 * csv
 * java LZWCompression -c CrimeLatLonXY.csv zipedCSV.txt
 * java LZWCompression -d zipedCSV.txt unzipcsv.csv
 * video file
 * java LZWCompression -c -v 01_Overview.mp4 mp4compressed.txt
 * java LZWCompression -d -v mp4compressed.txt unzipOverview.mp4
 */

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * This class takes the user's command for compression and decompression, applies LZW algorithm of compression and decompression
 * and generates the zip or unzip file
 */

public class LZWCompression {

    static BigInteger bytesRead=BigInteger.ZERO;    //bytes being read from input file
    static BigInteger bytesWritten=BigInteger.ZERO;   //bytes written to output file
    private static HashMap table;  //hashmap for compression
    private static String[] strArr; //array for decompression
    private static byte[] buffer = new byte[3]; //intermediate buffer
    private static boolean isOddEntry = true;   //boolean to signify odd or even entry to output file
    private static int counter; //counter for entering data to table/array

    /**
     * Main class takes input command from the user and drives the program
     * Can compress or decompress files using LZW algorithm
     * @param args program argument
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //User input
        System.out.println("Enter your command -");
        Scanner in = new Scanner(System.in);
        String cmd = in.nextLine();
        //Splitting with space
        String[] commands = cmd.split("\\s");

        String ipFile;  //input file
        String opFile;  //output file
        boolean isCompress; //compress/decompress
        boolean isVerbose = false;  //verbose output or non-verbose output
        if(!(commands.length==5 || commands.length==6)) //if command invalid
        {
            System.out.println("Incorrect command. Please enter the correct command");
            return;
        }
        //if first two words of command are incorrect
        if(!(commands[0].equals("java") && commands[1].equals("LZWCompression")))
        {
            System.out.println("Incorrect command. Please enter the correct command");
            return;
        }

        if(commands.length == 5)    //if non-verbose command
        {
            if(commands[2].equals("-c"))    //compress
            {
                isCompress = true;

            }
            else if(commands[2].equals("-d"))   //decompress
            {
                isCompress = false;
            }
            else    //invalid
            {
                System.out.println("Incorrect command. Please enter the correct command");
                return;
            }
            //setting input and output file names
            ipFile = commands[3];
            opFile = commands[4];
        }

        else    //verbose command
        {
            if(commands[2].equals("-c"))    //compress
            {
                isCompress = true;

            }
            else if(commands[2].equals("-d"))   //decompress
            {
                isCompress = false;
            }
            else    //incorrect command
            {
                System.out.println("Incorrect command. Please enter the correct command");
                return;
            }

            if(commands[3].equals("-v"))    //if verbose
            {
                isVerbose=true;
            }
            else    //incorrect
            {
                System.out.println("Incorrect command. Please enter the correct command");
                return;
            }
            //setting input and output file names
            ipFile = commands[4];
            opFile = commands[5];
        }

        if(isCompress)  //if compress command
        {
            compress(ipFile,opFile);
        }
        else    //decompress command
        {
            decompress(ipFile,opFile);
        }

        if(isVerbose)   //if verbose print bytes read and written
        {
            System.out.println("bytes read = "+ bytesRead + " , bytes written = "+ bytesWritten);
        }

    }

    /**
     * Decompresses input file and stores decompressed output file in the path mentioned
     * @param ipFile    file name for input
     * @param opFile    file name for output
     * @throws FileNotFoundException
     */
    private static void decompress(String ipFile, String opFile) throws FileNotFoundException {
        //entering all symbols in the table
        loadArray();
        //Data stream from input file
        DataInputStream in =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(ipFile)));
        //Data stream for output file
        DataOutputStream out =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(opFile)));
        //initialising priorCodeWord for storing the previous code written
        int priorCodeWord = 0;

        try {
            //Reading first and second unsigned byte from the input file
            //read(priorcodeword) and output its corresponding character;
            int b0 = in.readUnsignedByte();
            int b1 = in.readUnsignedByte();
            int b2=0;   //initialising b2 for next read
            bytesRead = bytesRead.add(BigInteger.TWO);  //updating bytes read
            //Extracting the first nibble from b1 and constructing the input in 12 bits
            priorCodeWord = ((b0<<4) | ((b1 & 0xF0)>>4)) & 0xFFF;
            //Writing to the output file
            if(strArr[priorCodeWord]!=null)
            {
                out.writeBytes(strArr[priorCodeWord]);
                bytesWritten = bytesWritten.add(new BigInteger(String.valueOf(strArr[priorCodeWord].length())));  //updating bytes written
                isOddEntry = !isOddEntry;   //since one write is made, isoddentry becomes false
            }
            while(true)
            {
                //reading code word
                int codeWord =0;
                if(isOddEntry)  //if odd entry, b0 and b1 take the input from the input file
                {
                    //Reading 2 bytes
                    b0 = in.readUnsignedByte();
                    b1 = in.readUnsignedByte();
                    bytesRead = bytesRead.add(BigInteger.TWO);  //updating bytes read
                    //Constructing codeWord, by shifting b0 by 4 bytes to the left, and doing bitwise OR with
                    //last for bits of b1 so in total we have 12 bits
                    codeWord = ((b0<<4) | ((b1 & 0xF0)>>4)) & 0xFFF;
                }
                else    //if even entry, codeword is extracted from b1 nd b2
                {
                    //Reading next byte into b2
                    b2 = in.readUnsignedByte();
                    bytesRead =  bytesRead.add(BigInteger.ONE);  //updating bytes read
                    //Constructing codeword from last 4 bits of b1 and dong bitwise or with b2
                    codeWord = ((b1 & 0xF)<<8 | b2) & 0xFFF;
                }
                isOddEntry=!isOddEntry; //negating isoddentry for each write
                if(strArr[codeWord]==null)  //if the string for the codeword is not present
                {
                    String string = strArr[priorCodeWord]+ strArr[priorCodeWord].charAt(0);
                    //enter string(priorcodeword) + firstChar(string(priorcodeword)) into the table;
                    strArr[counter++] = string;
                    //output string(priorcodeword) + firstChar(string(priorcodeword));
                    out.writeBytes(string);
                    bytesWritten =  bytesWritten.add(new BigInteger(String.valueOf(string.length())));  //updating bytes written
                }
                else    //if the string for the codeword is present
                {
                  //  enter string(priorcodeword) + firstChar(string(codeword)) into the table;
                    strArr[counter] = new String(strArr[priorCodeWord]+strArr[codeWord].charAt(0));
                    counter++;
                    //output string(codeword);
                    out.writeBytes(strArr[codeWord]);
                    bytesWritten = bytesWritten.add(new BigInteger(String.valueOf(strArr[codeWord].length())));  //updating bytes written
                }
                priorCodeWord = codeWord;   //assigning codeword to priorcodeword
                if(counter == 4096) //checking for overflow
                {
                    loadArray();    //reloading the array
                    isOddEntry = true;  //resetting isOddEntry
                     b0 = in.readUnsignedByte();    //Fetching new priorcodeword
                     b1 = in.readUnsignedByte();
                    bytesRead = bytesRead.add(BigInteger.TWO);  //updating bytes read
                     b2=0;
                    //Extracting the first nibble from b1 and constructing the input in 12 bits
                    priorCodeWord = ((b0<<4) | ((b1 & 0xF0)>>4)) & 0xFFF;
                    //writing the string corresponding to priorcodeword to the output file
                    if(strArr[priorCodeWord]!=null)
                    {
                        out.writeBytes(strArr[priorCodeWord]);
                        bytesWritten = bytesWritten.add(new BigInteger(String.valueOf(strArr[priorCodeWord].length())));  //updating bytes written
                       isOddEntry = !isOddEntry;
                    }
                }
            }

        }
        catch(EOFException e)   //Reaching end of file
        {
            try {
                in.close(); //closing streams
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads the array with strings of characters represnted by integers from 0 to 255(ASCII series)
     * and initialises counter variable to 256
     */
    private static void loadArray() {
        strArr = new String[4096];
        int i=0;
        for(i=0;i<256;i++)
        {
            strArr[i] = String.valueOf((char)i);
        }
        counter = i;
    }

    /**
     * Decompresses input file and stores decompressed output file in the path mentioned
     * @param ipFile    file name for input
     * @param opFile    file name for output
     * @throws IOException
     */
    private static void compress(String ipFile, String opFile) throws IOException {
        //entering all symbols in the table
        loadTable();
        //streams for input and output
        DataInputStream in =
                new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(ipFile)));
        DataOutputStream out =
                new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(opFile)));
        //initialising string s
        String s = null;

        try {
            //read(first character from w into string s)
            int input = in.readUnsignedByte();
           bytesRead = bytesRead.add(BigInteger.ONE);  //updating bytes read
            s = String.valueOf((char)input);
            while(true)
            {
                //read(character c);
                int nextInput = in.readUnsignedByte();
                bytesRead = bytesRead.add(BigInteger.ONE);  //updating bytes read
                char c = (char)nextInput;
                //Making s+c
                String sPlusC = s+ String.valueOf(c);
                //if(s + c is in the table)
                if(table.containsKey(sPlusC))
                {
                    s = new String(sPlusC);     //s=s+c
                }
                else    //if table does not contain s+C
                {
                    //output codeword(s)
                    int codeWord = table.get(s);
                    //if odd entry to output file
                    if(isOddEntry)
                    {
                        //Entering the MSB 8 bits of 12 bit representation of codeWord in buffer[0]
                        //Entering the LSB 4 bits of 12 bit representation of codeWord in buffer[1]
                        buffer[0] = (byte)((codeWord & 0xFFF) >>>4);
                        buffer[1] = (byte)((codeWord & 0xF)<<4);

                    }
                    else    //even entry
                    {
                        //Entering the LSB 8 bits of 12 bit representation of codeWord in buffer[2]
                        byte b2 = (byte)((codeWord & 0xF00)>>>8);
                        //Doing bitwise OR of the MSB 4 bits of b2 and the buffer[1]
                        buffer[1] = (byte)((buffer[1] | b2)& 0xFF);
                        buffer[2] = (byte)(codeWord & 0xFF);
                        //Writing the 3 bytes in buffer to output file
                        out.write(buffer, 0, 3);
                        //updating bytes written
                        bytesWritten =  bytesWritten.add(new BigInteger("3"));
                        //refresh buffer
                        buffer= new byte[3];
                    }
                    isOddEntry = !isOddEntry;   //complement isOddEntry for each entry made to output file
                    table.put(sPlusC, counter); //Enter s + c into the table;
                    counter++;
                    s = String.valueOf(c);  //s=c
                }
                if(counter == 4096) //if file overflows
                {
                    //new string to be read, if the file is at end, the subsequent steps won't be executed
                    int newBatchS = in.readUnsignedByte();
                    bytesRead =  bytesRead.add(BigInteger.ONE);  //updating bytes read
                    String newS = String.valueOf((char)input);
                    //Getting codeWord for the s
                    int codeWord = table.get(s);
                    if(isOddEntry)  //if odd entry
                    {
                        //Entering the MSB 8 bits of 12 bit representation of codeWord in buffer[0]
                        //Entering the LSB 4 bits of 12 bit representation of codeWord in buffer[1]
                        buffer[0] = (byte)((codeWord & 0xFFF) >>>4);
                        buffer[1] = (byte)((codeWord & 0xF)<<4);
                        //Writing the 2 bytes to output file
                        out.write(buffer, 0, 2);
                        //updating bytes written
                        bytesWritten =  bytesWritten.add(new BigInteger("2"));
                    }
                    else
                    {
                        //Entering the LSB 8 bits of 12 bit representation of codeWord in buffer[2]
                        byte b2 = (byte)((codeWord & 0xF00)>>>8);
                        //Doing bitwise OR of the MSB 4 bits of b2 and the buffer[1]
                        buffer[1] = (byte)((buffer[1] | b2)& 0xFF);
                        buffer[2] = (byte)(codeWord & 0xFF);
                        //Writing the 3 bytes to output file
                        out.write(buffer, 0, 3);
                        //updating bytes written
                        bytesWritten =  bytesWritten.add(new BigInteger("3"));
                    }
                    //Reload table
                    loadTable();
                    isOddEntry = true;  //reset isOddEntry
                    s = newS;   //Setting s to newS
                    buffer = new byte[3];   //Reset buffer
                }

            }
        }
        catch(EOFException e)   //At end of file
        {
            //Getting codeWord for the s
            int codeWord = table.get(s);
            if(isOddEntry)  //odd entry
            {
                //Entering the MSB 8 bits of 12 bit representation of codeWord in buffer[0]
                //Entering the LSB 4 bits of 12 bit representation of codeWord in buffer[1]
                buffer[0] = (byte)((codeWord & 0xFFF) >>>4);
                buffer[1] = (byte)((codeWord & 0xF)<<4);
                //Writing the 2 bytes to output file
                out.write(buffer, 0, 2);
                //updating bytes written
                bytesWritten =  bytesWritten.add(new BigInteger("2"));

            }
            else    //even entry
            {
                //Entering the LSB 8 bits of 12 bit representation of codeWord in buffer[2]
                byte b2 = (byte)((codeWord & 0xF00)>>>8);
                //Doing bitwise OR of the MSB 4 bits of b2 and the buffer[1]
                buffer[1] = (byte)((buffer[1] | b2)& 0xFF);
                buffer[2] = (byte)(codeWord & 0xFF);
                //Writing the 3 bytes to output file
                out.write(buffer, 0, 3);
                //updating bytes written
                bytesWritten =  bytesWritten.add(new BigInteger("3"));
            }
            //closing the streams
            in.close();
            out.close();
        }
    }

    /**
     * This method loads the table with strings of characters represnted by integers from 0 to 255(ASCII series)
     * and initialises counter variable to 256
     */
    private static void loadTable()
    {
        table = new HashMap();
        int i;
        for(i=0;i<256;i++)
        {
            table.put(String.valueOf((char)i),i);
        }
        counter = i;
    }
}
