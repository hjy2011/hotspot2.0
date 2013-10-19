package Tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;



public class proProcessTool {
	/**
	 * ��ʽ��HTML�ı�
	 * @param content
	 * @return
	 */
	public static String html(String content) {
		if(content==null) return "";        
		    String html = content;
		    html = StringUtils.replace(html, "'", "&apos;");
		    html = StringUtils.replace(html, "\"", "&quot;");
		    html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");// �滻����
		    //html = StringUtils.replace(html, " ", "&nbsp;");// �滻�ո�
		    html = StringUtils.replace(html, "<", "&lt;");
		    html = StringUtils.replace(html, ">", "&gt;");
		    return html;
	}
	public static String process(String content) {
	    if(content==null) return "";            
	    Pattern p=Pattern.compile("&[a-z]{1,19};"); 
	    Matcher m=p.matcher(content); 
    	String newStr=null;
	    while(m.find())
	    {
	    	switch(m.group())
	    	{
	    	
	    	case "&apos;":
	    		newStr=content.replaceAll("&apos;", "'");break;
	    	case "&quot;":
	    		newStr=content.replaceAll("&quot;", "\"");break;
	    	case "&lt;":
	    		newStr=content.replaceAll("&nbsp;", "<");break;
	    	case "&gt;":
	    		newStr=content.replaceAll("&gt;", ">");break;
	    	}
	    	content=newStr;
	    }
	    content=newStr.replaceAll("&nbsp;&nbsp;", "\t");
    	return content;
	}
	public static String processTitle(String content)
	{
		//ȥͣ�ô�
		//ȥ����ı�����
		//, ; : ? ��
		String newcontent= content.replaceAll("[,;:?��]", " ");
		return newcontent;
	}
	public static void main(String []args)
	{
		String content="showrooms: in china &quot; internet &gt; &apos; ;concessions; coming to china? don;t hold china��s";
		String news=process(content);
		System.out.println(news);
	}

}
