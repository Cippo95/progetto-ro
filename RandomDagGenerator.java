import java.util.*;
public class RandomDagGenerator{
	private boolean set_zero=false;
	private int size;
	public RandomDagGenerator(int size){ 
		this.size = size; 
	}
	private int randomizer(){
		Random random = new Random();
		if(random.nextFloat()>0.5) return 1; else return 0;
	}
	public int[][] getRandomDag(){ 
		int[][] matrix = new int[size][size];
		for(int i=0; i<size; i++){
			for(int j=0; j<size; j++){
				if(j==i){
					set_zero=true;
				}
				if(!set_zero){
					matrix[i][j]=randomizer();
				}
				else{
					matrix[i][j]=0;
				}
			}
			set_zero=false;
		}
		return matrix;
	}
}
				
