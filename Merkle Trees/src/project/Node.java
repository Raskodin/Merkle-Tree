package project;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import util.HashGeneration;

public class Node {
	private String hash="";
	private String path="";
	Node left;
	Node right;
	boolean penultimate;
	boolean empty=true;
	
	Node(String path){
		penultimate=false;
		this.path=path; 
	}
	Node(Node leaf, String hash){
		
		this.hash=hash;
		left=leaf;
		penultimate=true;
		
	}
	Node(){
		penultimate=false;
	}
	public Node getLeft() {
		return this.left;
	}
	public Node getRight() {
		return this.right;
	}
	public void updateNode() {
		try {
			this.hash=HashGeneration.generateSHA256(new File(this.left.getPath()));
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public String getData() {
		
		if(left==null&&right==null) {
			return this.path;
		}
		return this.hash;
	}
	
	public String getPath() {
		return this.path;
	}
	public String getHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {//each time one hash requested it will update that node's subtrees' all hashes
		
		
		if(!penultimate) {
			this.hash=HashGeneration.generateSHA256(left.getHash()+right.getHash());
			if(left.getHash().isEmpty()&&right.getHash().isEmpty()) {
				this.hash="";
			}
		}
		this.empty=this.hash.isEmpty();
		return this.hash;
	}
}
