package com.gdufs.hotspot.Main;


import java.util.ArrayList;

import com.gdufs.hotspot.DBcontrol.dbdao;
import com.gdufs.hotspot.cluster.OnePass;
import com.gdufs.hotspot.entity.Article;
import com.gdufs.hotspot.entity.News;

import com.gdufs.hotspot.entity.stopWord;
import com.gdufs.hotspot.preProcess.dataProcess;



public class HotSpot {

	public HotSpot()
	{
		stopWord stopwordclass = new stopWord("stop_words_en.txt");
		
		ArrayList<News> newslist = dbdao.selectCorpus("wsj", "2013-7-01", "2013-9-01");//1700Ãı…œœﬁ
		System.out.println(newslist.size());
		
		ArrayList<Article> articlelist = new dataProcess().splitAndDeleteStopWord(newslist, stopwordclass.getStopWord());
		
		new OnePass().doProcess(articlelist);
	}
	public static void main(String []args)
	{
		HotSpot hotspot= new HotSpot();
	}
	
}
