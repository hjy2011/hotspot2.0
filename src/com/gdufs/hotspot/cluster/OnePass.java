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
 * һ�˾����㷨
 * @author Administrator
 *
 */
public class OnePass {

	private int size;                                          //�ı���Ŀ
	private double r;                                          //��ֵ
	private int FeatureWordSize = 0;
	private ArrayList<Article> articles                        //�ı��еĴ�
							= new ArrayList<Article>();
	private TreeMap<String, TFDF> map_WordTFDF
							= new TreeMap<String, TFDF>();     // ������е��ʵ�TF��DF -- ��ʱ��
	private ArrayList<WordTFDF> list_WordTFDF
	 						= new ArrayList<WordTFDF>();       //��������  -- ��ʱ��,���������
	
	private ArrayList<WordTFDF> featureWordMap
		                    = new ArrayList<WordTFDF>();
	private ArrayList<String> titlelist
		                    = new ArrayList<String>();         //�ҳ����еı��⣬Ȼ���ԭ�е�list_wordTFDF��ȡ
	                                                           //����title���������洢���µ�featurelist�£�

	private ArrayList<TextVector> list_TextVector
							= new ArrayList<TextVector>();     //����ı�����
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
		// 2 - ��ȡtfdf
		System.out.println("2 ����tfdf");
		clustermethod.getWordTFDF( articles,map_WordTFDF);
		// 3 - 
		System.out.println("3 ��ȡ������");
		FeatureWordSize=clustermethod.getFeatureWordbyTitle(articles,titlelist,map_WordTFDF,
				featureWordMap);
		// 4 - 
		System.out.println("4 ��������");
		clustermethod.makeTextToVector(size,featureWordMap,articles,
				FeatureWordSize,list_TextVector);
		// 5 -
		System.out.println("5 �����ֵ");
	    r=clustermethod.getR(list_TextVector,size,FeatureWordSize);
		// 6 
		System.out.println("6 ����");
		clustering();
		// 7
		System.out.println("7 ���ȵ�ʴ浽�ı� onepassresult.txt");
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
		//����
		for(int i=0; i<FeatureWordSize; ++i)
		{
			point.tfidf[i] = textVector.tfidf[i];
		}
		list_CenterPoint.add(point);
	}
	
	/**
	 *һ�˾���,�㷨���£�
	 *	1. ��ʼʱ�����༯��Ϊ�գ�����һ���µĶ��� 
	 *	2. �����������һ���µ��ࣻ 
	 *	3. ���ѵ�ĩβ����ת 6����������¶������ø��������ƶȶ��壬��������ÿ�������������ƶȣ���ѡ����
		   ������ƶȣ� 
	 *	4. ��������ƶ�С�ڸ�������ֵ r ��ת 2�� 
	 *	5. ���򽫸ö��������������ƶȵ����У������¸���ĸ���������ֵ��ͳ��Ƶ�ȣ�ת 3�� 
	 *	6. ������
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
		
		//��һ�����ų�Ϊһ����
		createNewCluster(list_TextVector.get(0), clusterNumber);
		clusterNumber++;
		//�ӵڶ������ŵ����һ������
		for(int i=1; i<list_TextVector.size(); ++i)
		{
			maxSim = 0.0;
			TextVector curTextVector = list_TextVector.get(i);
			//�������������������������ƶ�
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
				//���¼�������
				for(int j=0; j<FeatureWordSize; ++j)
				{
					maxSimPoint.tfidf[j] = 0.0;
				}
				//�Ѹ����tfidf��Ӧ���
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
				//���¼���
				for(int j=0; j<FeatureWordSize; ++j)
				{
					maxSimPoint.tfidf[j] /= maxSimPoint.newsNumber;
				}
			}
			else
			{
				//����һ������
				createNewCluster(curTextVector, clusterNumber);
				clusterNumber++;
			}
		}
		System.out.printf("�������%d%n", clusterNumber-1);
		
		//�洢������
		//����� �� ÿ����������Щ�ļ�
	}

}
