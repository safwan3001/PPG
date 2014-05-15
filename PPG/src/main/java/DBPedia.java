import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.Timer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.tdb.TDBFactory;


public class DBPedia
{
	private int numberOfPredicates=0;
	private LinkedHashSet<String> predicateStringHashSet;
	private LinkedHashSet<Property> predicatePropertyHashSet;
	private LinkedHashMap<String,LinkedHashSet<String>> predicateScopeHashMap;
	//private LinkedHashSet<LinkedHashMap<String,LinkedHashSet<String>>> predicateScopeHashSet;
	private String predicateListHashSetDirectory;
	private String predicateScopeHashSetDirectory;
	private String DBDirectory;
	private Model model;
	private Dataset dataset;
	private File predicateListFile;
	private File predicateScopFile;

	public DBPedia(String predicateListHashSetDirectory ,String predicateScopeHashSetDirectory,String DBDirectory){

		this.predicateListHashSetDirectory=predicateListHashSetDirectory;
		this.predicateScopeHashSetDirectory=predicateScopeHashSetDirectory;

		this.DBDirectory=DBDirectory;
		dataset = TDBFactory.createDataset(DBDirectory);

		this.model = dataset.getDefaultModel() ;
		this.predicateStringHashSet=new LinkedHashSet<String>();
		this.predicatePropertyHashSet=new LinkedHashSet<Property>();
		this.predicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
		this.setPredicateListFile();
		this.setPredicateScopFile();
	}


