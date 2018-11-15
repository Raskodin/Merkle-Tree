package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import util.HashGeneration;

public class MerkleTree {
	private Node overallRoot=null;
	private int level=0;

	public MerkleTree() {
		
	}
	public MerkleTree(String path)  {
		
		Scanner file;
		try {
			file = new Scanner(new File(path));
			while(file.hasNext()) {

			add(file.next());
			}
			file.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} 
		
		
	}
	private Node build(int level) {
		if(level==0) {
			return new Node(null, "");
		}
		Node newborn=new Node();
		newborn.left=build(level-1);
		newborn.right=build(level-1);
		return newborn;
		
	}
	private Node build(Node prev, int level) {//builds a one more level higher tree
		Node newRoot=new Node();
		newRoot.left=prev;
		newRoot.right=build(level);
		return newRoot;
	}

	public void add(String next)  {
		
		try {
			File file=new File(next);
			Node newleaf=new Node(next);
			Node curr=new Node(newleaf, HashGeneration.generateSHA256(file));
			curr.empty=false;
			
			if(overallRoot==null) {
				overallRoot=curr;
				
			}else if(overallRoot.penultimate) {
				Node nroot=new Node();
				nroot.left=overallRoot;
				nroot.right=curr;
				overallRoot=nroot;	
				level++;
			} else {
				if(!addG(overallRoot, curr)) {
					overallRoot=build(overallRoot, level );
					level++;
					addG(overallRoot.right, curr);
				}
			}
			overallRoot.getHash();//updates hashes
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}	
	private boolean addG(Node root, Node toAdd)  {
		if(root.left.penultimate) {
			if(root.left.empty) {
				root.left=toAdd;
				return true;
			}else if(root.right.empty) {
				root.right=toAdd;
				return true;
			}else {
				return false;
			}
		}
		if(addG(root.left, toAdd)) {
			return true;
		}
		if(addG(root.right, toAdd)) {
			return true;
		}
		return false;
		
	}
	public boolean checkAuthenticity(String string) {
		try {
			Scanner check=new Scanner(new File(string));
			if(check.hasNext()) {
				if(check.next().equals(overallRoot.getHash())) {
					check.close();
					return true;
				}
			}
			check.close();
		} catch (FileNotFoundException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

		return false;
	}
	public ArrayList<Stack<String>> findCorruptChunks(String string){
		ArrayList<Stack<String>> finallist=new ArrayList<Stack<String>>();
		try {
			for(Stack<String> s:findCorruptChunks(checkMap(new File(string), overallRoot), overallRoot)) {
				Stack<String> temp=new Stack<String>();
				while(!s.empty()) {
					temp.push(s.pop());
				}
				finallist.add(temp);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finallist;
	
	}
	private ArrayList<Stack<String>> findCorruptChunks(HashMap<Node, String> map, Node root) {
		ArrayList<Stack<String>> list=new ArrayList<Stack<String>>();
		
		if(root.penultimate) {
			list.add(new Stack<String>());
			list.get(0).push(root.getData());
			return list;
		}
		if(isCorrupted(root.left, map)) {
			list.addAll(findCorruptChunks(map, root.left));
		}
		if(isCorrupted(root.right, map)) {
			list.addAll(findCorruptChunks(map, root.right));
		}
		
		for(Stack<String> stack:list) {
			stack.push(root.getData());
		}
		
		
		return list;
		
		
	}
	
 	private HashMap<Node, String> checkMap(File meta, Node originalroot) throws FileNotFoundException  {
		HashMap<Node, String> checkmap=new HashMap<Node, String>();
		Scanner reader=new Scanner(meta);
		ArrayDeque<Node> order=new ArrayDeque<Node>();
		order.add(originalroot);
		while(reader.hasNext()&&!order.isEmpty()) {
			Node temp=order.poll();
			checkmap.put(temp, reader.next());
			if(!temp.penultimate) {
				if(!temp.left.empty) {
					order.add(temp.left);
				}
				if(!temp.right.empty) {
					order.add(temp.right);
				}
			}	
		}
		reader.close();
		return checkmap;
	}

 	private boolean isCorrupted(Node node, HashMap<Node, String> map) {
 		
 		return !node.getData().equals(map.get(node));
 	}
	
	
	public Node getRoot() {
	
		return overallRoot;
	}
}
