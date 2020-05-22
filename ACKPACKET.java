


/**
 *
 * @author Salazar
 */


public class ACKPACKET extends Packet {
    public static byte[] blkNumber;
    public static int BLKSPACE=2;

    //calls the parent class and assigns the OpCODE
    public ACKPACKET(){
        super(ACK);
    }

    public ACKPACKET(byte[] packetCount){
        super(ACK);
        ACKPACKET.blkNumber = packetCount;
    }

    public byte[] Create(){
        int headerlength = 2 + blkNumber.length;
        byte[] packet = new byte[headerlength];
        int index = 0;

        packet[index] = zero;
        index++;
        packet[index] = this.getCode();
        index++;
        for(int i = 0; i < blkNumber.length; i++){
            packet[index] = blkNumber[i];
            index++;
        }
        super.message = packet;
        return packet;
    }

        public int blockNumber() {
		return this.get(BLKSPACE);
	}

    public byte[] getBlockNumber(){
        return blkNumber;
    }
}
