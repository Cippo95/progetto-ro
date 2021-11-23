import java.io.File;
import java.util.Scanner;

public class provaInput {
	public static void main(String args[]) throws Exception {
		File file = new File("./prova.txt");
		Scanner sc = new Scanner(file);
		sc.useDelimiter(",");
		System.out.println("trovato " + sc.nextLine());
	}
}
//ok posso usare questo dando un nome,durata,valore,{precedenze}
