import java.util.*;
public class DagGenerator{
	public static void main(String[] args){
		boolean set_zero=false;
		int[][] matrix = new int[Integer.parseInt(args[0])][Integer.parseInt(args[0])];
		for(int i=0; i<Integer.parseInt(args[0]); i++){
			for(int j=0; j<Integer.parseInt(args[0]); j++){
				if(j==i){set_zero=true;}
				if(!set_zero){matrix[i][j]=randomizer();}
				else{matrix[i][j]=0;}
			}
			set_zero=false;
		}
		System.out.println(Arrays.deepToString(matrix));
	}
	public static int randomizer(){
		Random random = new Random();
		if(random.nextFloat()>0.5) return 1; else return 0;	
	}
}
				
