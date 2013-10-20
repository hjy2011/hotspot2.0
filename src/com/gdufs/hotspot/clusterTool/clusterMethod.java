package com.gdufs.hotspot.clusterTool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.gdufs.hotspot.entity.Article;
import com.gdufs.hotspot.entity.CenterPoint;
import com.gdufs.hotspot.entity.TFDF;
import com.gdufs.hotspot.entity.TextVector;
import com.gdufs.hotspot.entity.WordTFDF;
import com.gdufs.hotspot.entity.stopWord;
import com.gdufs.hotspot.preProcess.dataProcess;

public class clusterMethod {
	
	
	public  int getFeatureWordbyTitle(ArrayList<Article> articles,ArrayList<String> titlelist,
			TreeMap<String, TFDF> map_WordTFDF,ArrayList<WordTFDF> featureWordMap)
	{
		ArrayList<String >allTitle=new ArrayList<String >();
		//从数据库获取title数据
		for(int j=0;j<articles.size();j++)
		{
			//处理标点，去除停用词
			String newstr=dataProcess.deleteAllPunctuation(articles.get(j).filename);
			String [] tem=newstr.split(" ");
			String result="";
			for(String word : tem)
			{
				if(word!=null && !stopWord.getStopWord().contains(word))
				{
					result+=word+" ";					
				}
			}
			String [] aTitleName=articles.get(j).filename.split(" ");
			for(String featureWord:aTitleName)
				if(!titlelist.contains(aTitleName))
				titlelist.add(featureWord.toLowerCase());
		}
		//printlist();
		String word = null;
		TFDF tfdf = null;
		WordTFDF wordTFDF;
		Set<Map.Entry<String, TFDF>> set = map_WordTFDF.entrySet();
		
		// 2 - 用tf法提取特征词
		for(Map.Entry<String, TFDF> entry : set)
		{
			word =entry.getKey();
			if(titlelist.contains(word)&&word!=null)
			{
				tfdf = entry.getValue();
				wordTFDF = new WordTFDF(word, tfdf.getTF(), tfdf.getDF());
				featureWordMap.add(wordTFDF);
			}
		}
		//printF();
		System.out.println(featureWordMap.size());
		System.out.println(titlelist.size());
		return featureWordMap.size();
	}
	
	
	
	public void getWordTFDF(ArrayList<Article> articles,TreeMap<String, TFDF> map_WordTFDF)
	{
		int flag = -1;
		for(int i=0; i<articles.size(); ++i)
		{
			ArrayList<String> list = articles.get(i).words;
			flag++; //文章号判断标记
			for(String word : list)
			{
				if(map_WordTFDF.containsKey(word))
				{
					TFDF tmp = map_WordTFDF.get(word);
					tmp.setTF(tmp.getTF()+1);
					if(tmp.getFlag() != flag)
					{
						tmp.setDF(tmp.getDF()+1);
						tmp.setFlag(flag);
					}
				}
				else
				{
					TFDF tmp = new TFDF(1, 1, flag);
					map_WordTFDF.put(word, tmp);
				}
			}
		}
	}

	
	
