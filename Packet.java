

/**
 *
 * @author Jacob Salazar
 */


import java.net.*;
import java.io.*;

//This class is the parent template of all the packet type, all of the needed attributes, Byte Spaces, Opcodes, and etc.
// are placed inside the class
public class Packet {

    //This indicates the port number of the Server which is port 69
    public static int TFTP_PORT = 69;
    // Indicates the Max default bytes that the data packet will store
    public static int maxTFTP_DATA =512;

    // default byte size of the a general packet
    protected static int MAXPACKET_SIZE =516;

  // opCodes
  protected static final short RRQSHORT=1;
  protected static final short WRQSHORT=2;
  protected static final short DATASHORT=3;
  protected static final short ACKSHORT=4;
  protected static final short ERRORSHORT=5;


    protected static byte zero = 0;
    protected static byte RRQ = 1;
    protected static byte WRQ = 2;
    protected static byte DATA = 3;
    protected static byte ACK = 4;
    protected static byte ERR = 5;
    protected static byte OACK = 6;

   /// attributes of a General Packet, OpCode, message, and the length
    protected byte Code;
    protected byte[] message;
    protected int length;

    /// Address information
    protected InetAddress host;
    protected int port;


    public Packet(){
        message = new byte[MAXPACKET_SIZE];
        length = MAXPACKET_SIZE;
    }

    public Packet(byte Code){
        this.Code = Code;
        this.length = MAXPACKET_SIZE;
    }


   ///Getters and Setters
    public byte[] getMessage(){
        return message;
    }
    public void setMessage(byte[] message){
        this.message = message;
    }
    public byte getCode(){
        return Code;
    }
    public int getLength(){
        return length;
    }


    public static Packet receive(DatagramSocket sock) throws IOException {
    Packet dummyPacket = new Packet();
    Packet receivePacket =new Packet();
    //receive data and put them into in.message
    DatagramPacket inPak = new DatagramPacket(dummyPacket.message,dummyPacket.length);
    sock.receive(inPak);


    switch (dummyPacket.get(0)) {
      case RRQSHORT:
    	  receivePacket = new RequestPacket();
        break;
      case WRQSHORT:
    	  receivePacket = new WritePacket();
        break;
      case ACKSHORT:
    	  receivePacket = new ACKPACKET();
        break;
    }

    receivePacket.message=dummyPacket.message;
    receivePacket.length= inPak.getLength();
    receivePacket.host = inPak.getAddress();
    receivePacket.port = inPak.getPort();

    return receivePacket;
  }




    // DatagramPacket like methods
  public InetAddress getAddress() {
    return host;
  }

  public int getPort() {
    return port;
  }


  protected int get(int at) {
    return (message[at] & 0xff) << 8 | message[at+1] & 0xff;
  }















}



