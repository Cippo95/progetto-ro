import java.util.*;
public class JobMain {
	public static void main(String[] args) {	
		int size = 0;
		boolean debug = false;
		//boolean random = false;
		/*
		 * Argomenti riga di comando:
		 * 1) Creazione dei job: -j INT,
		 * 2) Attivare più stampe per debug: -debug
		 */
		for (int i = 0; i < args.length; i++) {
			String argument = args[i];
			if (argument.equals("-j")) {
				size = Integer.parseInt(args[i+1]);
				i++;
			} else if (argument.equals("-debug")) {
				debug = true;
			} else {
				System.err.println("Illegal parameter usage");
				return;
			}
		}
		if (debug) {System.out.println("Debug is activated!");}
		if (size == 0) { 
			System.out.println("Numero dei job non specificato:"+
			" usa -j INT come argomento.");
			return;
		}
		JobIndexed[] job = new JobIndexed[size];
		Random random = new Random();
		int[] indexes = new int[size];
		System.out.println("Lavori assegnati:");
		for (int i = 0; i < size; i++) {
			job[i] = new JobIndexed(random.nextInt(100),random.nextInt(100),i);
			System.out.println(job[i]);
		}
		RandomDagGenerator generator = new RandomDagGenerator(size);
		int [][] array = generator.getRandomDag();
		System.out.println();
		System.out.println("Matrice triangolare delle precedenze");
		System.out.println(Arrays.deepToString(array));
		int [] priorities = new int[size];
		int sum = 0;
		for(int i = 0; i < size ; i++){
			for(int j = 0; j < size ; j++){
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
		for(int i = 0; i < size ; i++){
			if( priorities[i]==0 ){
				ready.add(job[i]);
			}
		}
		for(int i = 0; i < size ; i++){
			//riordino insieme
			Collections.sort(ready);
			if( debug ) System.out.println(ready);
			//copio indice primo lavoro
			indexes[i] = ready.get(0).getIndex();
			//controllo che abbia grado 0, se non è così priorità != dag
			if(
			//diminuire il rank a chi aspettava chi è uscito
			for(int j = 0; j < size; j++){
				if( array[j][indexes[i]]==1 ){
					priorities[j]--;
				}
			}
			priorities[indexes[i]]--;
			if( debug ) System.out.println(Arrays.toString(priorities));
			//aggiungere i nuovi job grado 0
			for(int k = 0 ; k < size ; k++){
				if( priorities[k]==0 && !ready.contains(job[k])){
					ready.add(job[k]);
				}
			}	
			//elimino il lavoro dalla lista
			ready.remove(0);
			if( debug ) System.out.println(ready);
		}
		System.out.println();
		System.out.println("Ordine di esecuzione:");
		System.out.println(Arrays.toString(indexes));
	}
}
