

/**
 *
 * @Jacob Salazar
 */



public class RequestPacket extends Packet {
    private final String mode = "Octet";

    public RequestPacket(){
        super(RRQ);
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
        super.message = packet;
        return packet;
    }

}