	public void makeTextToVector(int size,ArrayList<WordTFDF> featureWordMap,ArrayList<Article> articles,
			int FeatureWordSize,ArrayList<TextVector> list_TextVector)
	{
		TreeMap<String, TFDF> map_oneNewWords = new TreeMap<String, TFDF>();
		for(int i=0; i<size; ++i)
		{
			// 1 - 把特征词放进map_oneNewWords里面，利用来构造向量
			map_oneNewWords.clear();
			//printF();
			for(int j=0; j<featureWordMap.size(); ++j)
			{
				TFDF tfdf = new TFDF(0, featureWordMap.get(j).getDF(), -1);
				String temWord=featureWordMap.get(j).getWord();
				//System.out.println(temWord);
				map_oneNewWords.put(temWord, tfdf);
			}
			
			// 2 - 统计特征词数
			Article article = articles.get(i);
			ArrayList<String> words = article.words;
			for(String word : words)
			{
				if(map_oneNewWords.containsKey(word))
				{
					TFDF tfdf = map_oneNewWords.get(word);
					tfdf.setTF(tfdf.getTF()+1);
					map_oneNewWords.put(word, tfdf);
				}
			}
			// 3 - 计算tfidf值
			Set<Map.Entry<String, TFDF>> set = map_oneNewWords.entrySet();
			Iterator<Map.Entry<String, TFDF>> iter = set.iterator();
			TextVector textVector = new TextVector();
			textVector.filename = article.filename;
			textVector.tfidf = new double[FeatureWordSize];
			int k=0;
			double normalized = 0.0;
			double tmp = 0;
			double tmp1 = 0;
			while(iter.hasNext())
			{
				TFDF tfdf = iter.next().getValue();
				textVector.tfidf[k] = tfdf.getTF() * Math.log((double)size/tfdf.getDF() + 0.01);
				normalized += textVector.tfidf[k] * textVector.tfidf[k];
				
				tmp1 += textVector.tfidf[k];
				k++;
			}
			normalized = Math.sqrt(normalized);
			if(normalized!=0)
			{
				for(int j=0; j<FeatureWordSize; ++j)
				{
					if(textVector.tfidf[j] > 0)
					{
						textVector.tfidf[j] /= normalized;
						tmp+=textVector.tfidf[j];
					}
					
				}
			}
			textVector.clusterNumber = 0;
			list_TextVector.add(textVector);
		}
	}
	
	
	
	/**
	 * 生成阈值
	 */
	public double getR(ArrayList<TextVector> list_TextVector,int size,int FeatureWordSize)
	{
		int rand1;
		int rand2;
		double fenzi;
		double fenmu1;
		double fenmu2;
		double r = 0.0;
		TextVector textVector1, textVector2;
		int i=(int)(size);
		while((i--)!=0)
		{
			rand1 = (int)(Math.random() * size);
			rand2 = (int)(Math.random() * size);
			fenzi = fenmu1 = fenmu2 = 0.0;
			textVector1 = list_TextVector.get(rand1);
			textVector2 = list_TextVector.get(rand2);
			for(int j=0; j<FeatureWordSize; ++j)
			{
				fenzi += textVector1.tfidf[j] * textVector2.tfidf[j];
				fenmu1 += textVector1.tfidf[j] * textVector1.tfidf[j];
				fenmu2 += textVector2.tfidf[j] * textVector2.tfidf[j];
 			}
			if(fenmu1 !=0 && fenmu2 != 0)
			{
				r += fenzi/ Math.sqrt(fenmu1 * fenmu2);
			}
		}
		//r = C.MULTI * r / (double)(size * C.PRECENT);
		r = r*4 / (double)(size);
		System.out.printf("阈值r=%f%n", r);
		return r;
	}
	
	
	
	
	

