package com.lemon.huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Huffman压缩实现类
 * @author iLemon
 * @version 1.0 date:2016/6
 * 参考资料:
 * http://www.math.pku.edu.cn/teachers/sunm/dsPython/slides/lecture7.pdf
 * 
 *
 */
public class HuffmanCompression {
	private File sourceFile;//源文件
	private File destinationFile;//目标文件
	private List<Byte> fileBytes=new ArrayList<Byte>();//以字节存储源文件的内容
	private List<HuffmanTree> allTreeNodes=new ArrayList<HuffmanTree>();
	private Map<String,String> encodeMap=new HashMap<String,String>();//以字节的ASCII码为Key,Huffman编码为Value的Map
	private Map<String,String> decodeMap=new HashMap<String,String>();//以字节的ASCII码为Value,Huffman编码为Key的Map
	private int huffmanBodyLength;//压缩文件中内容的长度
	
	public HuffmanCompression(File sourceFile, File destinationFile) {
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
	}
	
	/**
	 * 压缩文件
	 */
	public void compress(){
		try {
			
			long beginTime=System.currentTimeMillis();
			//1.读取目标文件并统计每个字节出现的频率
			HashMap<Byte,Integer> asciiMap=countASSCIBySourceFile(this.sourceFile);
			
			//2.初始化所有Huffman树节点
			initializeHuffmanNode(asciiMap);
			
			//3.构造huffman树
			HuffmanTree tree=constructHuffmanTree(allTreeNodes);
			
			//4.递归初始化编码表
			encodingByHuffman(tree, new StringBuffer());
			//initDecodeMap(encodeMap);
			System.out.println(encodeMap);
			
			//5.根据文件内容的字节集合和编码表构造Huffman编码集合
			List<String> huffmanCodes=generateHuffmanCode(fileBytes);
			
			//6.将Huffman编码集合以bit为单位写入压缩文件
			writeContent(destinationFile, huffmanCodes);
			
			//7.写入Huffman码表到压缩文件尾部
			writeHead(destinationFile, decodeMap);
			System.out.println("写入压缩内容成功！");
			System.out.println("=============================");
			System.out.println("压缩后文件大小："+destinationFile.length()+"字节");
			BigDecimal compressedLength=new BigDecimal(destinationFile.length());
			BigDecimal sourceLength=new BigDecimal(sourceFile.length());
			double compressedPersent=1-compressedLength.divide(sourceLength, 2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			System.out.println("压缩率："+compressedPersent*100+"%");
			System.out.println("压缩耗时："+(System.currentTimeMillis()-beginTime)+"ms");
			System.out.println("=============================");
		} catch (HuffmanException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private void initDecodeMap(Map<String,String> encodeMap){
		Iterator<Entry<String, String>> iter = encodeMap.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			decodeMap.put(val.toString(), key.toString());
		}
	}
	
	/**
	 * 将Huffman编码Map写入头文件
	 * @param fileName
	 * @param decodeMap
	 */
	private void writeHead(File destinationFile,Map<String,String> decodeMap){
		OutputStream output=null;
		try {
			
			/*the second  param of FileOutputStream construction is true, 
			 *then bytes will be written to the end of the file rather than the beginning
			 */
			output=new FileOutputStream(destinationFile,true);
			output.write(System.getProperty(Constants.LINE_SEPARATOR).getBytes());
			String huffmanHead="HUFF BEGIN len="+huffmanBodyLength+" ";
			output.write(huffmanHead.getBytes());
			output.write(decodeMap.toString().getBytes());
			output.write(Constants.HUFFMAN_END_FLAG.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(output!=null){
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 写入压缩内容到文件
	 * @param destinationFile:Huffman压缩文件
	 * @param contentCodes:源文件内容的Huffman编码
	 */
	private void writeContent(File destinationFile, List<String> contentCodes) {
		StringBuffer sb=new StringBuffer();
		for(String str:contentCodes){
			sb.append(str);
		}
		huffmanBodyLength=sb.length();
		System.out.println(sb.length());//292679
		HuffmanUtils.write(destinationFile.getAbsolutePath(), sb.toString());
		
	}
	
	/**
	 * 生成哈夫曼编码
	 * @param dictionary
	 * @return
	 */
	private List<String> generateHuffmanCode(List<Byte> dictionary){
		List<String> result=new ArrayList<String>();
		for(Byte str:dictionary){
			String content=encodeMap.get(String.valueOf(str));
			if(content!=null){
				result.add(content);
			}
		}

		return result;
	}
	
	/**
	 * 递归对Huffman树中的叶子节点编码
	 * 左子树为0,右子树为1
	 * @param root
	 * @param prefix
	 */
	private void encodingByHuffman(HuffmanTree root,StringBuffer prefix){
		if(root==null){
			return;
		}else{

			//叶子节点
			if(root.left==null && root.right==null){
				if(prefix.length()==0){
					prefix.append('0');
				}
				//System.out.println(root.value + "\t" + root.weight + "\t" + prefix);
				encodeMap.put(root.value.toString(),prefix.toString());
				decodeMap.put(prefix.toString(),root.value.toString());
			}

			//遍历左子树
			prefix.append('0');
			encodingByHuffman(root.left, prefix);
			prefix.deleteCharAt(prefix.length()-1);

			//遍历右子树
			prefix.append('1');
			encodingByHuffman(root.right, prefix);
			prefix.deleteCharAt(prefix.length()-1);
		}
	}
	
	/**
	 * 递归构造Huffman树
	 * @param list
	 * @return
	 */
	private HuffmanTree constructHuffmanTree(List<HuffmanTree> list){
		if(list==null){
			return null;
		}
		if(list.size()==1){
			return list.get(0);
		}
		if(list.size()>=2){
			HuffmanTree leftSymbol=list.get(0);
			HuffmanTree rightSymbol=list.get(1);
			Integer rootValue=leftSymbol.weight+rightSymbol.weight;
			HuffmanTree root=new HuffmanTree(leftSymbol, rightSymbol, rootValue, rootValue);
			list.remove(leftSymbol);
			list.remove(rightSymbol);
			list.add(root);
			return constructHuffmanTree(sortHuffmanTree(list));

		}
		return null;
	}
	
	private List<HuffmanTree> sortHuffmanTree(List<HuffmanTree> treeList){
		Collections.sort(treeList, new Comparator<HuffmanTree>() {

			@Override
			public int compare(HuffmanTree o1, HuffmanTree o2) {
				if(o1.weight-o2.weight==0){
					return o1.value.toString().compareTo(o2.value.toString());
				}
				return o1.weight-o2.weight;
			}

		});
		return treeList;
	}
	
	/**
	 * 根据字节出现的频率初始化Huffman树节点
	 * @param map
	 * @return
	 */
	private void initializeHuffmanNode(Map<Byte,Integer> map){
		map=HuffmanUtils.sortByValue(map);
		Iterator<Entry<Byte, Integer>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			allTreeNodes.add(new HuffmanTree(null,null,key,new Integer(val.toString())));
		}
	}

	/** 
	 * 统计字节对应的ASCII码在源文件中出现的次数:
	 * 例如a的ascii码是97,b是98
	 * @param sourceFile:源文件
	 * @return HashMap {97:1,98:2}
	 * @throws HuffmanException 
	 */  
	private HashMap<Byte, Integer> countASSCIBySourceFile(File sourceFile) throws HuffmanException {  
		
		// 判断文件是否存在  
		if (!sourceFile.exists()) {  
			throw new HuffmanException("被压缩文件不能为空");  
		}
		
		//验证文件类型,目前只是简单的判断其后缀是否为文本文件
		if(!sourceFile.getName().endsWith(Constants.FILE_POSTFIX)){
			throw new HuffmanException("被压缩文件的格式目前只支持文本文件");
		}
		HashMap<Byte, Integer> result = new HashMap<>();  
		FileInputStream fis = null;  
		try {  
			// 创建文件输入流  
			fis = new FileInputStream(sourceFile);
			System.out.println("=============================");
			System.out.println("原始文件大小："+sourceFile.length()+"字节");
			System.out.println("=============================");
			//保存每次读取的字节  
			byte[] buf = new byte[1024];  
			int size = 0;  
			//每次读取1024个字节  
			while ((size = fis.read(buf)) != -1) {  
				//循环每次读到的字节数组  
				for (int i = 0; i < size; i++) {  
					byte b = buf[i];
					if(b!=13){//忽略回车
						
						fileBytes.add(b);
						
						// 如果map中包含了这个字节，则取出对应的值，自增一次  
						if (result.containsKey(b)) {  
							int old = result.get(b);  
							result.put(b, ++old);  
						} else {  
							// map中不包含这个字节，则直接放入，且出现次数为1  
							result.put(b, 1);  
						}  
					}
				}  
			}  
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			if (fis != null) {  
				try {  
					fis.close();  
				} catch (IOException e) {  
					fis = null;  
				}  
			}  
		}  
		return result;
	} 
}
