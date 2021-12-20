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
		//path del file
		String filePath = "";
		//lista dei lavori da file, serve per ricavare l'indice
		List<String> jobsNames = new ArrayList<String>();
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
			Random random = new Random();
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
		System.out.println();
		System.out.println("Soluzione Greedy");
		if (fileLoad) {
			Print(jobs, indexes, fileLoad, jobsNames);
		} else {
			Print(jobs, indexes);
		}
		//soluzione genetica
		System.out.println();
		System.out.println("Vuoi provare l'algoritmo genetico? Può metterci molto.");
		System.out.println("In caso fare ctrl+c: rispondi 'si' per continuare.");
		Scanner sc = new Scanner(System.in);
		String a = sc.next();
		if (a.equals("si")) { 
			indexes = GeneticAlg(jobs, matrix, false);
			System.out.println();
			System.out.println("Soluzione Genetica");
			if (fileLoad) {
				Print(jobs, indexes, fileLoad, jobsNames);
			} else {
				Print(jobs, indexes);
			}
		}
	}
	/*
	Algoritmo genetico: genera una popolazione di cui un elemento è la 
	soluzione greedy, il resto sono soluzioni random ammissibili
	*/
	public static int[] GeneticAlg(List<Job> jobs, int[][] matrix, boolean debug) {
		List<int[]> population = new ArrayList<int[]>();
		int[] indexes = Greedy(jobs, matrix, debug);
		int[] best = indexes.clone();
		int equalBest = 0;
		population.add(indexes);
		//Print(jobs, indexes);
		int jobsNumber = jobs.size();
		//lista priorità
		int bestFit = Fitness(jobs, indexes);
		int oldBestFit = Fitness(jobs, indexes);
		int[] priorities = Priorities(jobsNumber, matrix);
		Random random = new Random();
		//popolo di 200, 199 buone enumerazioni, come paper
		for (int i = 0; i < 200-1; i++) {
			indexes = GoodEnum(jobs, matrix, debug);
			population.add(indexes);
			//System.out.println(Arrays.toString(indexes));
		}
		//100*n generazioni come paper
		for(int gen = 0; gen < 100*jobsNumber; gen++) {
			int[] child = new int[jobsNumber];
			//System.out.println(population.size());
			int first = random.nextInt(population.size());
			//System.out.println("primo genitore" + Arrays.toString(population.get(first)));
			int second = random.nextInt(population.size());
			//System.out.println("secondo genitore" + Arrays.toString(population.get(second)));
			//punto di crossover, da almeno il primo o fino a penultimo job se no non lo farebbe
			int point = random.nextInt(jobsNumber-1)+1;
			//System.out.println(point);
			//job da evitare, serve per ordine relativo
			List<Integer> avoidedJobs = new ArrayList<Integer>();
			for(int i = 0; i < point; i++) {
				child[i] = population.get(first)[i];
				avoidedJobs.add(child[i]);
			}
			//System.out.println("evita" + avoidedJobs);
			for(int k = 0; k < jobsNumber; k++) {
				int candidate = population.get(second)[k];
				//System.out.println("candidato " + candidate);
				if(!avoidedJobs.contains(candidate)) {
					child[point] = candidate;
					//System.out.println(Arrays.toString(child));
					point++;
				}
			}
			//se ammissibile
			if(Admissibility(child, matrix)){
				//calcolo fitness
				int childFit = Fitness(jobs, child);
				//System.out.println("childFit pre ls: "+ childFit);
				//ricerca locale sul figlio per miglioramento
				for (int s = 0; s < jobsNumber; s++){
					child = localSearch(jobs, child, matrix);
				}
				childFit = Fitness(jobs, child);
				//System.out.println("childFit post ls: " + childFit);
				//se migliore di tutti
				if(childFit < bestFit) {
					bestFit = childFit;
					oldBestFit = childFit;
					best = child.clone();
					equalBest = 0;
				}
				int worstFit = Fitness(jobs, population.get(0));
				int worst = 0;
				for (int i = 0; i < population.size(); i++) {
					if(Fitness(jobs, population.get(i)) > worstFit) {
						worst = i;
					}
				}
				if (childFit < worstFit) {
					population.remove(worst);
					population.add(child);
				}
			}
			if(bestFit == oldBestFit) { 
				equalBest++;
			}
			if(equalBest == 10*jobsNumber) {
				System.out.println();
				System.out.println("Early stop: >10*n senza miglioramento");
				return best;
			}
		}		
		return best;	
	}
	/*
	* Algoritmo di ricerca locale: effettua swap in maniera esaustiva, se 
	* trova miglioramento ammissibile sostituisce la soluzione.
	*/
	public static int[] localSearch(List<Job> jobs, int[] indexes, int[][] matrix) {
		//indexesCopy mi serve per fare gli swap
		int[] indexesCopy = indexes.clone();
		//best inizialmente è l'ordine di partenza
		int[] best = indexes.clone();
		//fitness del ordine di partenza
		int fitness = Fitness(jobs, indexes);
		for (int i = 0; i < indexes.length; i++) {
			for (int j = 0; j < indexes.length; j++) {
				//reinizializzo indexesCopy uguale a start
				indexesCopy = indexes.clone();
				//swap tra due elementi
				indexesCopy[i] = indexes[j];
				indexesCopy[j] = indexes[i];
				//se ammissibile, controllo fitness
				if (Admissibility(indexesCopy, matrix)) { 
					if (Fitness(jobs, indexesCopy) < fitness) {
						best = indexesCopy.clone();
					}
				}
			}
		}
		return best;
	}
	/*
	* Algoritmo di calcolo delle priorità
	*/
	public static int[] Priorities(int jobsNumber, int[][] matrix) {
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
	/*
	* Algoritmo di controllo ammissibilità, controlla la buona enum
	*/
	public static boolean Admissibility(int[] indexes, int[][] matrix) {
		int jobsNumber = indexes.length;
		int[] priorities = Priorities(jobsNumber, matrix);
		for (int i = 0; i < jobsNumber; i++) {
			//per ogni turno controllo che sia job grado 0
			if (priorities[indexes[i]] == 0) {
				for (int j = 0; j < jobsNumber; j++) {
					//diminuisco grado a chi aspettava
					if(matrix[j][indexes[i]] == 1) {
						priorities[j]--;
					}
				}
			} else {
				//non ho buona enum
				return false;
			}
		}
		//ho buona enum
		return true;
	}
	/*
	* Algoritmo di calcolo fitness
	*/
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
	/*
	* Algoritmo di stampa ordine, tempi di attesa etc.
	*/
	public static void Print(List<Job> jobs, int[] indexes) { 
		int jobsNumber = indexes.length;
		System.out.println("Ordine di esecuzione:");
		System.out.println(Arrays.toString(indexes));
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
		System.out.println("Funzione obiettivo: " + tw);
	}
		/*
	* Algoritmo di stampa ordine, tempi di attesa etc.
	*/
	public static void Print(List<Job> jobs, int[] indexes, boolean fileLoad, List<String> jobsNames) { 
		int jobsNumber = indexes.length;
		System.out.println("Ordine di esecuzione:");
		String[] indexesToNames = new String[jobsNumber];
		for (int i = 0; i < jobsNumber; i++) {
		indexesToNames[i] = jobsNames.get(indexes[i]);
		}
		System.out.println(Arrays.toString(indexesToNames));
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
		System.out.println("Somma pesata: " + tw);
	}
	/*
	* Algoritmo greedy
	*/
	public static int[] Greedy(List<Job> jobs, int[][] matrix, boolean debug) {
		//variabili
		int jobsNumber = jobs.size();
		List<Job> readyJobs = new ArrayList<Job>();
		int[] priorities = Priorities(jobsNumber, matrix);
		int jobPrio = 0;
		int[] indexes = new int[jobsNumber];
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
	/*
	* Algoritmo che genera soluzioni ammissibili random
	*/
	public static int[] GoodEnum(List<Job> jobs, int[][] matrix, boolean debug) {
		Random random = new Random();
		int jobsNumber = jobs.size();
		List<Job> readyJobs = new ArrayList<Job>();
		int[] priorities = Priorities(jobsNumber, matrix);
		int jobPrio = 0;
		int[] indexes = new int[jobsNumber];
		Job jobItem;
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
}