	public void closeDataSet(){
		//Finish the transaction - if a write transaction and commit() has not been called, then abort
		dataset.end() ;
		// Close the dataset, potentially releasing any associated resources.
		dataset.close();
	}
	public LinkedHashSet<String> readPredicate() throws ClassNotFoundException, IOException{

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.predicateListHashSetDirectory));
		this.predicateStringHashSet=(LinkedHashSet<String>) ois.readObject();
		return this.predicateStringHashSet;

	}
	public void writePredicate() throws FileNotFoundException, IOException{

		predicatePropertyHashSet=this.getModelPredicates(this.getModel());

		if (this.getPredicateListFile().exists()){
			System.out.println("File already exists");
		}
		else{
			System.out.println("Creating the file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.predicateListHashSetDirectory));

			for(Property predicate: predicatePropertyHashSet )
			{
				this.predicateStringHashSet.add(predicate.toString());
			}
			oos.writeObject(this.predicateStringHashSet);
		}

	}

	public LinkedHashSet<Property> getModelPredicates(Model model){

		if(this.getPredicateListFile().exists()){
			System.out.println("File already exists no need to check all predicates");
			return predicatePropertyHashSet;
		}
		else{
			StmtIterator r=model.listStatements();
			while (r.hasNext()) {
				predicatePropertyHashSet.add(r.nextStatement().getPredicate());
			}

			setNumberOfPredicates();
			System.out.println("Number of predicates in the model is "+this.getNumberOfPredicates());
			return predicatePropertyHashSet;
		}
	}

	public void getPredicateScope() throws ClassNotFoundException, IOException{

		//int counter=0;
		//int predicateFileScopeNumber=0;
		//HashSet<Resource> resourceHashSet=null;
		//LinkedHashSet<String> stringLinkedHashSet;
		//LinkedHashSet<String> stringHashSet=null;
		LinkedHashSet<String> predicateList=this.readPredicate();
		System.out.println("calculating scope process begin for "+predicateList.size()+" predicate ");
		for (String predicate : predicateList){

			/*if(predicate.equals("http://purl.org/dc/terms/subject")){
				continue;}*/
			//	if (counter<25){
			System.out.println("1 "+predicate);
			ResIterator subjectsList=this.getModel().listSubjectsWithProperty(this.getModel().getProperty(predicate));
			//System.out.println("The scop of the predicate "+predicate+" contains "+subjectsList.toSet().size()+" element");
			//System.out.println("2 "+predicate);
			HashSet<Resource> resourceHashSet=(HashSet<Resource>) subjectsList.toSet();
			subjectsList=null;
			//System.gc();

			//System.out.println("3 "+predicate);
			LinkedHashSet<String> stringHashSet=new LinkedHashSet<String>();
			//System.out.println("4 "+predicate);
			for (Resource resource:resourceHashSet){
				stringHashSet.add(resource.toString());
			}
			//resourceHashSet=null;
			//System.gc();
			//System.out.println("5 "+predicate);
			//stringLinkedHashSet=new LinkedHashSet<String>(stringHashSet);
			//stringHashSet=null;
			//LinkedHashMap<String,LinkedHashSet<String>> predicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
			this.predicateScopeHashMap.put(predicate, stringHashSet);
			//stringHashSet=null;
			//System.gc();

			//System.out.println("6 "+predicate);
			//predicateScopeHashSet.add(predicateScopeHashMap);
			//predicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
			String fileName=predicate.replace("/","_").replace(":",";");
			writePredicateScope(predicateScopeHashSetDirectory+"\\"+fileName);
			this.predicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
			resourceHashSet=new LinkedHashSet<Resource>();
			stringHashSet=new LinkedHashSet<String>();
		}
	}

	public void writePredicateScope(String predicateScopeHashSetFileName) throws ClassNotFoundException, IOException{
		System.out.println("predicateScopeHashSetFileName is "+predicateScopeHashSetFileName);

		if (new File(predicateScopeHashSetFileName).exists()){
			System.out.println("File already exists");
		}
		else{
			System.out.println("Call getPredicate scope");
			//this.getPredicateScope();
			System.out.println("Creating the predicate scope file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(predicateScopeHashSetFileName));
			System.out.println("write getPredicate scope to file");
			oos.writeObject(this.predicateScopeHashMap);
			oos.close();
		}
	}

	public LinkedHashMap<String,LinkedHashSet<String>> readPredicateScope() throws FileNotFoundException, IOException, ClassNotFoundException{

		LinkedHashMap<String,LinkedHashSet<String>> readPredicateScopeHashMap=null;
		LinkedHashSet<String> modelPredicates=readPredicate();

		//for (String predicate : modelPredicates){

		File[] filesPathArray=new File[this.getNumberOfPredicates()];
		filesPathArray=this.getPredicateScopFile().listFiles();
		int counter=0;
		System.out.println("The size of the file array is "+filesPathArray.length);
		for (File predicateScopFile: filesPathArray)
		{
			readPredicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
			System.out.println("Reading the file "+predicateScopFile.getPath());
			System.out.println("File name is "+predicateScopFile.getName());
			String predicateName=predicateScopFile.getName().replace(";", ":").replace("_", "/");
			System.out.println("Predicate name after fixing is "+predicateName);

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(predicateScopFile.getPath()));

			readPredicateScopeHashMap=(LinkedHashMap<String,LinkedHashSet<String>>) ois.readObject();
			//saveOutputToFile(readPredicateScopeHashMap.toString(),"d:\\xx.txt");
			//System.out.println("Scope for the predicate "+fileName+" is "+readPredicateScopeHashMap.get(fileName));
			LinkedHashSet<String> scope=readPredicateScopeHashMap.get(predicateName);
			System.out.println("Number of predicates in the file  "+predicateScopFile.getPath()+" is "+readPredicateScopeHashMap.size());
			counter++;
			for(int i=counter; i<filesPathArray.length;i++){
				String predicateName1=filesPathArray[i].getName().replace(";", ":").replace("_", "/");
				LinkedHashMap predicateScopeHashMap=new LinkedHashMap<String,LinkedHashSet<String>>();
				System.out.println("The Value of the outer counter is "+counter+" and the value of the inner counter is "+i);
				System.out.println("The File name is "+filesPathArray[i].getPath());
				ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(filesPathArray[i].getPath()));
				try{
					predicateScopeHashMap=(LinkedHashMap<String,LinkedHashSet<String>>)ois1.readObject();
					LinkedHashSet<String> scope1= (LinkedHashSet) predicateScopeHashMap.get(predicateName1);
					//filesPathArray[i];
					System.out.println("The Predicate name is "+predicateName1);
					intersectScopes(predicateName,scope,predicateName1,scope1);
				}
				catch (Exception EOFException ){
					System.out.println("Problem in file "+filesPathArray[i].getPath());
					System.out.println("The exception is "+EOFException.toString());

				}

			}

		}

		return readPredicateScopeHashMap;
	}

	public void intersectScopes(String key1,LinkedHashSet<String> scope1,String key2, LinkedHashSet<String> scope2) throws IOException{

		LinkedHashSet<String> intersectionHashSet=new LinkedHashSet<String>();
		int precedenceCounte=0;
		if (scope1.size()>=scope2.size()){

			System.out.println("Case 1");

			intersectionHashSet=new LinkedHashSet<String>(scope1);
			System.out.println("Before rerain all intersectionHashSet.size()="+intersectionHashSet.size());

			intersectionHashSet.retainAll(scope2);
			System.out.println("intersectionHashSet.size()="+intersectionHashSet.size());
			System.out.println("scope1.size()="+scope1.size());
			System.out.println("scope2.size()="+scope2.size());
			if(intersectionHashSet.size()==scope2.size()){
				///key2 precede k1
				System.out.println("____Case 1 The predicate "+key2+" precede "+key1);
				saveOutputToFile("Case1 The predicate "+key2+" precede "+key1,"d:\\precedence.txt");
				precedenceCounte++;
			}
			else{
				System.out.println("Case 1 No precedency between "+key1+" and "+key2);
			}

		}
		else{
			System.out.println("Case 2");

			intersectionHashSet=new LinkedHashSet<String>(scope2);
			System.out.println("Before rerain all intersectionHashSet.size()="+intersectionHashSet.size());
			intersectionHashSet.retainAll(scope1);
			System.out.println("intersectionHashSet.size()="+intersectionHashSet.size());
			System.out.println("scope1.size()="+scope1.size());
			System.out.println("scope2.size()="+scope2.size());

			if(intersectionHashSet.size()==scope1.size()){
				///key2 precede k1
				System.out.println("______Case2 The predicate "+key1+" precede "+key2);
				saveOutputToFile("Case 2 The predicate "+key1+" precede "+key2,"d:\\precedence.txt");
				precedenceCounte++;

			}
			else{
				System.out.println("Case 2 No precedency between "+key1+" and "+key2);

			}
		}
		System.out.println("Number for precedence counter "+precedenceCounte);
	}

	public void saveOutputToFile(String data,String fileName) throws IOException{
		PrintWriter fstream = new PrintWriter(new FileWriter(fileName,true),true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data);
		out.close();
	}

	public String getPredicateListFilePath() {
		return predicateListHashSetDirectory;
	}

	public void setPredicateListFilePath(String predicateListHashSetDirectory) {
		this.predicateListHashSetDirectory = predicateListHashSetDirectory;
	}

	public int getNumberOfPredicates() {
		return numberOfPredicates;
	}

	public void setNumberOfPredicates() {
		this.numberOfPredicates = predicateStringHashSet.size();
	}

	public Model getModel() {
		return model;
	}

	public void setModel() {
		this.model = dataset.getDefaultModel() ;
	}

	public File getPredicateListFile() {
		return predicateListFile;
	}

	public void setPredicateListFile() {
		this.predicateListFile = new File(predicateListHashSetDirectory);
	}

	public File getPredicateScopFile() {
		return predicateScopFile;
	}

	public void setPredicateScopFile() {
		this.predicateScopFile = new File(this.predicateScopeHashSetDirectory);
	}

	public String getPredicateScopeHashSetDirectory() {
		return this.predicateScopeHashSetDirectory;
	}


	public void setPredicateScopeHashSetDirectory(String predicateScopeHashSetDirectory) {
		this.predicateScopeHashSetDirectory = predicateScopeHashSetDirectory;
	}
}