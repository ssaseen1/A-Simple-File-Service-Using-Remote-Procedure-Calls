import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class Client {
	public static void main(String args[]){
		int port = 0;
		String hostname = null;
		String operation = null;
		String filename = null;
		String user = null;
		if (args.length == 0 ){
			System.err.println("Enter the required arguments");
			System.exit(0);
		}
		if(args.length == 6 || args.length == 8){
			hostname = args[0];
			port = Integer.parseInt(args[1]);
			if (hostname == null || port == 0){
				System.err.println("Hostname and port number is mandartory");
				System.exit(0);
			}
			for (int i = 2; i < args.length; i = i + 2){
				if(!(args[i].equals("--operation")||args[i].equals("--filename")||args[i].equals("--user"))){
					System.err.println("Enter the valid arguments");
					System.exit(0);
				}
			}
		}		
		if (args.length == 8){		
			for (int i = 2; i < args.length; i = i + 2){
				if(args[i].equals("--operation")){
					operation = args[i+1].toLowerCase();
					if(operation == null){
						System.err.println("Enter the required operation: read, write or list");
						System.exit(0);
					}
					else if(operation.equals("list")){
						System.err.println(" List operation does not require filename");
						System.exit(0);
						}
					}
				else if(args[i].equals("--filename")){
					filename = args[i+1];
					if(filename == null){
						System.err.println("Enter the required filename");
					}
				}
				else if(args[i].equals("--user")){
					user = args[i+1];
					if(user == null){
						System.err.println("Enter the required username");
					}
				}
			}
		}
		else if(args.length == 6){
			for (int i = 2; i < args.length; i = i + 2){
				if(args[i].equals("--operation")){
					operation = args[i+1].toLowerCase();
					if(operation == null){
						System.err.println("Enter the required operation: read, write or list");
					}
					else if(!(operation.equals("list"))){
						System.err.println("The operation "+operation+" must contain more arguments");
						System.exit(0);
						}
					}
				else if(args[i].equals("--filename")){
						System.err.println("filename is not required with the operation 'list'");
					    System.exit(0);
				}
				else if(args[i].equals("--user")){
					user = args[i+1];
					if(user == null){
						System.err.println("Enter the required username");
					}
				}
			}
		}
		else {
			System.err.println("Enter the correct arguments");
			System.exit(0);
		}	
		try {	
			//Open a transport to the server.
			TTransport transport = null;
			transport = new TSocket(hostname, port);
			transport.open();
			
			//Declare the protocol to use.
			TProtocol protocol = new  TBinaryProtocol(transport);
			
			//Create a client using the protocol.
		    FileStore.Client client = new FileStore.Client(protocol);

		    perform(client, operation, filename, user);

		    transport.close();
		} catch (TException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private static void perform(FileStore.Client client, String operation, String filename, String user) throws TException{
		// TODO Auto-generated method stub
		
		TIOStreamTransport tSTransport = new TIOStreamTransport(System.out);
		TProtocol tProtocol = new TJSONProtocol.Factory().getProtocol(tSTransport);
		
		FileReader fr = null;
		BufferedReader br = null;
		StringBuilder f = null;
		String line = null;
		String fileInfo = null;
		RFileMetadata metadata = new RFileMetadata();
		RFile filecontent = new RFile();
		StatusReport writefile = null;
		RFile readfile = new RFile();
		List<RFileMetadata> listfiles = null;
		
		try {
	        //RPC to readFile function of handler.
			if(operation.equals("write")){
				fr = new FileReader(filename);
		        br = new BufferedReader(fr);
    	        f = new StringBuilder();
    	        while((line = br.readLine())!= null){
    	        	f.append(line);
    	        	f.append("\n");
    	        }
    	      
    	        fileInfo = f.toString();
    	        metadata.setFilename(filename);
				metadata.setOwner(user);
				filecontent.setContent(fileInfo);
				filecontent.setMeta(metadata);
				filecontent.setMetaIsSet(true);
				writefile = client.writeFile(filecontent);
				writefile.write(tProtocol);
				System.out.println();
    	        br.close();		
			}
			// RPC to readFile function of handler.
			if(operation.equals("read")){
				try {
					readfile = client.readFile(filename,user);
					readfile.write(tProtocol);
					System.out.println();
				} catch (SystemException e) {
					// TODO: handle exception
					e.write(tProtocol);
					System.out.println();
				}		
			}
			// RPC to listOwnedFiles function of handler.
			if(operation.equals("list")){	
				try {
					listfiles = client.listOwnedFiles(user);
					tProtocol.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, listfiles.size()));
					for (RFileMetadata each : listfiles)
					{
						each.write(tProtocol);
					}
					tProtocol.writeListEnd();
		            System.out.println();
				} catch (SystemException e) {
					// TODO: handle exception
					e.write(tProtocol);
					System.out.println();
				}
			}
		} catch (TTransportException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			System.exit(0);
		}
		catch (FileNotFoundException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			System.exit(0);
		}
		catch (IOException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			System.exit(0);
		}
		catch (TException e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			System.exit(0);
		}
	} 
}