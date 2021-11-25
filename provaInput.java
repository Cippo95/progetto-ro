import java.io.File;
import java.util.Scanner;

public class provaInput {
	public static void main(String args[]) throws Exception {
		//creo il file
		File file = new File("./prova.txt");
		//lo apro con lo scanner
		Scanner sc = new Scanner(file);
		//fino alla fine
		while (sc.hasNextLine()) {
			//prendo la linea
			line = sc.nextLine();
			//la divido in token
	     		String[] tokens = line.split(" ");
			//il secondo e il terzo sono durata e valore
			
    			JobIndexed item = new JobIndexed(tokens[1], tokens[2], i);
	}
}
//ok posso usare questo dando un nome,durata,valore,{precedenze}

//prendo la linea
//spezzo la linea in token inizializzo un job nome, durata e valore
//gli assegno un numero (creo corrispondenza chiave valore)
//finché non finisco la linea creo un array per fare le precedenze
//prendo tutto
//creo la matrice delle precedenze, es. primo job guardo leggo e prendo indice


//array di array per le precedenze string
//quindi ho bisogno di un hashmap per chiave valore, nome -> indice
//array di array per la matrice
