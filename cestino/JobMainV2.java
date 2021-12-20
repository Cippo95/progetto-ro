import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
* JobMain esegue molte cose, abbastanza legate, ho trovato un po' difficile 
spezzare le cose per cui rimane "monolitico".
@author Filippo Landi
*/
public class JobMainV2 {
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
		//job che mi serve nella parte random
		Job jobRandom;
		//lavori grado 0 nel turno
		List<Job> readyJobs = new ArrayList<Job>();
		//random
		Random random = new Random();
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
		System.out.println();
		System.out.println("DEFAULT: Greedy prende solo job con ratio minore.");
		System.out.println();
		//STAMPO I LAVORI E LA MATRICE
		indexes = new int[jobsNumber];
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
		//lista priorità dei job
		int [] priorities = new int[jobsNumber];
		//CALCOLO LE PRIORITÀ DEI JOB
		int jobPrio = 0;
		for (int i = 0; i < jobsNumber; i++) {
			for (int j = 0; j < jobsNumber; j++) {
				jobPrio += matrix[i][j];
			}
			priorities[i] = jobPrio;
			jobPrio = 0;
		}
		System.out.println();
		System.out.println("Grado di priorità iniziale:");
		System.out.println(Arrays.toString(priorities));
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
				return;
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
		System.out.println();
		System.out.println("Ordine di esecuzione:");
		if (fileLoad) {
			String[] indexesToNames = new String[jobsNumber];
			for (int i = 0; i < jobsNumber; i++) {
				indexesToNames[i] = jobsNames.get(indexes[i]);
			}
			System.out.println(Arrays.toString(indexesToNames));
		} else {
			System.out.println(Arrays.toString(indexes));
		}
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
		//ricerca alternativa RANDOM
		int besttw = tw;
		System.out.println();
		System.out.println("Vuoi provare scelta con randomicità? scelta greedy al 90%.");
		System.out.println("Può metterci molto tempo, in caso fare ctrl+c. (si/no)");
		Scanner sc = new Scanner(System.in);
		String a= sc.next();
		if (a.equals("si")) { 
			while ( tw >= besttw) {
				//CALCOLO LE PRIORITÀ DEI JOB
				jobPrio = 0;
				for (int i = 0; i < jobsNumber; i++) {
					for (int j = 0; j < jobsNumber; j++) {
						jobPrio += matrix[i][j];
					}
					priorities[i] = jobPrio;
					jobPrio = 0;
				}
				//aggiungo nuovi job grado 0 e parte algoritmo principale
				for (int k = 0; k < jobsNumber; k++) {
					if (priorities[k] == 0
						&& !readyJobs.contains(jobs.get(k))) {
						readyJobs.add(jobs.get(k));
					}
				}
				for (int i = 0; i < jobsNumber; i++) {
					//job random, lo tolgo dai ready, prio -1 lo esclude
					if (random.nextFloat() > 0.9) {
						jobRandom = readyJobs.get(random.nextInt(readyJobs.size()));
					} else { 
						jobRandom = Collections.min(readyJobs);
					}	
					indexes[i] = jobRandom.getIndex();
					readyJobs.remove(jobRandom);
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

				}
				wait = 0;
				tw = 0;
				//waitingTime = new int[jobsNumber];
				for (int i = 0; i < jobsNumber; i++) {
					wait += jobs.get(indexes[i]).getSpan();
					//waitingTime[i] = wait;
					tw += wait * jobs.get(indexes[i]).getValue();
				}
			}
			System.out.println();
			System.out.println("Ordine di esecuzione:");
			if (fileLoad) {
				String[] indexesToNames = new String[jobsNumber];
				for (int i = 0; i < jobsNumber; i++) {
					indexesToNames[i] = jobsNames.get(indexes[i]);
				}
				System.out.println(Arrays.toString(indexesToNames));
			} else {
				System.out.println(Arrays.toString(indexes));
			}
			System.out.println();
			System.out.println("Tempi di attesa per job");
			wait = 0;
			waitingTime = new int[jobsNumber];
			for (int i = 0; i < jobsNumber; i++) {
				wait += jobs.get(indexes[i]).getSpan();
				waitingTime[i] = wait;
			}
			System.out.println(Arrays.toString(waitingTime));
			System.out.println("Somma pesata tempi completamento jobs: " + tw);
		}
	}
}

