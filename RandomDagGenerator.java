import java.util.*;
public class RandomDagGenerator {
	private int size;
	public RandomDagGenerator(int size) { 
		this.size = size; 
	}
	private int randomizer() {
		Random random = new Random();
		if(random.nextFloat() > 0.5) {
			return 1;
		} else {
			return 0;
		}
	}
	public int[][] getRandomDag() { 
		int[][] matrix = new int[size][size];
		//ciclo su tutta la matrice con due loop (riga i e colonna j)
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++){
				//da diagonale in poi solo zeri
				if(j >= i){
					matrix[i][j] = 0;
				} else {
					//parte triangolare sotto diagonale
					matrix[i][j] = randomizer();
				}
			}
		}
		return matrix;
	}
}
				