	/*
	 * 
	 */
	public void getHotSpot(ArrayList<CenterPoint> list_CenterPoint,TreeMap<String, TFDF> map_WordTFDF, 
			ArrayList<WordTFDF> list_WordTFDF,ArrayList<TextVector> list_TextVector,ArrayList<Article> articles)
	{
		//找出每个聚类中词频最高的前几个词
		int sumNoise = 0;
		System.out.println(list_CenterPoint.size());
		for(int i=0; i<list_CenterPoint.size(); ++i)
		{
			map_WordTFDF.clear();
			list_WordTFDF.clear();
			CenterPoint curPoint = list_CenterPoint.get(i);
			//int noise = (int)(C.HOTSPOTNUMBER * curPoint.newsNumber / size);
			int noise = 40;
			sumNoise+=noise;
			ArrayList<Integer> clusterTextList = new ArrayList<Integer>();
			int index = 0;
			for(int j=0; j<list_TextVector.size(); ++j)
			{
				if(list_TextVector.get(index).clusterNumber == curPoint.clusterNumber)
				{
					clusterTextList.add(index);
					Article article = articles.get(index);
					ArrayList<String> a_words = article.words;
					for(int k=0; k<a_words.size(); ++k)
					{
						String word = a_words.get(k);
						if(map_WordTFDF.containsKey(word))
						{
							TFDF tfdf = map_WordTFDF.get(word);
							tfdf.setTF(tfdf.getTF()+1);
							map_WordTFDF.put(word, tfdf);
						}
						else
						{
							map_WordTFDF.put(word, new TFDF(1, 0, 0));
						}
					}
				}
				index++;//判断完一个，判断下一个		
			}
			/*************************************/
			/*
			 * 根据簇的大小、词频的均值、方差来选择簇
			 * @author dsj
			 */
			//WordTFDF wordTFDF;
			double junzhitem=0;
			double fangchatem=0;
			int clutersize=clusterTextList.size();
			
			Set<Map.Entry<String, TFDF>> set = map_WordTFDF.entrySet();
			for(Map.Entry<String, TFDF> entry : set)
			{
				TFDF tfdf = entry.getValue();
				junzhitem+=tfdf.getTF();
			}
			junzhitem/=map_WordTFDF.size();
			for(Map.Entry<String, TFDF> entry : set)
			{
				TFDF tfdf = entry.getValue();
				fangchatem+=(tfdf.getTF()-junzhitem)*(tfdf.getTF()-junzhitem);
			}
			fangchatem=Math.sqrt(fangchatem);
			System.out.println("簇方差： "+fangchatem+" 簇大小： "+clutersize+" 比值： "+fangchatem/clutersize+"簇号： "+i);
			/*************************************/
			sortWordByTF(map_WordTFDF, list_WordTFDF);
			
			//拿出热点词，取出每个类的热点词
			try(BufferedWriter bw = new BufferedWriter(new FileWriter("corpus/onepassresult.txt", true))){
			 if(clutersize>5)//||fangchatem>20
			 {//
				 Iterator<WordTFDF> iter = list_WordTFDF.iterator();
					bw.append("聚类号："+i+"\n");
					bw.append("热点词：\n");
					int tmp = 0;
					//把热点词输出到文件
					while(iter.hasNext() && tmp<noise)
					{
						WordTFDF wordTFDF = iter.next();
						String line = wordTFDF.getWord()+"   tf="+wordTFDF.getTF();
						bw.append(line+"\n");
						tmp++;
					}
					//输出该剧类中的文件
					for(int k=0; k<clusterTextList.size(); ++k)
					{
						int targetAi = clusterTextList.get(k);
						//bw.append(targetAi+"  ");
						String title = articles.get(targetAi).filename;
						Date datetime = articles.get(targetAi).datetime;
						bw.append(title+"   "+datetime+"\n");
					}
					bw.append("\n");
			 }//
				
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 把词语按照tf值来排序
	 */
	public void sortWordByTF(TreeMap<String, TFDF> map_WordTFDF, 
			ArrayList<WordTFDF> list_WordTFDF)
	{
		WordTFDF wordTFDF;
		Set<Map.Entry<String, TFDF>> set = map_WordTFDF.entrySet();
		for(Map.Entry<String, TFDF> entry : set)
		{
			TFDF tfdf = entry.getValue();
			wordTFDF = new WordTFDF(entry.getKey(), tfdf.getTF(), tfdf.getDF());
			list_WordTFDF.add(wordTFDF);
		}
		//按照TF从大到小排序
		Collections.sort(list_WordTFDF, new Comparator<WordTFDF>(){
			@Override
			public int compare(WordTFDF w1, WordTFDF w2)
			{
				return -(w1.getTF()-w2.getTF());
			}
		});
	}
	
}
