import java.util.*;
import java.io.File;

class FileList
{
	public List<String> filesRec = new ArrayList<String>();
	public List<String> filesCur = new ArrayList<String>();
	private String path = null;

	public FileList(String path)
	{
		this.path = path;
		File folder = new File(path);
      	File fileList[] = folder.listFiles();
      	getFilesRecursively(fileList, path);
      	// getFilesCurrentDirectory(fileList);
   		//    for(String str : filesRec)
   		//    		System.out.println(str);

   		// System.out.println("====================");
   		//    for(String str : filesCur)
		// 			System.out.println(str);
	}

	public void getFilesRecursively(File fileList[], String curpath)
	{
		for(int i=0; i<fileList.length; i++)
		{
			if(fileList[i].isFile())
			{
				String str = fileList[i].getName();
				//filesRec.add(curpath+"/"+str);
				filesRec.add(str);
			}
			else if(fileList[i].isDirectory())
			{
				String newPath = curpath+"/"+fileList[i].getName();
				File folder = new File(newPath);
				getFilesRecursively(folder.listFiles(),newPath);
			}
		}
	}

	public void getFilesCurrentDirectory(File fileList[])
	{
		for(int i=0; i<fileList.length; i++)
		{
			if(fileList[i].isFile())
			{
				String str[] = fileList[i].getName().split("\\.");
				filesCur.add(str[0]);
			}
		}
	}

	// public static void main(String[] args) {
	// 	FileList fl = new FileList(args[0]);
	// }
}