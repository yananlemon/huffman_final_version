package com.lemon.huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Huffman解压实现类
 * @author andy
 */
public class HuffmanUnCompression {
	private File sourceFile;//源文件
	private File destinationFile;//目标文件
	private int huffmanBodyLength;//压缩文件中内容的长度
	private Map<String,String> huffmanTable = new HashMap<>();//huffman解码表
	private StringBuffer contentOfHuffmanBuffer=new StringBuffer();//huffman内容
	private StringBuffer losslessContent=new StringBuffer();//无损的文件内容
	public HuffmanUnCompression(File sourceFile, File destinationFile) {
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
	}
	
	public void unCompress(){
		long beginTime=System.currentTimeMillis();
		//1.生成码表
		buildHuffmanTable(sourceFile);
		
		//2.读取huffman文件中的主体内容
		readCompressionFileContent(sourceFile);
		
		//3.写入Extract文件
		matchAndWriteExtractedFile(destinationFile, contentOfHuffmanBuffer.toString());
		
		long endTime=System.currentTimeMillis();
		System.out.println("解压到"+destinationFile.getAbsolutePath()+"\n解压后文件大小："+destinationFile.length()+"(字节)\n 耗时："+(endTime-beginTime)+"ms");
	}
	
	/**
	 * 根据读入的文件内容与Huffman码表匹配文件真实内容并写入到解压后的文件
	 */
	private void matchAndWriteExtractedFile(File destinationFile, String contentOfHuffmanBuffer){
		FileOutputStream output=null;
		try {
			output = new FileOutputStream(destinationFile);
			char[] c=contentOfHuffmanBuffer.toString().toCharArray();
			StringBuffer s=new StringBuffer();
			for(int i=0;i<c.length;i++){
				String key=String.valueOf(c[i]);
				Object value=null;
				if(s.length()>0){
					value=huffmanTable.get(s.toString());
				}else{
					value=huffmanTable.get(key);
				}
				if(value!=null){
					s.delete(0, s.length());
					if(value.toString().equals(" ")){
						losslessContent.append(" ");
						output.write(32);
					}else if(value.toString().equals("10")){
						losslessContent.append(System.getProperty("line.separator"));
						output.write(System.getProperty("line.separator").getBytes());
					}else{
						losslessContent.append(value.toString());
						output.write(Integer.parseInt(value.toString()));
					}
				}else{
					s.append(key);
					value=huffmanTable.get(s.toString());
					if(value!=null){
						s.delete(0, s.length());
						if(value.toString().equals("")){
							losslessContent.append(" ");
							output.write(32);
						}else if(value.toString().equals("10")){
							losslessContent.append(System.getProperty("line.separator"));
							output.write(System.getProperty("line.separator").getBytes());
						}else{
							losslessContent.append(value.toString());
							output.write(Integer.parseInt(value.toString()));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			//删除huffman文件
			if(sourceFile.exists()){
				sourceFile.delete();
			}
			
			//关闭输出流
			if(output!=null){
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 读取文件最后一行,并将Huffman map 字符串转换为HashMap
	 * @param sourceFile
	 */
	private void buildHuffmanTable(File sourceFile){
		BufferedReader bReader=null;
		try {
			bReader = new BufferedReader(new FileReader(sourceFile));
			String buf = null;
	        String str = null;
	        while ((buf = bReader.readLine()) != null) {
	            str = buf;
	        }

	        bReader.close();

	        System.out.println("last line:"+str);
	        System.out.println(str.indexOf("="));
	        huffmanBodyLength=Integer.valueOf(str.substring(str.indexOf("=")+1,str.indexOf("{")-1));
	        String head="HUFF BEGIN len="+huffmanBodyLength;
	        String map=str.substring(head.length(),str.indexOf("HUFF END"));
	        System.out.println(map);
	        map=map.trim();
	        map = map.substring(1, map.length()-1);           
			String[] keyValuePairs = map.split(",");
			for(String pair : keyValuePairs){
				if(pair!=null && pair.length()>0){
					String[] entry = pair.split("="); 
					if(entry.length==1){
						huffmanTable.put(entry[0].trim(), " ");
					}else{
						huffmanTable.put(entry[0].trim(), entry[1].trim());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 以Bit为单位读取指定文件的内容到contentOfHuffmanBuffer
	 * @param fileName
	 * @return
	 */
	private void readCompressionFileContent(File sourceFile){
		BufferedBitReader reader=null;
		try {
			reader=new BufferedBitReader(sourceFile.getAbsolutePath());
			int i=-1;
			while((i=reader.readBit())!=-1 && huffmanBodyLength>0){
				contentOfHuffmanBuffer.append(i);
				huffmanBodyLength--;
			}
			reader.close();
			System.out.println(contentOfHuffmanBuffer.length());//301100
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
