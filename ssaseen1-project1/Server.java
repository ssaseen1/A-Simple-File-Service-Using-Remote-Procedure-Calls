import java.io.File;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Server {
	
	static public FileStoreHandler handler;
	static public FileStore.Processor<FileStore.Iface> processor;
	static int port = 0;
	
	public static void delete_dir(File dir) {
	    File[] file = dir.listFiles();
	    if (file != null) {
	        for (File f : file) {
	            delete_dir(f);
	        }
	    }
	    dir.delete();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TServerTransport serverPort = null;
		TServer server = null ;
		
		File d = new File("Server_folder");
		if(d.exists()){
			delete_dir(d);
		}
		
		handler = new FileStoreHandler();
		processor = new FileStore.Processor<FileStore.Iface>(handler);
		
		if(args.length != 1)
		{
			System.err.println("Enter the required port number");
			System.exit(0);
		}
		else
		{
			port = Integer.parseInt(args[0]);
			try {
			    serverPort = new TServerSocket(port);
			    server = new TSimpleServer(new Args(serverPort).processor(processor));
			      
			    System.out.println(" Server is started at port number "+port);
			    server.serve();
			    
			} catch (TException e) {
				// TODO: handle exception
				System.err.println(e.getMessage());
				System.exit(0);
			}				
		}
	}
}