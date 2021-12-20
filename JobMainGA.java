import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
* JobMain esegue molte cose, abbastanza legate, ho trovato un po' difficile 
spezzare le cose per cui rimane "monolitico".
@author Filippo Landi
*/
public class JobMainGA {
	public static void main(String[] args) throws Exception {
		//DEFINIZIONE VARIABILI
		//true se -j
		boolean randomGen = false;
		//true se -f
		boolean fileLoad = false;
		//true se -debug
		boolean debug = false;
		//numero dei job
		int jobsNumber = 0;
		//lista dei job
		List<Job> jobs = new ArrayList<Job>();
		//matrice delle priorità
		int [][] matrix = null;
		//ordine esecuzione lavori
		int[] indexes;
		//lavori grado 0 nel turno
		List<Job> readyJobs = new ArrayList<Job>();
		//path del file
		String filePath = "";
		Random random = new Random();
		//lista dei lavori da file, serve per ricavare l'indice
		List<String> jobsNames = new ArrayList<String>();
		List<int[]> population = new ArrayList<int[]>();
		//GESTIONE ARGOMENTI
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.equals("-j") && !fileLoad) {
				System.out.println("Generazione random!");
				randomGen = true;
				jobsNumber = Integer.parseInt(args[i+1]);
				i++;
			} else if (argument.equals("-f") && !randomGen) {
				System.out.println("Caricamento da file!");
				fileLoad = true;
				filePath = args[i+1];
				i++;
			} else if (argument.equals("-debug")) {
				System.out.println("Stampe di debug attive!");
				debug = true;
			} else {
				System.err.println("Errore negli argomenti: "
				+ "Usa -j NUM oppure -f PATH");
				return;
			}
		}
		//ISTANZIAZIONE RANDOM
		if (randomGen) {
			//Random random = new Random();
			//job random
			for (int j = 0; j < jobsNumber; j++) {
				Job jobItem = new Job(
				j,				//indice
				random.nextInt(99)+1, 		//durata
				random.nextInt(99)+1 		//valore
				);
				jobs.add(jobItem);
			}
			//classe generatrice matrice triangolare random
			RandomDagGenerator generator = 
				new RandomDagGenerator(jobsNumber);
			//matrice random
			matrix = generator.getRandomDag();
		}
		//ISTANZIAZIONE DA FILE
		if (fileLoad) {
			File file = new File(filePath);
			String line;
			//lista delle precedenze dei lavori (lista di liste)
			List<ArrayList<String>> precList = 
				new ArrayList<ArrayList<String>>();
			//lista delle precedenze del singolo lavoro
			ArrayList<String> jobPrec;
			Scanner sc = new Scanner(file);
			//salto la prima linea che contiene header
			line = sc.nextLine();
			while (sc.hasNextLine()) {
				jobPrec = new ArrayList<String>();
				line = sc.nextLine();
                  		String[] tokens = line.split(",");
                  		System.out.println(Arrays.toString(tokens));
				jobsNames.add(tokens[0]);
                 		//creo il job con indice, durata e valore
                  		Job jobItem = new Job(
                  			//countJobs,
                  			jobsNumber,
                  			Integer.parseInt(tokens[1]),
                  			Integer.parseInt(tokens[2])
					);
				//leggo le precedenze, se ci sono
				if(tokens.length == 4){
					String prec = tokens[3];
					String[] tok = prec.split(";");
					for(int j = 0; j < tok.length; j++) {
						jobPrec.add(tok[j]);
					}
				}
				precList.add(jobPrec);
				jobs.add(jobItem);
				jobsNumber++;
			}
			if (debug) {
				System.out.println("Lista priorità:");
				System.out.println(precList);
			}
			//matrice delle precedenze
			matrix = new int[jobsNumber][jobsNumber];
			for (int j = 0; j < jobsNumber; j++) {
				jobPrec = precList.get(j);
				for (int k = 0; k < jobsNumber; k++) {
					matrix[j][k] = 0;
				}
				for (int k = 0; k < jobPrec.size(); k++) {
					matrix[j][jobsNames.indexOf(
						jobPrec.get(k))] = 1;
				}
			}
		}
		if (jobsNumber == 0) { 
			System.err.println("Nessun lavoro!");
			return;
		}
		System.out.println();
		//CREAZIONE DEL GRAFICO .DOT
		try {
			FileWriter myWriter = new FileWriter("prioGraph.dot");
			myWriter.write("digraph prioGraph {\n");
			for(int i = 0; i < jobsNumber; i++) {
				for(int j = 0; j < jobsNumber; j++) {
					if (matrix[i][j] == 1 && fileLoad) {
						myWriter.write(
							jobsNames.get(i)
							+ " -> " 
							+ jobsNames.get(j)
							+ ";\n");
					} else if (matrix[i][j] == 1) {
						myWriter.write(i + " -> " + j 
							+ ";\n");
					}
				}
			}
			myWriter.write("}");		
			myWriter.close();
			System.out.println("Scritto file .dot");
		} catch (IOException e) {
      			System.out.println("Errore di scrittura sul file.");
      			e.printStackTrace();
		}
		
		//soluzione greedy
		indexes = Greedy(jobs, matrix, debug);
		System.out.println("Soluzione Greedy");
		System.out.println(Arrays.toString(indexes));
		System.out.println(Fitness(jobs, indexes));
		
		//soluzione genetica
		System.out.println("Vuoi provare algoritmo genetico?");
		System.out.println("Può metterci molto tempo a seconda dell'istanza, in caso fare ctrl+c. (si/no)");
		Scanner sc = new Scanner(System.in);
		String a= sc.next();
		if (a.equals("si")) { 
			indexes = GeneticAlg(jobs, matrix, debug);
			System.out.println("Soluzione Genetica");
			System.out.println(Arrays.toString(indexes));
			System.out.println(Fitness(jobs, indexes));
			if(Admissibility(indexes, matrix)) { 
				System.out.println("controllo ok"); 
			} else { 
				System.out.println("non ok");
			}
		}
	}
	
	public static int[] GeneticAlg(List<Job> jobs, int[][] matrix, boolean debug) {
		//popolazione
		List<int[]> population = new ArrayList<int[]>();
		//soluzione greedy
		int[] indexes = Greedy(jobs, matrix, debug);
		int[] best = indexes.clone();
		population.add(indexes);
		//Print(jobs, indexes);
		int jobsNumber = jobs.size();
		//lista priorità
		int bestFit = Fitness(jobs, indexes);
		int[] priorities = Priorities(jobsNumber, matrix);
		Random random = new Random();
		//popolo di 20, 19 buone enumerazioni
		for (int i = 0; i < 20-1; i++) {
			indexes = GoodEnum(jobs, matrix, debug);
			population.add(indexes);
		}
		/*
		System.out.println(Arrays.toString(indexes));
		int fitness = Fitness(jobs, indexes);
		System.out.println(fitness);
		if (Admissibility(indexes, matrix)) {
			System.out.println("Soluzione ammissibile!");
		} else {
			System.out.println("Soluzione non ammissibile!");
		}
		
		int[] indexesNew = localSearch(jobs, indexes, matrix);
		fitness = Fitness(jobs, indexesNew);
		System.out.println(fitness);
		System.out.println(Arrays.toString(indexesNew));
		if (Admissibility(indexes, matrix)) {
			System.out.println("Soluzione ammissibile!");
		} else {
			System.out.println("Soluzione non ammissibile!");
		}
		*/
		int[] child = new int[jobsNumber];
		for(int gen = 0; gen < 100*jobsNumber; gen++) {
			//System.out.println();
			//System.out.println(gen + " best "+ bestFit);
			//System.out.println("popolazione");
			//for (int i = 0; i < population.size(); i++) {
			//	System.out.println(Arrays.toString(population.get(i)));
			//}
			//primo e secondo genitore
			

			
			int first = random.nextInt(population.size());
			//System.out.println("primo genitore" + Arrays.toString(population.get(first)));
			int second = random.nextInt(population.size());
			//System.out.println("secondo genitore" + Arrays.toString(population.get(second)));
			//punto di crossover
			int point = random.nextInt(jobsNumber-1);
			//System.out.println(point);
			
			//figlio

			
			//se già presenti nella prima parte non nella seconda
			List<Integer> avoidedJobs = new ArrayList<Integer>();
			
			for(int i = 0; i < point; i++) {
				child[i] = population.get(first)[i];
				avoidedJobs.add(child[i]);
			}
			//System.out.println("evita" + avoidedJobs);
			int j = 0;
			for(int i = 0; i < jobsNumber; i++) {
				int candidate = population.get(second)[i];
				//System.out.println("candidato" + candidate);
				if(!avoidedJobs.contains(candidate)) {
					child[point] = candidate;
					point++;
				}
			}
			//se ammissibile
			if(Admissibility(child, matrix)){
				//calcolo fitness
				int childFit = Fitness(jobs, child);
				//System.out.println("fit figlio prima ls: "+ childFit);
				//ricerca locale sul figlio per miglioramento
				for (int s = 0; s < jobsNumber; s++){
					child = localSearch(jobs, child, matrix);
				}
				childFit = Fitness(jobs, child);
				//System.out.println("fit figlio dopo ls: " + childFit);
				//System.out.println("fit child " + childFit);
				//se migliore di tutti
				if(childFit < bestFit) {
					bestFit = childFit;
					best = child.clone();
				}
				int worstFit = Fitness(jobs, population.get(0));
				int worst = 0;
				for (int i = 0; i < population.size(); i++) {
					if( Fitness(jobs, population.get(i)) > worstFit ) {
						worst = i;
					}
				}
				if (childFit < worstFit) {
					population.remove(worst);
					population.add(child);
				}
			}
		}		
		return best;	
	}
	
	public static int[] localSearch(List<Job> jobs, int[] indexes, int[][] matrix ) {
		//System.out.println("ricerca locale");
		int[] indexesCopy = indexes.clone();
		int[] best = indexes.clone();
		int item;
		int fitness = Fitness(jobs, indexes);
		for (int i = 0; i < indexes.length; i++) {
			for (int j = 0; j < indexes.length; j++) {
				indexesCopy = indexes.clone();
				indexesCopy[i] = indexes[j];
				indexesCopy[j] = indexes[i];
				if (Admissibility(indexesCopy, matrix)) { 
					if ( Fitness(jobs, indexesCopy) < fitness ) {
						best = indexesCopy.clone();
					}
				}
				//System.out.println(Arrays.toString(indexesCopy));
			}
		}
		return best;
	}
	
	public static int[] Priorities(int jobsNumber, int[][] matrix) {
		//CALCOLO LE PRIORITÀ DEI JOB
		int jobPrio = 0;
		int[] priorities = new int[jobsNumber];
		for (int i = 0; i < jobsNumber; i++) {
			for (int j = 0; j < jobsNumber; j++) {
				jobPrio += matrix[i][j];
			}
			priorities[i] = jobPrio;
			jobPrio = 0;
		}
		return priorities;
	}
	
	public static boolean Admissibility(int[] indexes, int[][] matrix) {
		int jobsNumber = indexes.length;
		int[] priorities = Priorities(jobsNumber, matrix);
		for (int i = 0; i < jobsNumber; i++) {
			//per ogni turno controllo che sia stato scelto un job grado 0
			if (priorities[indexes[i]] == 0) {
				for (int j = 0; j < jobsNumber; j++) {
					//diminuisco grado a chi aspettava il job
					if(matrix[j][indexes[i]] == 1) {
						priorities[j]--;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	public static int Fitness(List<Job> jobs, int[] indexes) {
		int jobsNumber = indexes.length;
		int wait = 0;
		int weightedCompletions = 0;
		for (int i = 0; i < jobsNumber; i++) {
			wait += jobs.get(indexes[i]).getSpan();
			weightedCompletions += wait * jobs.get(indexes[i]).getValue();
		}
		return weightedCompletions;
	}
	
	public static void Print(List<Job> jobs, int[] indexes) { 
	int jobsNumber = indexes.length;
	System.out.println();
		System.out.println("Ordine di esecuzione:");
		//if (fileLoad) {
		//	String[] indexesToNames = new String[jobsNumber];
		//	for (int i = 0; i < jobsNumber; i++) {
		//		indexesToNames[i] = jobsNames.get(indexes[i]);
		//	}
		//	System.out.println(Arrays.toString(indexesToNames));
		//} else {
			System.out.println(Arrays.toString(indexes));
		//}
		System.out.println();
		System.out.println("Tempi di attesa per job");
		int wait = 0;
		int tw = 0;
		int[] waitingTime = new int[jobsNumber];
		for (int i = 0; i < jobsNumber; i++) {
			wait += jobs.get(indexes[i]).getSpan();
			waitingTime[i] = wait;
			tw += wait * jobs.get(indexes[i]).getValue();
		}
		System.out.println(Arrays.toString(waitingTime));
		System.out.println();
		System.out.println("Somma pesata tempi completamento jobs: " + tw);
	}
	
	
	//Algoritmo greedy
	public static int[] Greedy(List<Job> jobs, int[][] matrix, boolean debug) {
		//variabili
		int jobsNumber = jobs.size();
		List<Job> readyJobs = new ArrayList<Job>();
		int[] priorities = new int[jobsNumber];
		int jobPrio = 0;
		int[] indexes = new int[jobsNumber];
		/*
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < jobsNumber; i++) {
			System.out.println(jobs.get(i));
		}
		
		System.out.println();
		System.out.println("Matrice delle precedenze");
		//più leggibile stampata per righe
		for (int i = 0; i < jobsNumber; i++) {
			System.out.println(Arrays.toString(matrix[i]));
		}
		*/
		//CALCOLO LE PRIORITÀ DEI JOB

		for (int i = 0; i < jobsNumber; i++) {
			for (int j = 0; j < jobsNumber; j++) {
				jobPrio += matrix[i][j];
			}
			priorities[i] = jobPrio;
			jobPrio = 0;
		}
		/*
		System.out.println();
		System.out.println("Grado di priorità iniziale:");
		System.out.println(Arrays.toString(priorities));
		*/
		//aggiungo nuovi job grado 0 e parte algoritmo principale
		for (int k = 0; k < jobsNumber; k++) {
			if (priorities[k] == 0
				&& !readyJobs.contains(jobs.get(k))) {
				readyJobs.add(jobs.get(k));
			}
		}
		for (int i = 0; i < jobsNumber; i++) {
			if (debug) {
				System.out.println();
				System.out.println("Turno numero: " + i);
				System.out.println("Job candidati:");
				System.out.println(readyJobs);
			}
			if (readyJobs.size() == 0) {
				System.err.println("Errore di precedenze: "
				+ "non ci sono job candidati (deadlock)");
				System.exit(-1);
			}
			if (debug) {
				System.out.println("Best job: ");
				System.out.println(Collections.min(readyJobs));
			}
			//job migliore, lo tolgo dai ready, prio -1 lo esclude
			indexes[i] = Collections.min(readyJobs).getIndex();
			readyJobs.remove(Collections.min(readyJobs));
			priorities[indexes[i]]--;
			for (int j = 0; j < jobsNumber; j++) {
				//diminuisco grado a chi aspettava il job
				if(matrix[j][indexes[i]] == 1) {
					priorities[j]--;
				}
				//aggiungo job grado 0, se nuovo
				if (priorities[j] == 0
					&& !readyJobs.contains(jobs.get(j))) {
					readyJobs.add(jobs.get(j));
				}
			} 
			if (debug) {
				System.out.println("Priorità aggiornate:");
				System.out
				.println(Arrays.toString(priorities));
			}	

		}
		return indexes;
	}
	//Algoritmo buona enumerazione
	public static int[] GoodEnum(List<Job> jobs, int[][] matrix, boolean debug) {
		//variabili
		Random random = new Random();
		int jobsNumber = jobs.size();
		List<Job> readyJobs = new ArrayList<Job>();
		int[] priorities = new int[jobsNumber];
		int jobPrio = 0;
		int[] indexes = new int[jobsNumber];
		Job jobItem;
		/*
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < jobsNumber; i++) {
			System.out.println(jobs.get(i));
		}
		
		System.out.println();
		System.out.println("Matrice delle precedenze");
		//più leggibile stampata per righe
		for (int i = 0; i < jobsNumber; i++) {
			System.out.println(Arrays.toString(matrix[i]));
		}
		*/
		//CALCOLO LE PRIORITÀ DEI JOB

		for (int i = 0; i < jobsNumber; i++) {
			for (int j = 0; j < jobsNumber; j++) {
				jobPrio += matrix[i][j];
			}
			priorities[i] = jobPrio;
			jobPrio = 0;
		}
		//System.out.println();
		//System.out.println("Grado di priorità iniziale:");
		//System.out.println(Arrays.toString(priorities));
		//aggiungo nuovi job grado 0 e parte algoritmo principale
		for (int k = 0; k < jobsNumber; k++) {
			if (priorities[k] == 0
				&& !readyJobs.contains(jobs.get(k))) {
				readyJobs.add(jobs.get(k));
			}
		}
		for (int i = 0; i < jobsNumber; i++) {
			if (debug) {
				System.out.println();
				System.out.println("Turno numero: " + i);
				System.out.println("Job candidati:");
				System.out.println(readyJobs);
			}
			if (readyJobs.size() == 0) {
				System.err.println("Errore di precedenze: "
				+ "non ci sono job candidati (deadlock)");
				System.exit(-1);
			}
			if (debug) {
				System.out.println("Best job: ");
				System.out.println(Collections.min(readyJobs));
			}
			//job migliore, lo tolgo dai ready, prio -1 lo esclude
			jobItem = readyJobs.get(random.nextInt(readyJobs.size()));
			indexes[i] = jobItem.getIndex();
			readyJobs.remove(jobItem);
			priorities[indexes[i]]--;
			for (int j = 0; j < jobsNumber; j++) {
				//diminuisco grado a chi aspettava il job
				if(matrix[j][indexes[i]] == 1) {
					priorities[j]--;
				}
				//aggiungo job grado 0, se nuovo
				if (priorities[j] == 0
					&& !readyJobs.contains(jobs.get(j))) {
					readyJobs.add(jobs.get(j));
				}
			} 
			if (debug) {
				System.out.println("Priorità aggiornate:");
				System.out
				.println(Arrays.toString(priorities));
			}	

		}
		return indexes;
	}
	//Genetico con local search
}

