

/**
 *
 * @Jacob Salazar
 */


import java.net.*;
import java.io.*;

final class DataPacket extends Packet {

    public static int opCodeSpace=0;
    public static int BlockSpace=2;
    public static int DataSpace=4;
    public static int maxdataSpace=512;
	// Constructors
	protected DataPacket() {}
	public DataPacket(int blockNumber, FileInputStream in) throws IOException {
            super(DATA);
            super.message = new byte[516];
		put(opCodeSpace, DATASHORT);
		put(BlockSpace, (short) blockNumber);
		// read the file into packet and calculate the entire length
		super.length = in.read(super.message, DataSpace, maxdataSpace) + 4;
	}



	public int blockNumber() {
		return this.get(BlockSpace);
	}

	public int write(FileOutputStream out) throws IOException {
		out.write(super.message, BlockSpace, super.length - 4);
		return (super.length - 4);
	}

        protected void put(int at, short value) {
         super.message[at++] = (byte)(value >>> 8);
         super.message[at] = (byte)(value % 256);
            }


   public void send(InetAddress ip, int port, DatagramSocket s) throws IOException {
    s.send(new DatagramPacket(message,length,ip,port));
  }


}
