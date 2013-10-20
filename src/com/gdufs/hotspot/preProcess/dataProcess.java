package com.gdufs.hotspot.preProcess;

import java.util.ArrayList;

import com.gdufs.hotspot.entity.Article;
import com.gdufs.hotspot.entity.News;


public class dataProcess {
	

	public  ArrayList<Article> splitAndDeleteStopWord(ArrayList<News> newslist,ArrayList<String> stopwords)
	{
		ArrayList<Article> articleList = new ArrayList<Article>();
		int index = 0;
		for(News news : newslist)
		{
			String content = news.getContent();
			content=deleteAllPunctuation(content);
			String[] words = content.split(" ");
			
			Article article = new Article();
			article.words = new ArrayList<String>();
			article.index = index;
			/*
			 * ����filenameȥͣ�ô�   δ��
			 */
			
			article.filename=news.getTitle();
			article.datetime = news.getDate();
			
			for(String word : words)
			{
				word = strip(word, "[]()\'\".,?:-!~��������;").toLowerCase();//������ƥ��
				if(!word.isEmpty() && !stopwords.contains(word))
				{
					article.words.add(word);					
				}
			}
			articleList.add(article);
		}
		
		return articleList;
	}
	
	
	
	/**
	 * ���ڶ�ÿһ���������߼�ȥ��������
	 * @param src ������ĵ���
	 * @param seq ��ȥ���ı�����
	 * @return    �����ĵ���
	 */
	public String strip(String src, String seq)
	{
		int start = 0;
		int end = src.length();
		for(int i=0; i<src.length(); ++i)
		{
			if(seq.indexOf(src.charAt(i))!=-1)
			{
				start++;
			}
			else
			{
				break;
			}
		}
		for(int i=src.length()-1; i>=0 && start < end; --i)
		{
			if(seq.indexOf(src.charAt(i))!=-1)
			{
				end--;
			}
			else
			{
				break;
			}
		}
		return src.substring(start, end);
	}
	
	/**
	 * @param content ������ľ���
	 * @return        ȥ�����б����ŵľ���
	 */
	public static String deleteAllPunctuation(String content)
	{
		String newcontent= content.replaceAll("[,;:?��()\\-!~��������]", " ");
		return newcontent;
	}

}
