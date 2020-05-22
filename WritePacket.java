
/**
 *
 * @Jacob Salazar
 */


import java.net.*;
import java.io.*;

public class WritePacket extends Packet {
    private final String mode = "Octet";

    public WritePacket(){
        super(WRQ);
    }

    public byte[] Create(String filename){
        int headerlength = 2 + filename.length() + 1 + mode.length() + 1;
        byte[] packet = new byte[headerlength];
        int ZeroSpace = 0;

        int index =  0;
        packet[index] = zero;
        index++;
        packet[index] = this.getCode();
        index++;

        for (int i = 0; i < filename.length(); i++){
            packet[index] = (byte) filename.charAt(i);
            index++;
        }

        packet[index] = zero;
        index++;


        for (int i = 0; i < mode.length(); i++){
            packet[index] = (byte) mode.charAt(i);
            index++;
        }

        packet[index] = zero;
        index++;
        super.message = message;
        return packet;
    }

    //sending method
   public void send(byte[] msg,InetAddress ip, int port, DatagramSocket s) throws IOException {
    s.send(new DatagramPacket(msg,this.length,ip,port));
  }





}
