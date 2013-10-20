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
			 * 处理filename去停用词   未做
			 */
			
			article.filename=news.getTitle();
			article.datetime = news.getDate();
			
			for(String word : words)
			{
				word = strip(word, "[]()\'\".,?:-!~“‘’”;").toLowerCase();//从两边匹配
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
	 * 用于对每一个词用两边夹去除标点符号
	 * @param src 待处理的单词
	 * @param seq 待去除的标点符号
	 * @return    处理后的单词
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
	 * @param content 待处理的句子
	 * @return        去掉所有标点符号的句子
	 */
	public static String deleteAllPunctuation(String content)
	{
		String newcontent= content.replaceAll("[,;:?’()\\-!~“‘’”]", " ");
		return newcontent;
	}

}
