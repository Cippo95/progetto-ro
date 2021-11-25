import java.util.*;
public class Generator{
	private String path;
	public Generator(String path){
		this.path = path;
	}
	public getGenerator(){
		File file = new File(path);
		//mappa per unire lavoro a numero
		Map<String, Integer> hash = new Hashtable<String, Integer>();
		//lista di liste di priorità
		List<ArrayList<String>> prioritiesList = new ArrayList<ArrayList<String>>();
		//lista di priorità del singolo lavoro
		ArrayList<String> prioritiesWithName;
		i++;
		//caricamento file
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			prioritiesWithName = new ArrayList<String>();
			//prendo la linea
			line = sc.nextLine();
          		//la divido in token
          		String[] tokens = line.split(",");
          		//mapping tra nomi e numero job
			hash.put(tokens[0], count);
         		//creo il job con durata, valore e indice
          		jobItem = new JobIndexed(Integer.parseInt(tokens[1]), 
			Integer.parseInt(tokens[2]), count);
			//ricavo le precedenze
			for (int j = 3; j < tokens.length; j++) {
				prioritiesWithName.add(tokens[j]);
			}
			prioritiesList.add(prioritiesWithName);
			jobs.add(jobItem);
			count++;
		}
		size = count;
		System.out.println(prioritiesList);
		array = new int[size][size];
		for (int j = 0; j < size; j++) {
			prioritiesWithName = prioritiesList.get(j);
			for (int k = 0; k < size; k++) {
				array[j][k] = 0;
			}
			for (int k = 0; k < prioritiesWithName.size(); k++) {
				array[j][hash.get(prioritiesWithName.get(k))] = 1;
			}
		}

	}
}
