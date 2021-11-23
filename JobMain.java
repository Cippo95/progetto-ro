import java.util.*;
import java.io.File;
public class JobMain {
	public static void main(String[] args) {	
		int size = 0;
		boolean debug = false;
		File file;
		Random random;
		JobIndexed jobItem;
		List<JobIndexed> jobs = new ArrayList<JobIndexed>();
		//boolean random = false;
		/*
		 * Argomenti riga di comando:
		 * 1) Creazione dei job: -j INT,
		 * 2) Attivare più stampe per debug: -debug
		 */
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.equals("-j") && !argument.equals("-f")) {
				size = Integer.parseInt(args[i+1]);
				i++;
				random = new Random();
				for (int j = 0; j < size; j++) {
					jobItem = new JobIndexed(
					random.nextInt(100),
					random.nextInt(100),
					j);
					jobs.add(jobItem);
				}
			//} else if (argument.equals("-f") && !argument.equals("-j")){
			//	file = new File(args[i+1]);
			//	i++;
			} else if (argument.equals("-debug")) {
				debug = true;
				System.out.println("Stampe di debug attive!");
			} else {
				System.err.println("Errore:"
				+"uso corretto: -j INT {,-debug}");
				return;
			}
		}
		if (size == 0) { 
			System.out.println("Nessun lavoro, esco.");
			return;
		}
		//JobIndexed[] job = new JobIndexed[size];

		int[] indexes = new int[size];
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < size; i++) {
			System.out.println(jobs.get(i));
		}
		RandomDagGenerator generator = new RandomDagGenerator(size);
		int [][] array = generator.getRandomDag();
		System.out.println();
		System.out.println("Matrice delle precedenze");
		//la voglio stampare per righe
		for (int i = 0; i < size; i++) {
			System.out.println(Arrays.toString(array[i]));
		}
		//System.out.println(Arrays.deepToString(array));
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
		List<JobIndexed> ready = new ArrayList<JobIndexed>();
		//creazione insieme iniziale
		//for (int i = 0; i < size; i++) {
		//	if (priorities[i] == 0) {
		//		ready.add(job[i]);
		//	}
		//}
		for (int i = 0; i < size; i++) {
			//aggiungere i nuovi job grado 0
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
				System.out.println("Errore di precedenze: "
				+ "non ci sono job candidati (deadlock)");
				return;
			}
			//riordino insieme
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
		System.out.println(Arrays.toString(indexes));
	}
}
