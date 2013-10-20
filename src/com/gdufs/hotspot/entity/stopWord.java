package com.gdufs.hotspot.entity;

import java.util.ArrayList;
import java.io.*;
/*
 * 此类只需要在主程序开始时一次构造
 * 就可到处调用因此设置为静态方法
 */
public class stopWord {
	private static ArrayList<String > stopwordlist;
	
	/*
	 * 初始化构造停用词表
	 * @param stopwordPath  停用词表相对路径
	 */
	public stopWord(String stopWordPath)
    {
		stopwordlist= new ArrayList<String>();
		
		String line=null;
		BufferedReader br = null;
		try {
			 br= new BufferedReader(new FileReader(new File(stopWordPath)));
			while((line = br.readLine())!=null)
				stopwordlist.add(line);
		} catch (IOException e) 
		{
			e.printStackTrace();
		}finally
		{
			try {
			        if(br != null)
					   br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
    }
	/*
	 * 访问停用词表
	 * @return stopwordlist  停用词表
	 */
	public static ArrayList<String> getStopWord()
	{
		return stopwordlist;
	}
	
	public static void main(String [] args)
	{
		stopWord sp = new stopWord("stop_words_en.txt");
		System.out.println(sp.getStopWord());
	}
	
}
