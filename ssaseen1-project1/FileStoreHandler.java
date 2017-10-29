import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.thrift.TException;

public class FileStoreHandler implements FileStore.Iface {
	
	ArrayList<RFile> list = new ArrayList<RFile>();
	Hashtable<String, ArrayList<RFile>> user_table = new Hashtable<>();
	
	@Override
	public StatusReport writeFile(RFile rFile) throws SystemException, TException {	
		// TODO Auto-generated method stub
		String userFolder = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		userFolder = rFile.getMeta().getOwner();
		File f2 = new File("Server_folder/"+userFolder);
		if(!(f2.exists())){
			f2.mkdirs();
		}
		File f3 = new File(f2,rFile.getMeta().getFilename());
		try {
			ArrayList<RFile> list0 = new ArrayList<RFile>();
			StringBuffer b = new StringBuffer();
			
			MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] hb = md.digest(rFile.getContent().getBytes("UTF-8"));
	        
	        for (int i = 0; i < hb.length; i++) {
	            b.append(Integer.toString((hb[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        String digest = b.toString();
	        
			if(!(f3.exists())){
				f3.createNewFile();
				fw = new FileWriter(f3.getAbsoluteFile());
				bw = new BufferedWriter(fw);		
				bw.write(rFile.getContent());
				bw.close();
				String content = rFile.getContent();
				rFile.setContent(content);	
				rFile.getMeta().getFilename();
				rFile.getMeta().setCreated(f3.lastModified());
				rFile.getMeta().setCreatedIsSet(true);
				rFile.getMeta().setUpdated(f3.lastModified());
				rFile.getMeta().setUpdatedIsSet(true);
				rFile.getMeta().setVersion(0);
				rFile.getMeta().setVersionIsSet(true);
				rFile.getMeta().setContentLength(rFile.getContent().length());
				rFile.getMeta().setContentLengthIsSet(true);
				rFile.getMeta().setContentHash(digest);
				rFile.getMeta().setContentHashIsSet(true);
				
				if(user_table.get(rFile.getMeta().getOwner()) == null){
					list0.add(rFile);
					user_table.put(rFile.getMeta().getOwner(), list0);
					
				}
				else{
					list0 = user_table.get(rFile.getMeta().getOwner());
					list0.add(rFile);
					user_table.put(rFile.getMeta().getOwner(), list0);
				}
			}
			else {	

				fw = new FileWriter(f3.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write(rFile.getContent());
				bw.close();	
				list0 = user_table.get(rFile.getMeta().getOwner());
				for(int i = 0; i < list0.size(); i++){
					if(rFile.getMeta().getFilename().equals(list0.get(i).getMeta().getFilename())){
						String content = rFile.getContent();
						rFile.setContent(content);	
						rFile.getMeta().getFilename();
						list0.get(i).getMeta().setUpdated(f3.lastModified());
						list0.get(i).getMeta().setUpdatedIsSet(true);	
						list0.get(i).getMeta().setVersion((list0.get(i).getMeta().getVersion()) + 1);
						list0.get(i).getMeta().setVersionIsSet(true);
						list0.get(i).getMeta().setContentLength(rFile.getContent().length());
						list0.get(i).getMeta().setContentLengthIsSet(true);
						list0.get(i).getMeta().setContentHash(digest);
						list0.get(i).getMeta().setContentHashIsSet(true);
						list0.get(i).setContent(rFile.getContent());
					}
				}
				list0 = user_table.get(rFile.getMeta().getOwner());
				list0.add(rFile);
				user_table.put(rFile.getMeta().getOwner(), list0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			StatusReport status = new StatusReport(Status.FAILED);
			return status;
		}
		StatusReport status = new StatusReport(Status.SUCCESSFUL);
		return status;	
	}

	@Override
	public RFile readFile(String filename, String owner) throws SystemException, TException {
		// TODO Auto-generated method stub
		ArrayList<RFile> readlist = new ArrayList<RFile>();
		SystemException se = null;
		readlist = user_table.get(owner);
		//System.out.println("username ----" +user_table);
		if(readlist == null){
			se = new SystemException();
			se.setMessage("Owner "+owner+" does not exist");
			throw se;
		}
		else{
			for(int i = 0; i < readlist.size(); i++){
				if(readlist.get(i).getMeta().getFilename().equals(filename)){
					return readlist.get(i);
				}
			}
				se = new SystemException();
				se.setMessage("File "+filename+" does not exist");
				throw se;
		}
	}
	
	@Override
	public List<RFileMetadata> listOwnedFiles(String user) throws SystemException, TException {
		// TODO Auto-generated method stub
		SystemException s = null;
		ArrayList<RFile> user_name = user_table.get(user);
		List<RFileMetadata> user_files = new ArrayList<RFileMetadata>();
		if( user_name == null){
			s = new SystemException();
			s.setMessage("User "+user+" does not exist");
			throw s;
		}
		else
		{
			for(int i = 0; i< user_name.size(); i++){
				user_files.add(user_name.get(i).getMeta());
			}
		}
		return user_files;
	}
}