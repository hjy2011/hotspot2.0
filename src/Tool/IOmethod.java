package Tool;

import java.io.*;

public class IOmethod {
	public static BufferedWriter gerBW(String filename)
	{
		BufferedWriter bw=null;
		try {
			 bw= new BufferedWriter(new FileWriter(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bw;
	}
	public static void closeBW (BufferedWriter bw)
	{
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static BufferedReader gerBR(String filename)
	{
		BufferedReader br=null;
		try {
			 br= new BufferedReader(new FileReader(new File(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return br;
	}
	public static void closeBR (BufferedReader br)
	{
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
