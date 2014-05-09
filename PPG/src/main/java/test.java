import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.rdf.model.Property;


public class test {
	
	public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException{
    	HashMap attHashMap=new HashMap();
    	attHashMap.put(1, "Ali");
    	attHashMap.put(1, "mmm");
    	System.out.println(attHashMap.toString());
    	
    	
    	HashSet<String> test=new HashSet();
    	test.add("yyyyyy");
    	test.add("eeeeee");
    	System.out.println(test.toString());
    	
    	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("d:/test.txt"));
		String readHashSet=(String)ois.readObject();
		System.out.println(readHashSet);
	}

}
