
/**
 *
 * @author Jacob Salazar
 */


import java.util.*;
import java.net.*;
import java.io.*;

public class Network {

    private DatagramSocket datagramSocket = null;
    private InetAddress inetAddress = null;
    private DatagramPacket outBoundDatagramPacket;
    private DatagramPacket inBoundDatagramPacket;
    private final static int PACKET_SIZE = 516;
    private final static int TIMEOUT = 5;

    private byte[] uploadByteArray;
    private byte[] requestByteArray;
    private byte[] bufferByteArray;

    private static final int TFTP_DEFAULT_PORT = 69;

    // TFTP OP Code
	private static final byte OP_RRQ = 1;
	private static final byte OP_DATAPACKET = 3;
	private static final byte OP_ACK = 4;
	private static final byte OP_ERROR = 5;
	private static boolean result = true; // assumes no error

    public static void main(String[] args) throws IOException {
        Network nw = new Network();
        String fileName;
        String newFile;
        String Server_IP ="";
        int option = 0;
        Scanner in = new Scanner(System.in);

        boolean bExit = true;



       while(bExit){

              do{
             System.out.println();
             System.out.println("     [MAIN PROGRAM MENU]           ");
             System.out.println("[1] - Download a File");
             System.out.println("[2] - Upload a File");
             System.out.println("[3] - Exit Program");
             System.out.print("SELECTED OPTION:  ");
             option = in.nextInt();
             in.nextLine();


              }while(option != 1 && option != 2 && option != 3);


               if (option == 1){
                 System.out.println("-----------------[Download Mode]---------------- ");
                 System.out.println("ENTER IP ADDRESS: ");
                 Server_IP = in.nextLine();
                 System.out.println("Enter the filename: ");
                 fileName = in.nextLine();
                 nw.Download(fileName,Server_IP);

                 if (result)
                    System.out.println("Download Success!");
                else
                    System.out.println("Download Fail!");


                 }else if (option == 2){
                System.out.println("-----------------[Upload Mode]---------------- ");
                 System.out.println("ENTER IP ADDRESS: ");
                 Server_IP = in.nextLine();
                System.out.println("Enter the filename: ");
                fileName = in.nextLine();
                System.out.println("Upload file as : ");
                newFile = in.nextLine();
                nw.Upload(fileName,newFile,Server_IP);

                if (result)
                    System.out.println("Upload Success!");
                else
                    System.out.println("Upload Fail!");


                }
                 else if (option == 3){

                     System.out.println("-----------GOOODBYTE-------------");
                     bExit = false;

                 }
                  result = true;



       }








    }



    private void Download(String fileName,String Server_IP) throws IOException {

    try{

        inetAddress = InetAddress.getByName(Server_IP);
	datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(2000); /// SET THE TIMEOUT -> 2 secs
	RequestPacket p = new RequestPacket();
        requestByteArray =  p.Create(fileName);
	outBoundDatagramPacket = new DatagramPacket(requestByteArray,
	requestByteArray.length, inetAddress, TFTP_DEFAULT_PORT);
        System.out.println("Loading......... please wait");
	datagramSocket.send(outBoundDatagramPacket);

	ByteArrayOutputStream byteOutOS = receiveFile(fileName);


	writeFile(byteOutOS, fileName);

	}catch (SocketTimeoutException t) {
                System.err.println("Error Cannot Connect to the Server.Try Again");
                result = false;

        }
    }

    private void Upload(String fileName,String newFile,String Server_IP) throws IOException {

    try{

        inetAddress = InetAddress.getByName(Server_IP);
	datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(2000); // SET THE TIMEOUT -> 2 SECOND
	WritePacket p = new WritePacket();
        requestByteArray =  p.Create(newFile);


        outBoundDatagramPacket = new DatagramPacket(requestByteArray,
	requestByteArray.length, inetAddress, TFTP_DEFAULT_PORT);
        System.out.println("Loading......... please wait");
        //send the Write Packet Request to the server
        datagramSocket.send(outBoundDatagramPacket);

	 sendFile(fileName,newFile);

	}catch (SocketTimeoutException t) {
                System.out.println("Error Cannot Connect to the Server. Try Again");
                result = false;
        }
    }

