package com.gdufs.hotspot.entity;

import java.util.ArrayList;
import java.io.*;
/*
 * ����ֻ��Ҫ��������ʼʱһ�ι���
 * �Ϳɵ��������������Ϊ��̬����
 */
public class stopWord {
	private static ArrayList<String > stopwordlist;
	
	/*
	 * ��ʼ������ͣ�ôʱ�
	 * @param stopwordPath  ͣ�ôʱ����·��
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
	 * ����ͣ�ôʱ�
	 * @return stopwordlist  ͣ�ôʱ�
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
