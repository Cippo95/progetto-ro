public class Job implements Comparable<Job>
{
	protected Integer span;
	protected Integer value;
	protected Double ratio;
	public Job(int span, int value)
	{
		this.span = span;
		this.value = value;
		ratio = Double.valueOf(span)/Double.valueOf(value); 
	}
	public int getSpan(){return span;}
	public int getValue(){return value;}
	public double getRatio(){return ratio;}
	public String toString() {return "Il lavoro dura "+getSpan()+" e ha valore "+getValue();}
	public int compareTo(Job x){
	Job j = (Job) x;
	int compareRatio = ratio.compareTo(j.ratio);
	return compareRatio;
	}
} 
	
