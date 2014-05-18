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
import java.util.LinkedHashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Property;


public class test {
	
	public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException{
    	
		HashMap attHashMap=new HashMap();
    	attHashMap.put(1, "Ali");
    	attHashMap.put(1, "mmm");
    	System.out.println(attHashMap.toString());
    	
    	
    	LinkedHashSet<HashMap<String,LinkedHashSet<String>>> test=new LinkedHashSet<HashMap<String,LinkedHashSet<String>>>();
    	
    	HashMap<String,LinkedHashSet<String>> testHashMap=new HashMap<String,LinkedHashSet<String>>();
    	LinkedHashSet testHashSet=new LinkedHashSet();
    	testHashSet.add(1);
    	testHashSet.add(1);
    	testHashSet.add(1);
    	LinkedHashSet xx=new LinkedHashSet(testHashSet);
    	testHashMap.put("Safwan",testHashSet);
    	
    	test.add(testHashMap);
    	
    	testHashMap=new HashMap<String,LinkedHashSet<String>>();
    	testHashSet=new LinkedHashSet();
    	testHashSet.add(1);
    	testHashSet.add(2);
    	testHashSet.add(3);
    	testHashMap.put("Ali",testHashSet);
    	
    	System.out.println(testHashSet);

    	
    	test.add(testHashMap);
    	
    	System.out.println(testHashMap.toString());
    	System.out.println(test.toString());

    	float x=(float)1374/25;
    	System.out.println("x is "+x);

    	System.out.println("testing 1374/25 "+Math.ceil(x));
    	System.out.println(Math.ceil(54.9));


    	String myString="http://purl.org/dc/terms/subject";
    	myString=myString.replace("/","_").replace(":","_").replace("//", "_");
    	System.out.println(myString);

    	
    	/*ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("d:/test.txt"));
		oos.writeObject(test);*/
		
		
    	PrintWriter fstream = new PrintWriter(new FileWriter("d:\\test.txt"));
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(test.toString());
		out.close();
		
		HashSet<String> s1=new HashSet<String>();
		s1.add("a");
		s1.add("b");
		s1.add("c");
    	
		HashSet<String> s2=new HashSet<String>();
		s2.add("a");
		s2.add("b");
		s2.add("c");
		s2.add("d");
		
		HashSet<String> intersectionHashSet=new HashSet<String>(s2);
		

		intersectionHashSet.retainAll(s1);
		System.out.println("The intersectionHashSet value is "+intersectionHashSet);
		System.out.println("The intersectionHashSet value is "+intersectionHashSet.retainAll(s1));

    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:\\predicateScope\\http;__www.w3.org_2003_01_geo_wgs84_pos#lat"));
    	HashMap<String,HashSet<String>>predicateScopeHashMap=(HashMap<String,HashSet<String>>)ois.readObject();
    	System.out.println(predicateScopeHashMap.get("http://www.w3.org/2003/01/geo/wgs84/pos#lat"));
    	
		//String readHashSet=(String)ois.readObject();
		//System.out.println(readHashSet);
	}

}
