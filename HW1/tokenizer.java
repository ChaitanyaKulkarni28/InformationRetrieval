
/**
 * This class provides methods for tokenization 
 * It tokenizes and collects information about tokens
 * in Cranfield collection of documents
 *
 * @author  Chaitanya Kulkarni
 */
 
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class tokenizer {

	static HashMap<String, Integer> tokens = new HashMap<>();
	static HashMap<String,Integer> stemTokens = new HashMap<>();
	static int totalTokens, occuringOnceTokens, avgNumOfTokens, totalStems;
	static int totalFiles = 0;

	//Reads all files from given path
	public void parseAllFiles(String path) {
		try {
			File file = new File(path);

			for (File currentFile : file.listFiles()) {

				File readFile = new File(path + currentFile.getName());
				Scanner sc = new Scanner(readFile);

				while (sc.hasNextLine()) {
					preProcessEachLine(sc.nextLine());
				}
				sc.close();
				totalFiles++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//Processes each line individually by removing unwanted symbols
	public void preProcessEachLine(String line) {

		line = line.replaceAll("\\<.*?>", " "); // Removes SGML tags
		line = line.replaceAll("[\\d+]", " "); // Remove digits
		line = line.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{\\.]", ""); // Removes special characters
		line = line.replaceAll("\\'s", ""); // Removes Possessives (university's -> university)
		line = line.replaceAll("\\'", " ");
		line = line.replaceAll("-", " "); // Words with dashes are separated in 2 words (middle-class -> middle class)
		line = line.replaceAll("\\s+", " "); // Remove multiple white spaces

		line.toLowerCase(); // Convert complete line into lower case

		String words[] = line.split(" ");

		for (String word : words) {
			if (word == null || word.length() < 1) {
				continue;
			}
			totalTokens++;
			totalStems++;
			tokens.put(word, tokens.getOrDefault(word, 0) + 1);
			addStemmedWord(word);
		}
	}
	
	//Calls Stemmer on each word to get stem and add it to hash map
	public void addStemmedWord(String word) {
		Stemmer stemmer = new Stemmer();
		char arr[] = word.toCharArray();
		stemmer.add(arr,arr.length);
		stemmer.stem();
		stemTokens.put(stemmer.toString(), stemTokens.getOrDefault(stemmer.toString(), 0) + 1);
	}

	//Sorts hashmap by value in descending order
	public HashMap<String, Integer> get30MostFrequentTokens(HashMap<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	//Iterates over whole hashmap to see keys occuring once
	public int traverseMap(HashMap<String, Integer> map) {
		occuringOnceTokens = 0;
		for (Map.Entry<String, Integer> en : map.entrySet()) {
			if (en.getValue() == 1) {
				occuringOnceTokens++;
			}
		}
		return occuringOnceTokens;
	}

	public static void main(String[] args) {
		//String filePath = "E:\\Spring 2019\\IR\\HW\\Assignment1\\Cranfield\\";
		if(args.length <= 0){
			System.err.println("Please enter path to data as argument while running program");
			System.exit(0);
		}
		String filePath = args[0].toString();

		if(filePath.isEmpty()) {
			System.err.println("Please enter path to data as argument while running program");
			System.exit(0);
		}
		
		tokenizer obj = new tokenizer();

		long startTime = System.currentTimeMillis();
		
		obj.parseAllFiles(filePath);

		HashMap<String, Integer> top30 = obj.get30MostFrequentTokens(tokens);
		
		HashMap<String,Integer> top30stems = obj.get30MostFrequentTokens(stemTokens);

		long stopTime = System.currentTimeMillis();

		int ct = 0;

		System.out.println("------------------------Output of Problem 1------------------------------ ");
		
		System.out.println("Count of unique tokens " + tokens.size());
		System.out.println("Total tokens " + totalTokens);
		System.out.println("Number of tokens occuring once " + obj.traverseMap(tokens));
		System.out.println("Total files: " + totalFiles);
		System.out.println("Average tokens per document " + (totalTokens / totalFiles));
		
		System.out.println("-------------------30 Most Frequent Tokens--------------------------------");
		for (Map.Entry<String, Integer> en : top30.entrySet()) {
			if (ct == 30) {
				break;
			}
			System.out.println((ct + 1) + "] "+ en.getKey() + " ==> " + en.getValue());
			ct++;
		}

		
		System.out.println("------------------------Output of Problem 2------------------------------ ");
		
		System.out.println("Count of unique stem tokens "+stemTokens.size());
		System.out.println("Total stems " + totalStems);
		System.out.println("Number of stems occuring once "+obj.traverseMap(stemTokens));
		System.out.println("Total files: " + totalFiles);
		System.out.println("Average stems per document " + (totalStems / totalFiles));
		
		System.out.println("-------------------30 Most Frequent Stems--------------------------------");
		int ct1 = 0;
		for (Map.Entry<String, Integer> en : top30stems.entrySet()) {
			if (ct1 == 30) {
				break;
			}
			System.out.println((ct1 + 1) + "] "+ en.getKey() + " ==> " + en.getValue());
			ct1++;
		}

		
		
		System.out.println("Time required "+(stopTime - startTime)+" milli seconds");
	}

}
