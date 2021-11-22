import java.util.*;
public class JobMain{
	public static void main(String[] args){
		int size = Integer.parseInt(args[0]);
		JobIndexed[] job = new JobIndexed[size];
		Random random = new Random();
		int[] indexes = new int[size];
		for(int i = 0; i < size; i++){
			job[i] = new JobIndexed(random.nextInt(10),random.nextInt(10),i);
			System.out.println(job[i]);
		}
		//controllare matrice e sommare "uni" per avere il rank
		//ciclo di durata numero dei job, se job[i] rank 0 allora faccio add a una lista
		//faccio sort per il nostro parametro, scelgo il primo, poi devo valutare di nuovo
		//la matrice per andare a sottrare il rank a chi stava aspettando chi è uscito
		//e poi aggiornata la lista rifaccio il sort e tolgo di nuovo il primo e così via. 
		//fine.
		RandomDagGenerator generator = new RandomDagGenerator(size);
		int [][] array = generator.getRandomDag();
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
			System.out.println(ready);
			//copio indice primo lavoro
			indexes[i] = ready.get(0).getIndex();
			//diminuire il rank a chi aspettava chi è uscito
			for(int j = 0; j < size; j++){
				if( array[j][indexes[i]]==1 ){
					priorities[j]--;
				}
			}
			priorities[indexes[i]]--;
			System.out.println(Arrays.toString(priorities));
			//aggiungere i nuovi job grado 0
			for(int k = 0 ; k < size ; k++){
				if( priorities[k]==0 && !ready.contains(job[k])){
					ready.add(job[k]);
				}
			}	
			//elimino il lavoro dalla lista
			ready.remove(0);
			System.out.println(ready);
		}
		System.out.println(Arrays.toString(indexes));
	}
}
