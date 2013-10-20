package com.gdufs.hotspot.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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

import com.gdufs.hotspot.clusterTool.clusterMethod;
import com.gdufs.hotspot.entity.Article;
import com.gdufs.hotspot.entity.CenterPoint;
import com.gdufs.hotspot.entity.TFDF;
import com.gdufs.hotspot.entity.TextVector;
import com.gdufs.hotspot.entity.WordTFDF;

/**
 * 一趟聚类算法
 * @author Administrator
 *
 */
public class OnePass {

	private int size;                                          //文本数目
	private double r;                                          //阈值
	private int FeatureWordSize = 0;
	private ArrayList<Article> articles                        //文本中的词
							= new ArrayList<Article>();
	private TreeMap<String, TFDF> map_WordTFDF
							= new TreeMap<String, TFDF>();     // 存放所有单词的TF与DF -- 临时用
	private ArrayList<WordTFDF> list_WordTFDF
	 						= new ArrayList<WordTFDF>();       //用于排序  -- 临时用,存放特征词
	
	private ArrayList<WordTFDF> featureWordMap
		                    = new ArrayList<WordTFDF>();
	private ArrayList<String> titlelist
		                    = new ArrayList<String>();         //找出所有的标题，然后从原有的list_wordTFDF获取
	                                                           //包含title的拿下来存储到新的featurelist下；

	private ArrayList<TextVector> list_TextVector
							= new ArrayList<TextVector>();     //存放文本向量
	private ArrayList<CenterPoint> list_CenterPoint 
							= new ArrayList<CenterPoint>();
	private TreeMap<String, TFDF> map_HotSpot 
							= new TreeMap<String, TFDF>();
	
	public void doProcess(ArrayList<Article> articleList)
	{
		clusterMethod clustermethod= new clusterMethod();
		this.articles = articleList;
		this.size = this.articles.size();
		System.out.println("article size:"+this.size);
		// 2 - 获取tfdf
		System.out.println("2 计算tfdf");
		clustermethod.getWordTFDF( articles,map_WordTFDF);
		// 3 - 
		System.out.println("3 获取特征词");
		FeatureWordSize=clustermethod.getFeatureWordbyTitle(articles,titlelist,map_WordTFDF,
				featureWordMap);
		// 4 - 
		System.out.println("4 生成向量");
		clustermethod.makeTextToVector(size,featureWordMap,articles,
				FeatureWordSize,list_TextVector);
		// 5 -
		System.out.println("5 获得阈值");
	    r=clustermethod.getR(list_TextVector,size,FeatureWordSize);
		// 6 
		System.out.println("6 聚类");
		clustering();
		// 7
		System.out.println("7 把热点词存到文本 onepassresult.txt");
		clustermethod.getHotSpot(list_CenterPoint, map_WordTFDF, 
				list_WordTFDF,list_TextVector,articles);
	}
	public  void printF()
	{
		int i;
		for( i=0;i<featureWordMap.size();i++)
			System.out.println(featureWordMap.get(i).getWord());
		System.out.println("feature "+i);
	}
	public void printlist()
	{
		int i;
		for( i=0;i<titlelist.size();i++)
			System.out.println(titlelist.get(i));
		System.out.println("list "+i);
	}
	
	public void createNewCluster(TextVector textVector, int num)
	{
		textVector.clusterNumber = num;
		CenterPoint point = new CenterPoint();
		point.clusterNumber  = num;
		point.newsNumber = 1;
		point.tfidf = new double[FeatureWordSize];
		//质心
		for(int i=0; i<FeatureWordSize; ++i)
		{
			point.tfidf[i] = textVector.tfidf[i];
		}
		list_CenterPoint.add(point);
	}
	
	/**
	 *一趟聚类,算法如下：
	 *	1. 初始时，聚类集合为空，读入一个新的对象； 
	 *	2. 以这个对象构造一个新的类； 
	 *	3. 若已到末尾，则转 6，否则读入新对象，利用给定的相似度定义，计算它与每个已有类间的相似度，并选择最
		   大的相似度； 
	 *	4. 若最大相似度小于给定的阈值 r ，转 2； 
	 *	5. 否则将该对象并入具有最大相似度的类中，并更新该类的各分类属性值的统计频度，转 3； 
	 *	6. 结束。
	 */
	public void clustering()
	{
		CenterPoint maxSimPoint = new CenterPoint();
		int clusterNumber = 1;
		int maxSimClusterNum = -1;
		double maxSim;
		double sim;
		double fenzi;
		double fenmu1;
		double fenmu2;
		
		//第一个新闻成为一个类
		createNewCluster(list_TextVector.get(0), clusterNumber);
		clusterNumber++;
		//从第二个新闻到最后一个新闻
		for(int i=1; i<list_TextVector.size(); ++i)
		{
			maxSim = 0.0;
			TextVector curTextVector = list_TextVector.get(i);
			//求该新闻与所有类质心最大相似度
			for(int j=0; j<list_CenterPoint.size(); ++j)
			{
				fenzi = 0.0;
				fenmu1 = 0.0;
				fenmu2 = 0.0;
				CenterPoint center = list_CenterPoint.get(j);
				for(int k=0; k<FeatureWordSize; ++k)
				{
					fenzi += curTextVector.tfidf[k] * center.tfidf[k];
					fenmu1 += curTextVector.tfidf[k] * curTextVector.tfidf[k];
					fenmu2 += center.tfidf[k] * center.tfidf[k];
				}
				
				sim = fenzi/ Math.sqrt(fenmu1 * fenmu2);
				if(sim > maxSim)
				{
					maxSim = sim;
					maxSimClusterNum = center.clusterNumber;
					maxSimPoint = center;
				}								
			}
			
			if(maxSim > r)
			{
				curTextVector.clusterNumber = maxSimClusterNum;
				maxSimPoint.newsNumber++;
				//重新计算质心
				for(int j=0; j<FeatureWordSize; ++j)
				{
					maxSimPoint.tfidf[j] = 0.0;
				}
				//把该类的tfidf对应求和
				for(int j=0; j<list_TextVector.size(); ++j)
				{
					if(list_TextVector.get(j).clusterNumber == curTextVector.clusterNumber)
					{
						for(int k=0; k<FeatureWordSize; ++k)
						{
							maxSimPoint.tfidf[k] += list_TextVector.get(j).tfidf[k];
						}
					}
				}
				//重新计算
				for(int j=0; j<FeatureWordSize; ++j)
				{
					maxSimPoint.tfidf[j] /= maxSimPoint.newsNumber;
				}
			}
			else
			{
				//创建一个新类
				createNewCluster(curTextVector, clusterNumber);
				clusterNumber++;
			}
		}
		System.out.printf("聚类个数%d%n", clusterNumber-1);
		
		//存储聚类结果
		//聚类号 ， 每个聚类有哪些文件
	}

}
