package com.lemon.huffman;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		File sourceFile=new File("E:\\java\\test\\test\\test3\\abc.txt");
		File destinationFile=new File("E:\\java\\test\\test\\test3\\abc.huffman");
		File extractedFile=new File("E:\\java\\test\\test\\test3\\abc_解压后.txt");
		HuffmanCompression compression=new HuffmanCompression(sourceFile, destinationFile);
		compression.compress();
		
		HuffmanUnCompression unCompression=new HuffmanUnCompression(destinationFile, extractedFile);
		unCompression.unCompress();
	}

}	
