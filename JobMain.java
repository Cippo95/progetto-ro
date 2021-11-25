import java.util.*;
import java.io.File;
public class JobMain {
	public static void main(String[] args) throws Exception {
		//size contiene il numero di job
		int size = 0;
		//debug imposta stampate in più
		boolean debug = false;
		//count conta il numero di job importati da file
		int count = 0;
		//randomTest imposta il test con job e priorità random
		boolean randomTest = false;
		//fileLoad imposta la lettura da file dei job e priorità
		boolean fileLoad = false;
		//salvo in path il path del file da caricare
		String path = "";
		//lista dei job di una mia classe JobIndexed
		List<JobIndexed> jobs = new ArrayList<JobIndexed>();
		//matrice delle priorità
		int [][] array = null;
		//indexes contiene l'ordine finale di esecuzione dei lavori
		int[] indexes;
		//lista del lavori che possono essere eseguiti in un certo turno
		List<JobIndexed> ready = new ArrayList<JobIndexed>();
		//mappatura numero a lavoro, utile alla fine
		Map<Integer, String> reverseHash = 
			new Hashtable<Integer, String>();
		//gestione degli argomenti
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.equals("-j") && !fileLoad) {
				randomTest = true;
				size = Integer.parseInt(args[i+1]);
				i++;
			} else if (argument.equals("-f") && !randomTest) {
				fileLoad = true;
				path = args[i+1];
				i++;	
			} else if (argument.equals("-debug")) {
				debug = true;
				System.out.println("Stampe di debug attive!");
			} else {
				System.err.println("Errore negli argomenti: "
				+ "Usa -j <num> oppure -f <path>");
				return;
			}
		}
		//logica della costruizione random
		if (randomTest) {
			Random random = new Random();
			for (int j = 0; j < size; j++) {
				JobIndexed jobItem = new JobIndexed(
				random.nextInt(100),
				random.nextInt(100),
				j);
				jobs.add(jobItem);
			}
			//mia classe per generare matrice triangolare random
			RandomDagGenerator generator = 
				new RandomDagGenerator(size);
			array = generator.getRandomDag();
		}
		//logica della costruzione caricando da file
		if (fileLoad) {
			File file = new File(path);
			//per lettura linea
			String line;
			//mappatura lavoro a numero
			Map<String, Integer> hash = 
				new Hashtable<String, Integer>();
			//lista di liste di priorità, serve per costruire array
			List<ArrayList<String>> prioList = 
				new ArrayList<ArrayList<String>>();
			//lista di priorità del singolo lavoro
			ArrayList<String> prioWithName;
			//caricamento file
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				prioWithName = new ArrayList<String>();
				line = sc.nextLine();
                  		String[] tokens = line.split(",");
                  		//aggiungo map tra nomi job e numero job
				hash.put(tokens[0], count);
				//e viceversa
				reverseHash.put(count, tokens[0]);
                 		//creo il job con durata, valore e indice
                  		JobIndexed jobItem = new JobIndexed(
                  			Integer.parseInt(tokens[1]), 
					Integer.parseInt(tokens[2]), 
					count);
				//leggo le precedenze
				for (int j = 3; j < tokens.length; j++) {
					prioWithName.add(tokens[j]);
				}
				prioList.add(prioWithName);
				jobs.add(jobItem);
				count++;
			}
			//finito di leggere so il numero di job
			size = count;
			if (debug) {
				System.out.println(prioList);
			}
			//posso inizializzare la matrice delle precedenze
			array = new int[size][size];
			//e popolarla
			for (int j = 0; j < size; j++) {
				prioWithName = prioList.get(j);
				for (int k = 0; k < size; k++) {
					array[j][k] = 0;
				}
				for (int k = 0; k < prioWithName.size(); k++) {
					array[j][hash.get(prioWithName.get(k))] = 
						1;
				}
			}
		}
		if (size == 0) { 
			System.err.println("Nessun lavoro!");
			return;
		}
		//inizializzo la lista di esecuzione
		indexes = new int[size];
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < size; i++) {
			System.out.println(jobs.get(i));
		}
		System.out.println();
		System.out.println("Matrice delle precedenze");
		//è più leggibile stampata per righe
		for (int i = 0; i < size; i++) {
			System.out.println(Arrays.toString(array[i]));
		}
		//lista che determina priorità di esecuzione di un job
		int [] priorities = new int[size];
		int sum = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum += array[i][j];
			}
			priorities[i] = sum;
			sum = 0;
		}
		System.out.println();
		System.out.println("Grado di priorità iniziale:");
		System.out.println(Arrays.toString(priorities));
		//ciclo di durata <num> jobs perché tutti devono eseguire
		for (int i = 0; i < size; i++) {
			//aggiungo nuovi job grado 0
			for (int k = 0; k < size; k++) {
				if (priorities[k] == 0
					&& !ready.contains(jobs.get(k))) {
					ready.add(jobs.get(k));
				}
			}	
			if (debug) {
				System.out.println();
				System.out.println("Turno: " + i);
				System.out.println("Candidati "
				+ "prima dell'ordinamento:");
				System.out.println(ready);
			}
			//controllo deadlock cioè assenza di job candidati
			if (ready.size() == 0) {
				System.err.println("Errore di precedenze: "
				+ "non ci sono job candidati (deadlock)");
				return;
			}
			//riordino insieme come programmato durata/valore <
			Collections.sort(ready);
			if (debug) {
				System.out.println("Candidati ordinati:");
				System.out.println(ready);
			}
			//copio indice primo lavoro
			indexes[i] = ready.get(0).getIndex();
			//diminuire il rank a chi aspettava chi è uscito
			for (int j = 0; j < size; j++) {
				if( array[j][indexes[i]] == 1 ) {
					priorities[j]--;
				}
			} 
			priorities[indexes[i]]--;
			if (debug) {
				System.out.println("Priorità aggiornate:");
				System.out
				.println(Arrays.toString(priorities));
			}
			//elimino il lavoro dalla lista
			ready.remove(0);	
			if (debug) {
				System.out.println("Lista aggiornata:");
				System.out.println(ready);
			}
		}
		System.out.println();
		System.out.println("Ordine di esecuzione:");
		if (fileLoad){
			for (int i = 0; i < size; i++) {
				System.out.print(
					i + ":" + reverseHash.get(indexes[i]));
				System.out.println();
			}
		} else {
			System.out.println(Arrays.toString(indexes));
		}
	}
}
