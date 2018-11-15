package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.Stack;

import project.MerkleTree;
import project.Node;

public class Main {

	public static void main(String[] args)  {


		//		MerkleTree m0 = new MerkleTree("data/9.txt");
		//		String hash = m0.getRoot().getData();
		//		System.out.println(hash);
		//
		//
		//		boolean valid = m0.checkAuthenticity("data/9meta.txt");
		//		System.out.println(valid);
		//
		//		// The following just is an example for you to see the usage.
		//		// Although there is none in reality, assume that there are two corrupt chunks in this example.
		//		ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("data/9meta.txt");
		//		System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).toString());
		//		System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());
		//
		download("secondaryPart/data/download_from_trusted.txt");



	}
	/**
	 * Taken help for download from https://www.journaldev.com/924/java-download-file-url
	 */
	public static void download(String path) {

		try {

			Scanner scan=new Scanner(new File(path));


			for(int i=4;i<9;i++) {
				downloadMetaFile(scan.next(), i);
				File urllist=downloadUrlLists(scan.next(), i+"", i);
				File urllistalt=downloadUrlLists(scan.next(), i+"alt", i);


				Scanner scan1=new Scanner(urllist);
				MerkleTree mt=new MerkleTree();

				new File("secondaryPart/data/split/"+i).mkdirs();
				while(scan1.hasNext()) {
					String url=scan1.next();
					String targetpath="secondaryPart/data/split/"+i+"/"+url.substring(url.lastIndexOf("/")+1);
					downloadFile(url, targetpath);
					mt.add(targetpath);
				}
				while(!mt.checkAuthenticity("secondaryPart/data/"+i+"/"+i+"meta.txt")) {
					Stack<String> corrupt = mt.findCorruptChunks("secondaryPart/data/"+i+"/"+i+"meta.txt").get(0);
					Stack<String> corruptstack=new Stack<String>();
					while(!corrupt.empty()) {
						corruptstack.push(corrupt.pop());
					}
					Node curr=mt.getRoot();
					corruptstack.pop();
					while(!corruptstack.empty()) {
						if(curr.getLeft().getData()==corruptstack.peek()) {
							curr=curr.getLeft();
							corruptstack.pop();
						}else if(curr.getRight().getData()==corruptstack.peek()) {
							curr=curr.getRight();
							corruptstack.pop();
						}
					}
					String corruptedfile=curr.getLeft().getData();
					new File(corruptedfile).delete();

					String altUrl="";
					Scanner alt=new Scanner(urllistalt);
					while(alt.hasNext()) {
						altUrl=alt.next();
						if(altUrl.substring(altUrl.lastIndexOf("/")).equals(corruptedfile.substring(corruptedfile.lastIndexOf("/")))){
							break;
						}
					}
					alt.close();
					downloadFile(altUrl ,corruptedfile);
					curr.updateNode();
				}
				scan1.close();
			}
			scan.close();



		} catch (IOException e) {

			e.printStackTrace();
		}



	}
	private static File downloadUrlLists(String Url,String filename ,int filenum) throws IOException {
		URL url= new URL(Url);
		File file=new File("secondaryPart/data/"+filenum+"/"+filename+".txt");
		file.createNewFile();
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);

		byte[] buffer=new byte[1024];
		int count=0;
		while((count=bis.read(buffer,0,1024))!=-1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
		return file;

	}


	private static File downloadMetaFile(String Url, int filenum) throws IOException {
		URL url= new URL(Url);
		new File("secondaryPart/data/"+filenum).mkdir();
		File file=new File("secondaryPart/data/"+filenum+"/"+filenum+"meta.txt");
		file.createNewFile();
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);

		byte[] buffer=new byte[1024];
		int count=0;
		while((count=bis.read(buffer,0,1024))!=-1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
		return file;
	}

	private static void downloadFile(String Url, String targetpath) throws IOException {
		URL url= new URL(Url);
		File file=new File(targetpath);
		file.createNewFile();
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream(file);

		byte[] buffer=new byte[1024];
		int count=0;
		while((count=bis.read(buffer,0,1024))!=-1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}

	private static void merge(String path)  {
		try {
			File dir = new File(path);
			File output = new File(path+".jpg");

			FileOutputStream fos = new FileOutputStream(output);
			for(File f : dir.listFiles()) {
				FileInputStream fis = new FileInputStream(f);
				byte[] mybyte = fis.readAllBytes();
				fos.write(mybyte);
				fos.flush();
				fis.close();
			}
			fos.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