    //sends Write Request to the TFTP server
    private void sendFile(String fileName,String newFile) throws IOException{
       try {

        Packet pckt =  Packet.receive(datagramSocket);
         int TFTPport = pckt.getPort();
        // check if it is an acknowledgement packet
        if (  pckt instanceof ACKPACKET)
        {
            ACKPACKET ACK = (ACKPACKET) pckt;
            System.out.println("Uploading the selected file....");
        }



        int bytesRead = PACKET_SIZE;

         FileInputStream source = new FileInputStream(fileName);

        int timeout = 5;
        int blknumber = 1;

        for (int block = 1; bytesRead == PACKET_SIZE ;block++) {
        DataPacket DP = new DataPacket(block, source);
	bytesRead = DP.getLength();
        DP.send(inetAddress, TFTPport, datagramSocket);
        System.out.println("Uploading: "+ " [ " + fileName+ " ] " + " Data Packet Sending >>"  + block);
        while ( timeout != 0) {
	    try {
		Packet ack = Packet.receive(datagramSocket);
                    if (!(ack instanceof ACKPACKET)) {

                        break;
                        }

		ACKPACKET a = (ACKPACKET) ack;
		if (TFTPport != a.getPort()) {

                        continue;
		}
		if (a.blockNumber() != block) {
                     result = false;
			System.out.println("DATA PACKET WAS LOST");
				throw new SocketTimeoutException("DATA PACKET LOST PLEASE resend packet");
			}
                            break;
                    }catch (SocketTimeoutException t0) {
                         result = false;
		    System.out.println("DATA PACKET RESNET " + block);
                   DP.send(inetAddress, TFTPport, datagramSocket);
	            timeout--;
		   }
		} // end of while
		if (timeout == 0) {
                    result = false;
                    System.out.println("connection failed");
		}


	}
            source.close();
            datagramSocket.close();
    }catch (SocketTimeoutException t) {
	System.out.println("Error Cannot Connect to the Server!");
	result = false;
    }
    catch(FileNotFoundException e){
        reportErrorWRQ();
    }


    }





      private ByteArrayOutputStream receiveFile(String filename) throws IOException {
	ByteArrayOutputStream byteOutOS = new ByteArrayOutputStream();
		int block = 1;
		do {

			block++;
			bufferByteArray = new byte[PACKET_SIZE];
			inBoundDatagramPacket = new DatagramPacket(bufferByteArray,
					bufferByteArray.length, inetAddress,
					datagramSocket.getLocalPort());




			datagramSocket.receive(inBoundDatagramPacket);

			byte[] opCode = { bufferByteArray[0], bufferByteArray[1] };


			if (opCode[1] == OP_ERROR) {
				reportError();
			} else if (opCode[1] == OP_DATAPACKET) {
                        System.out.println("Downloading: "+ " [ " + filename+ " ] " + " Data Packet Receiving >>"  + block);
				byte[] blockNumber = { bufferByteArray[2], bufferByteArray[3] };

				DataOutputStream dos = new DataOutputStream(byteOutOS);
				dos.write(inBoundDatagramPacket.getData(), 4,
						inBoundDatagramPacket.getLength() - 4);



                                sendAcknowledgment(blockNumber);
			}

		} while (!isLastPacket(inBoundDatagramPacket));
		return byteOutOS;
	}


      private void sendAcknowledgment(byte[] blockNumber) {

		byte[] ACK = { 0, OP_ACK, blockNumber[0], blockNumber[1] };

		DatagramPacket ack = new DatagramPacket(ACK, ACK.length, inetAddress,
				inBoundDatagramPacket.getPort());
		try {
			datagramSocket.send(ack);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




    private boolean isLastPacket(DatagramPacket datagramPacket) {
	if (datagramPacket.getLength() < 512){
            return true;
        }
	else
	      return false;
	}





    private void reportError() {
		result = false;
		String errorCode = new String(bufferByteArray, 3, 1);
		String errorText = new String(bufferByteArray, 4,
				inBoundDatagramPacket.getLength() - 4);
		System.err.println("Error: " + errorCode + " " + errorText);
	}

	private void reportErrorWRQ(){
        result = false;
		String errorCode = new String(requestByteArray, 3, 1);
		String errorText = new String(requestByteArray, 4,
				outBoundDatagramPacket.getLength() - 4);
		System.err.println("Error: File not found "+ errorText );


	}

    private void writeFile(ByteArrayOutputStream baoStream, String fileName) {

		try {
			OutputStream outputStream = new FileOutputStream(fileName);
			baoStream.writeTo(outputStream);
		} catch (IOException e) {
		}
	}







}
