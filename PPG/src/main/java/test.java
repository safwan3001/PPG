import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Property;


public class test {
	
	public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException{
    	HashMap attHashMap=new HashMap();
    	attHashMap.put(1, "Ali");
    	attHashMap.put(1, "mmm");
    	System.out.println(attHashMap.toString());
    	
    	
    	HashSet<HashMap<String,HashSet<String>>> test=new HashSet<HashMap<String,HashSet<String>>>();
    	
    	HashMap<String,HashSet<String>> testHashMap=new HashMap<String,HashSet<String>>();
    	HashSet testHashSet=new HashSet();
    	testHashSet.add(1);
    	testHashSet.add(2);
    	testHashSet.add(3);
    	testHashMap.put("Safwan",testHashSet);
    	
    	test.add(testHashMap);
    	
    	testHashMap=new HashMap<String,HashSet<String>>();
    	testHashSet=new HashSet();
    	testHashSet.add(1);
    	testHashSet.add(2);
    	testHashSet.add(3);
    	testHashMap.put("Ali",testHashSet);
    	
    	test.add(testHashMap);
    	
    	System.out.println(testHashMap.toString());
    	System.out.println(test.toString());

    	float x=(float)1374/25;
    	System.out.println("x is "+x);

    	System.out.println("testing 1374/25 "+Math.ceil(x));
    	System.out.println(Math.ceil(54.9));


    	
    	
    	/*ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("d:/test.txt"));
		oos.writeObject(test);*/
		
		
    	PrintWriter fstream = new PrintWriter(new FileWriter("d:\\test.txt"));
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(test.toString());
		out.close();
    	
    	//ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:/test.txt"));
    	
		//String readHashSet=(String)ois.readObject();
		//System.out.println(readHashSet);
	}

}
