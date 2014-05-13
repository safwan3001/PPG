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
	private HashSet<String> predicateStringHashSet;
	private HashSet<Property> predicatePropertyHashSet;
	private HashSet<HashMap<String,HashSet<String>>> predicateScopeHashSet;
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
		this.predicateStringHashSet=new HashSet<String>();
		this.predicatePropertyHashSet=new HashSet<Property>();
		this.predicateScopeHashSet=new HashSet<HashMap<String,HashSet<String>>>();
		this.setPredicateListFile();
		this.setPredicateScopFile();
	}


	public void closeDataSet(){
		//Finish the transaction - if a write transaction and commit() has not been called, then abort
		dataset.end() ;
		// Close the dataset, potentially releasing any associated resources.
		dataset.close();
	}
	public HashSet<String> readPredicate() throws ClassNotFoundException, IOException{

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.predicateListHashSetDirectory));
		this.predicateStringHashSet=(HashSet<String>) ois.readObject();
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

	public HashSet<Property> getModelPredicates(Model model){

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
		int counter=0;
		int predicateFileScopeNumber=0;
		HashSet<Resource> resourceHashSet;
		HashSet<String> stringHashSet;
		HashMap<String,HashSet<String>> predicateScopeHashMap=new HashMap<String,HashSet<String>>();
		HashSet<String> predicateList=this.readPredicate();
		System.out.println("calculating scope process begin for "+predicateList.size()+" predicate ");
		for (String predicate : predicateList){

			//	if (counter<25){
			//System.out.println("1 "+predicate);
			ResIterator subjectsList=this.getModel().listSubjectsWithProperty(this.getModel().getProperty(predicate));
			//System.out.println("The scop of the predicate "+predicate+" contains "+subjectsList.toSet().size()+" element");
			//System.out.println("2 "+predicate);
			resourceHashSet=(HashSet<Resource>) subjectsList.toSet();
			//System.out.println("3 "+predicate);
			stringHashSet=new HashSet<String>();
			//System.out.println("4 "+predicate);
			for (Resource resource:resourceHashSet){
				stringHashSet.add(resource.toString());
			}
			//System.out.println("5 "+predicate);
			predicateScopeHashMap.put(predicate, stringHashSet);
			//System.out.println("6 "+predicate);
			predicateScopeHashSet.add(predicateScopeHashMap);
			predicateScopeHashMap=new HashMap<String,HashSet<String>>();
			//System.out.println("7 "+predicate);
			float numberOfFilesFloat=(float) predicateList.size()/25;
			int numberOfFilesInteger=(int) Math.ceil(numberOfFilesFloat);
			//System.out.println("size is"+predicateList.size());
			//System.out.println("dddd"+numberOfFilesInteger);
			counter++;
			System.out.println("The counter Values is "+counter);

			if ( predicateScopeHashSet.size()==25 ){
				predicateFileScopeNumber++;
				writePredicateScope(predicateScopeHashSetDirectory+"\\predicateScop"+predicateFileScopeNumber);
				System.out.println("the size of the written hashset is "+predicateScopeHashSet.size());

				resourceHashSet=new HashSet<Resource>();
				stringHashSet=new HashSet<String>();
				predicateScopeHashSet=new HashSet<HashMap<String,HashSet<String>>>();
				//counter=0;
			}
			else if(predicateFileScopeNumber==numberOfFilesInteger-1 && predicateList.size()==counter){
				predicateFileScopeNumber++;
				writePredicateScope(predicateScopeHashSetDirectory+"\\predicateScop"+predicateFileScopeNumber);
				System.out.println("the size of the written hashset is "+predicateScopeHashSet.size());
				resourceHashSet=new HashSet<Resource>();
				stringHashSet=new HashSet<String>();
				predicateScopeHashSet=new HashSet<HashMap<String,HashSet<String>>>();

			}

			//	}
			/*else{
				predicateFileScopeNumber++;
				ResIterator subjectsList=this.getModel().listSubjectsWithProperty(this.getModel().getProperty(predicate));
				resourceHashSet=(HashSet<Resource>) subjectsList.toSet();
				stringHashSet=new HashSet<String>();
				for (Resource resource:resourceHashSet){
					stringHashSet.add(resource.toString());
				}
				predicateScopeHashMap.put(predicate, stringHashSet);
				predicateScopeHashSet.add(predicateScopeHashMap);

				writePredicateScope(predicateScopeHashSetDirectory+"\\predicateScop"+predicateFileScopeNumber);
				System.out.println("the size of the written hashset is "+predicateScopeHashSet.size());

				resourceHashSet=new HashSet<Resource>();
				stringHashSet=new HashSet<String>();
				predicateScopeHashSet=new HashSet<HashMap<String,HashSet<String>>>();
				counter=0;
			}*/
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
			oos.writeObject(predicateScopeHashSet);
			oos.close();
		}
	}

	public HashSet<HashMap<String,HashSet<String>>> readPredicateScope() throws FileNotFoundException, IOException, ClassNotFoundException{

		HashSet<HashMap<String,HashSet<String>>> readPredicateScopeHash=null;
		for (File predicateScopFile: this.getPredicateScopFile().listFiles())
		{
			readPredicateScopeHash=new HashSet<HashMap<String,HashSet<String>>>();
			System.out.println("Reading the file"+predicateScopFile.getPath());
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(predicateScopFile.getPath()));
			readPredicateScopeHash=(HashSet<HashMap<String,HashSet<String>>>) ois.readObject();
			System.out.println("Number of predicates in the file "+predicateScopFile.getPath()+" is "+readPredicateScopeHash.size());
			saveOutputToFile(readPredicateScopeHash.toString(),predicateScopFile.getPath()+".txt");

		}

		return readPredicateScopeHash;


	}

	public void saveOutputToFile(String data,String fileName) throws IOException{
		PrintWriter fstream = new PrintWriter(new FileWriter(fileName));
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data);
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