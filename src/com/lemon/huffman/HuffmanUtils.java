package com.lemon.huffman;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class HuffmanUtils {

	/**
	 * 将需要写入的内容以Bit为单位，写到目标文件中。
	 * @param fileName
	 * @param source
	 */
	public static void write(String fileName,String source){
		BufferedBitWriter writer=null;
		try {
			writer=new BufferedBitWriter(fileName);
			for(int i=0;i<source.length();i++){
				char c=source.charAt(i);
				String s=String.valueOf(c);
				writer.writeBit(Integer.valueOf(s.toString()));
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 以Bit为单位读取指定文件的内容到StringBuffer
	 * @param fileName
	 * @return
	 */
	public static StringBuffer read(String fileName){
		StringBuffer result=new StringBuffer();
		BufferedBitReader reader=null;
		try {
			reader=new BufferedBitReader(fileName);
			int i;
			while((i=reader.readBit())!=-1){
				result.append(i);
			}
			reader.close();
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
		return result;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ){
		List<Map.Entry<K, V>> list =
				new LinkedList<>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>(){
			@Override
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ){
				return ( o1.getValue() ).compareTo( o2.getValue() );
			}
		} );

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list){
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

}
