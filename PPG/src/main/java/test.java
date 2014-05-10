import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    	
    	
    	HashSet<HashMap<String,Set>> test=new HashSet<HashMap<String,Set>>();
    	
    	HashMap<String,Set> testHashMap=new HashMap<String,Set>();
    	HashSet testHashSet=new HashSet();
    	testHashSet.add(1);
    	testHashMap.put("qqqqq",testHashSet);
    	
    	/*test.add(new HashMap("yyyyyy",HashSet);
    	test.add("eeeeee");
    	System.out.println(test.toString());*/
    	
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:/test.txt"));
		//String readHashSet=(String)ois.readObject();
		//System.out.println(readHashSet);
	}

}
