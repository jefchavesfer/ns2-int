package main.java;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
class FileReader 
{
   private BufferedReader br;
   
   FileReader(String fileName){
	   try{
		   FileInputStream fstream =  new FileInputStream(fileName);
		   DataInputStream in = new DataInputStream(fstream);
		   this.br = new BufferedReader(new InputStreamReader(in));	   
	   }catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
	   }
   }
   
   public String readLine() throws IOException
   {
	   return br.readLine();
   }
}
