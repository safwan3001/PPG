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
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.Timer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
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
	private HashMap<String,HashSet<String>> predicateScopeHashMap;
	private LinkedHashSet<HashMap<String,HashSet<String>>> predicateValueHashSet;
	private String predicateListHashSetDirectory;
	private String predicateScopeHashSetDirectory;
	private String predicateValuesScopeHashSetDirectory;
	private String DBDirectory;
	private Model model;
	private Dataset dataset;
	private File predicateListFile;
	private File predicateScopFile;
	private File predicateValueScopeFile;
	private int predicatePrecedenceCount=0;
	private int valuePrecedenceCount=0;

	public DBPedia(String predicateListHashSetDirectory ,String predicateScopeHashSetDirectory,String predicateValuesScopeHashSetDirectory,String DBDirectory){

		this.setPredicateListHashSetDirectory(predicateListHashSetDirectory);
		//this.predicateListHashSetDirectory=predicateListHashSetDirectory;

		this.setPredicateScopeHashSetDirectory(predicateScopeHashSetDirectory);
		//this.predicateScopeHashSetDirectory=predicateScopeHashSetDirectory;
		this.setPredicateValuesScopeHashSetDirectory(predicateValuesScopeHashSetDirectory);
		this.setDBDirectory(DBDirectory);
		//this.DBDirectory=DBDirectory;

		dataset = TDBFactory.createDataset(DBDirectory);
		this.setModel();
		//this.model = dataset.getDefaultModel() ;

		this.predicateStringHashSet=new LinkedHashSet<String>();
		this.predicatePropertyHashSet=new LinkedHashSet<Property>();
		this.predicateScopeHashMap=new HashMap<String,HashSet<String>>();
		this.setPredicateListFile();
		this.setPredicateScopFile();
		this.setPredicateValueScopeFile();
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
		System.out.println("number of predicates in the model is "+this.predicateStringHashSet.size());
		return this.predicateStringHashSet;

	}

	public void writePredicate() throws FileNotFoundException, IOException{

		this.predicatePropertyHashSet=this.getModelPredicates(this.getModel());

		if (this.getPredicateListFile().exists()){
			System.out.println("Predicate List File already exists");
		}
		else{
			System.out.println("Creating the predicate List file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.getPredicateListHashSetDirectory()));

			for(Property predicate: this.predicatePropertyHashSet )
			{
				this.predicateStringHashSet.add(predicate.toString());
			}
			oos.writeObject(this.predicateStringHashSet);
		}

	}

	public LinkedHashSet<Property> getModelPredicates(Model model){

		if(this.getPredicateListFile().exists()){
			System.out.println("Predicate List File already exists no need to check all predicates");
			return this.predicatePropertyHashSet;
		}
		else{
			StmtIterator r=model.listStatements();
			while (r.hasNext()) {
				predicatePropertyHashSet.add(r.nextStatement().getPredicate());
			}

			setNumberOfPredicates();
			System.out.println("Number of predicates in the model is "+this.getNumberOfPredicates());
			return this.predicatePropertyHashSet;
		}
	}

	public void getPredicateScope() throws ClassNotFoundException, IOException{

		LinkedHashSet<String> predicateList=this.readPredicate();
		System.out.println("calculating scope process begin for "+predicateList.size()+" predicate ");
		for (String predicate : predicateList){

			System.out.println("Calculating Scope for predicate "+predicate);
			ResIterator subjectsList=this.getModel().listSubjectsWithProperty(this.getModel().getProperty(predicate));
			HashSet<Resource> resourceHashSet=(HashSet<Resource>) subjectsList.toSet();
			subjectsList=null;
			HashSet<String> stringHashSet=new HashSet<String>();

			for (Resource resource:resourceHashSet){
				stringHashSet.add(resource.toString());
			}
			resourceHashSet=null;

			this.predicateScopeHashMap.put(predicate.toString(), stringHashSet);

			String fileName=this.generatePredicateFileName(predicate.toString());
			writePredicateScope(this.getPredicateScopeHashSetDirectory()+"\\"+fileName);

			this.predicateScopeHashMap=new HashMap<String,HashSet<String>>();
			resourceHashSet=new HashSet<Resource>();
			stringHashSet=new HashSet<String>();
		}
	}

	public void writePredicateScope(String predicateScopeHashSetFileName) throws ClassNotFoundException, IOException{

		System.out.println("Wtriting predicateScopeHashSet in the fileName "+predicateScopeHashSetFileName);
		if (new File(predicateScopeHashSetFileName).exists()){
			System.out.println("predicateScopeHashSetFileName File is already exists");
		}
		else{
			//this.getPredicateScope();
			System.out.println("Creating the predicate scope file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(predicateScopeHashSetFileName));
			System.out.println("write getPredicate scope to file");
			oos.writeObject(this.predicateScopeHashMap);
			oos.reset();
			oos.close();
		}
	}

	@SuppressWarnings("unchecked")
	public HashMap<String,HashSet<String>> readPredicateScope() throws FileNotFoundException, IOException, ClassNotFoundException{

		@SuppressWarnings("unchecked")
		HashMap<String,HashSet<String>> readPredicateScopeHashMap=null;
		//LinkedHashSet<String> modelPredicatesHashSet=readPredicate();
		//this.saveOutputToFile(modelPredicatesHashSet.toString(), "D:\\list.txt");

		File[] filesPathArray=new File[this.getNumberOfPredicates()];
		filesPathArray=this.getPredicateScopFile().listFiles();
		int counter=0;
		System.out.println("The size of the file array is "+filesPathArray.length);
		for (File predicateScopFile: filesPathArray)
		{
			readPredicateScopeHashMap=new HashMap<String,HashSet<String>>();
			System.out.println("---------------- Outer Loop begin ----------------"+counter);
			System.out.println("Reading the file "+predicateScopFile.getPath());
			System.out.println("File name is "+predicateScopFile.getName());
			String predicateName=this.getPredicateNameFromFileName(predicateScopFile.getName());
			System.out.println("Predicate name after fixing is "+predicateName);

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(predicateScopFile.getPath()));
			readPredicateScopeHashMap=(HashMap<String,HashSet<String>>) ois.readObject();

			HashSet<String> scope=readPredicateScopeHashMap.get(predicateName);
			counter++;

			for(int i=counter; i<filesPathArray.length;i++){

				String predicateName1=this.getPredicateNameFromFileName(filesPathArray[i].getName());
				HashMap<String,HashSet<String>> predicateScopeHashMap=new HashMap<String,HashSet<String>>();
				System.out.println("The Value of the outer counter is "+counter+" and the value of the inner counter is "+i);
				System.out.println("The File name is "+filesPathArray[i].getPath());
				ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(filesPathArray[i].getPath()));
				predicateScopeHashMap=(HashMap<String,HashSet<String>>)ois1.readObject();
				HashSet<String> scope1=predicateScopeHashMap.get(predicateName1);
				System.out.println("The Predicate name is "+predicateName1);
				this.intersectPredicateScopes(predicateName, scope, predicateName1, scope1,"d:\\precedence.json",this.predicatePrecedenceCount,"Predicate",null,null);
			}
			System.out.println("---------------- Outer Loop finish ----------------"+counter);
		}
		System.out.println("Number for precedence counter "+predicatePrecedenceCount);
		return readPredicateScopeHashMap;
	}

	public void intersectPredicateScopes(String key1,HashSet<String> scope1,String key2, HashSet<String> scope2,String precedenceFileName,int precedenceCounter,String flag,
			String predicate1, String predicate2) throws IOException{

		LinkedHashSet<String> intersectionHashSet=new LinkedHashSet<String>();
		//int precedenceCounte=0;

		if (scope1.size()>scope2.size()){

			//System.out.println("Case 1");
			intersectionHashSet=new LinkedHashSet<String>(scope1);
			intersectionHashSet.retainAll(scope2);

			//System.out.println("intersectionHashSet.size()="+intersectionHashSet.size());
			//System.out.println("scope1.size()="+scope1.size());
			//System.out.println("scope2.size()="+scope2.size());
			if(intersectionHashSet.size()==scope2.size()){
				///key2 precede k1
				System.out.println("____Case 1 The predicate "+key1+" precede "+key2);
				if(flag.equals("Predicate")){
					saveOutputToFile("Case1 The predicate "+key1+" precede "+key2,"d:\\precedence.txt");
					saveOutputToFile("{source: \""+key1+"\", target: \""+key2+"\", type:\"suit\" },\n",precedenceFileName);	
				}
				if(flag.equals("Value")){
					//saveOutputToFile("Case1 The predicate "+key1+" precede "+key2,"d:\\precedence.txt");
					saveOutputToFile("Value "+key1 +" of Predicate "+predicate1+ "Precede value "+key2+" of Predicate "+predicate2,"d:\\valuePrecedence.txt");
					saveOutputToFile("{source: \""+key1+"\", target: \""+key2+"\", type:\"suit\" },\n",precedenceFileName);	
				}				

				precedenceCounter++;
			}
			else{
				System.out.println("Case 1 No precedency between "+key1+" and "+key2);
			}

		}
		else if (scope1.size()<scope2.size()){
			System.out.println("Case 2");
			intersectionHashSet=new LinkedHashSet<String>(scope2);
			intersectionHashSet.retainAll(scope1);
			System.out.println("intersectionHashSet.size()="+intersectionHashSet.size());
			System.out.println("scope1.size()="+scope1.size());
			System.out.println("scope2.size()="+scope2.size());

			if(intersectionHashSet.size()==scope1.size()){
				System.out.println("______Case2 The predicate "+key2+" precede "+key1);
				if(flag.equals("Predicate")){
					saveOutputToFile("Case 2 The predicate "+key2+" precede "+key1,"d:\\precedence.txt");
					saveOutputToFile("{source: \""+key2+"\", target: \""+key1+"\", type:\"suit\" },\n",precedenceFileName);
				}
				if (flag.equals("Value")){
					//saveOutputToFile("Case 2 The predicate "+key2+" precede "+key1,"d:\\precedence.txt");
					saveOutputToFile("Value "+key2 +" of Predicate "+predicate2+ "Precede Value "+key1+" of Predicate "+predicate1,"d:\\valuePrecedence.txt");
					saveOutputToFile("{source: \""+key2+"\", target: \""+key1+"\", type:\"suit\" },\n",precedenceFileName);					
				}
				precedenceCounter++;
			}
			else{
				System.out.println("Case 2 No precedency between "+key1+" and "+key2);

			}
		}
		else if(scope1.size()==scope2.size()){
			System.out.println("Case 3 equal scopes "+key1+" and "+key2);
			saveOutputToFile("Value "+key1 +" of Predicate "+predicate1+ "has same scope with value "+key2+" of Predicate "+predicate2,"d:\\equalValueScope.txt");

		}
	}


	@SuppressWarnings("unchecked")
	public void getPredicateValueScope() throws ClassNotFoundException, IOException{

		LinkedHashSet<String> predicateList=this.readPredicate();

		for (String predicate : predicateList){
			System.out.println("begin with predicate "+predicate);
			predicateValueHashSet=new LinkedHashSet<HashMap<String,HashSet<String>>>();

			/*if (predicate.equals("http://purl.org/dc/terms/subject")){
				continue;
			}*/
			System.out.println("List all object for that predicate "+predicate);
			NodeIterator objectList=this.getModel().listObjectsOfProperty(this.getModel().getProperty(predicate));

			LinkedHashSet<RDFNode> objectListHashSet=new LinkedHashSet<RDFNode>(objectList.toSet());
			objectList=null;

			for (RDFNode object : objectListHashSet){
				HashMap<String, HashSet<String>> predicateValueHashMap=new HashMap<String,HashSet<String>>();
				ResIterator r=this.getModel().listSubjectsWithProperty(this.getModel().getProperty(predicate), object); 
				//System.out.println("2");
				HashSet<Resource> hashSetSubjects=new HashSet<Resource>(r.toSet());
				r=null;
				predicateValueHashMap.put(object.toString(),this.resourceToString(hashSetSubjects));
				predicateValueHashSet.add(predicateValueHashMap);
			}
			writePredicateValueScope(this.getPredicateValuesScopeHashSetDirectory()+"\\"+this.generatePredicateFileName(predicate.toString()));
			//writePredicateValueScope(this.generatePredicateFileName("http://dbpedia.org/ontology/country"));
		}
	}

	@SuppressWarnings("unchecked")
	public void writePredicateValueScope(String predicateValueScopeFileName) throws FileNotFoundException, IOException{
		System.out.println("Wtriting predicateValueScopeHashSet "+predicateValueScopeFileName);
		if (new File(predicateValueScopeFileName).exists()){
			System.out.println("predicateValueScopeHashSetFileName "+predicateValueScopeFileName+" is already exists");
		}
		else{
			//this.getPredicateScope();
			System.out.println("Creating the predicate value scope file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(predicateValueScopeFileName));
			System.out.println("write getPredicate scope to file");
			oos.writeObject(this.predicateValueHashSet);
			oos.reset();
			oos.close();
		}
		/*public void writePredicateScope(String predicateScopeHashSetFileName) throws ClassNotFoundException, IOException{

			System.out.println("Wtriting predicateScopeHashSet in the fileName "+predicateScopeHashSetFileName);
			if (new File(predicateScopeHashSetFileName).exists()){
				System.out.println("predicateScopeHashSetFileName File is already exists");
			}
			else{
				//this.getPredicateScope();
				System.out.println("Creating the predicate scope file and writing the hashSet on it");
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(predicateScopeHashSetFileName));
				System.out.println("write getPredicate scope to file");
				oos.writeObject(this.predicateScopeHashMap);
				oos.reset();
				oos.close();
			}
		}*/

	}

	@SuppressWarnings("unchecked")
	public LinkedHashSet<HashMap<String, HashSet<String>>> readPredicateValueScope() throws ClassNotFoundException, IOException{

		LinkedHashSet<HashMap<String,HashSet<String>>> readPredicateValueScopeHashSet=null;
		File[] filesPathArray=new File[this.getNumberOfPredicates()];
		filesPathArray=this.getPredicateValueScopeFile().listFiles();
		int counter=0;
		System.out.println("The size of the file array is "+filesPathArray.length);
		for (File predicateScopValueFile: filesPathArray)
		{
			//readPredicateValueScopeHashSet=new LinkedHashSet<HashMap<String,LinkedHashSet<String>>>();
			System.out.println("---------------- Outer Loop begin ----------------"+counter);
			//System.out.println("Reading the predicateScopValueFile "+predicateScopValueFile.getPath());
			//System.out.println("predicateScopValueFile name is "+predicateScopValueFile.getName());
			String predicateName=this.getPredicateNameFromFileName(predicateScopValueFile.getName());
			System.out.println("Predicate name after fixing is "+predicateName);

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(predicateScopValueFile.getPath()));
			readPredicateValueScopeHashSet=(LinkedHashSet<HashMap<String,HashSet<String>>>) ois.readObject();

			counter++;
			for(int i=counter; i<filesPathArray.length;i++){

				String predicateName1=this.getPredicateNameFromFileName(filesPathArray[i].getName());
				LinkedHashSet<HashMap<String,HashSet<String>>> readPredicateValueScopeHashSet1=new LinkedHashSet<HashMap<String,HashSet<String>>>();
				System.out.println("The Value of the outer counter is "+counter+" and the value of the inner counter is "+i);
				System.out.println("Inner loop file name is "+filesPathArray[i].getPath());
				ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(filesPathArray[i].getPath()));
				readPredicateValueScopeHashSet1=(LinkedHashSet<HashMap<String,HashSet<String>>>)ois1.readObject();

				this.intersectPredicateValueScopes(predicateName,readPredicateValueScopeHashSet, predicateName1,readPredicateValueScopeHashSet1);
			}
			System.out.println("---------------- Outer Loop finish ----------------"+counter);
		}
		System.out.println("Number for precedence counter "+this.valuePrecedenceCount);

		return readPredicateValueScopeHashSet;
	}

	@SuppressWarnings("unchecked")
	public void intersectPredicateValueScopes(String predicate,LinkedHashSet<HashMap<String,HashSet<String>>> hashSet, 
			String predicate1, LinkedHashSet<HashMap<String,HashSet<String>>> hashSet1) throws IOException{

		for (HashMap<String,HashSet<String>> predicateValueHashMap : hashSet){
			Set<String> value=predicateValueHashMap.keySet();
			String key=(String)value.iterator().next();

			System.out.println("Checking the intersection between predicate "+predicate+" with value "+key);
			//System.out.println("size of the hashmap key is "+value.size());
			for (HashMap<String,HashSet<String>> predicateValueHashMap1 : hashSet1){
				Set value1=predicateValueHashMap1.keySet();
				String key1=(String)value1.iterator().next();
				System.out.println("Checking the intersection between predicate "+predicate+" with value "+key+ " with the predicate "+predicate1+" and value "+key1);
				//System.out.println("Scope of first value "+predicateValueHashMap.get(value.iterator().next()).toString()+" \n Scope of second value "+predicateValueHashMap1.get(value1.iterator().next().toString()));
				this.intersectPredicateScopes(key, predicateValueHashMap.get(key), key1, predicateValueHashMap1.get(key1)
						,"d:\\valuePrecedence.json",this.valuePrecedenceCount,"Value",predicate,predicate1);
			}
		}
	}
	public void saveOutputToFile(String data,String fileName) throws IOException{
		PrintWriter fstream = new PrintWriter(new FileWriter(fileName,true),true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data);
		out.close();
	}

	public void testPredicatesValues() throws NoSuchElementException, IOException{
		HashSet<Resource> resourceValueHashSet=new HashSet<Resource>();
		HashSet<Literal> literalValueHashSet=new HashSet<Literal>();
		StmtIterator r=model.listStatements();
		int resourceCounter=0;
		int literalCounter=0;
		while (r.hasNext()){

			RDFNode n=r.nextStatement().getObject();
			saveOutputToFile("attribute is "+r.nextStatement().getPredicate()+"the value is "+n.toString()+"\n","d:\\values.txt");
			if (n.isLiteral()){
				literalValueHashSet.add((Literal) n);
				//literalCounter++;
			}
			else if (n.isResource()){
				resourceValueHashSet.add((Resource) n);
				//resourceCounter++;
			}
		}
		//System.out.println("Number of literal values is "+literalCounter);
		//System.out.println("Number of resource values is "+resourceCounter);
		System.out.println("Number of literal values is "+literalValueHashSet.size());
		System.out.println("Number of resource values is "+resourceValueHashSet.size());
	}

	public String generatePredicateFileName(String predicateName){
		String name=predicateName.replace("/",",").replace(":",";");
		return name;
	}

	public String getPredicateNameFromFileName(String filename){
		String predicate=filename.replace(";", ":").replace(",", "/");
		return predicate;
	}

	public int getNumberOfPredicates() {
		return numberOfPredicates;
	}

	public void setNumberOfPredicates() {
		this.numberOfPredicates = this.predicatePropertyHashSet.size();
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
		return this.predicateScopFile;
	}

	public void setPredicateScopFile() {
		this.predicateScopFile = new File(this.predicateScopeHashSetDirectory);
	}

	public String getPredicateScopeHashSetDirectory() {
		return this.predicateScopeHashSetDirectory;
	}

	public void setPredicateScopeHashSetDirectory(String predicateScopeHashSetDirectory){
		this.predicateScopeHashSetDirectory=predicateScopeHashSetDirectory;
	}

	public String getPredicateListHashSetDirectory() {
		return predicateListHashSetDirectory;
	}

	public void setPredicateListHashSetDirectory(String predicateListHashSetDirectory) {
		this.predicateListHashSetDirectory = predicateListHashSetDirectory;
	}

	public String getDBDirectory() {
		return DBDirectory;
	}

	public void setDBDirectory(String dBDirectory) {
		DBDirectory = dBDirectory;
	}
	public String getPredicateValuesScopeHashSetDirectory() {
		return predicateValuesScopeHashSetDirectory;
	}

	public File getPredicateValueScopeFile() {
		return predicateValueScopeFile;
	}


	public void setPredicateValueScopeFile() {
		this.predicateValueScopeFile = new File(predicateValuesScopeHashSetDirectory);
	}

	public void setPredicateValuesScopeHashSetDirectory(String predicateValuesScopeHashSetDirectory) {
		this.predicateValuesScopeHashSetDirectory = predicateValuesScopeHashSetDirectory;
	}
	public HashSet<String> resourceToString(HashSet<Resource> resourceHashSet){
		HashSet<String> stringHashSet=new HashSet<String>();
		for (Resource p : resourceHashSet){
			stringHashSet.add(p.toString());
		}
		return stringHashSet;
	}

	/*public LinkedHashSet<String> propertyToStringLinkedHashSet(LinkedHashSet<Resource> resourceHashSet){
			LinkedHashSet<String> stringHashSet=new LinkedHashSet<String>();
			for (Resource p : resourceHashSet){
				stringHashSet.add(p.toString());
			}
			return stringHashSet;
		}*/

}