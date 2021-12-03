public class Job implements Comparable<Job>
{
	protected Integer index;
	protected Integer span;
	protected Integer value;
	protected Double ratio;
	public Job(int index, int span, int value)
	{
		this.index = index;
		this.span = span;
		this.value = value;
		ratio = Double.valueOf(span)/Double.valueOf(value); 
	}
	public int getIndex() {
		return index;
	}
	public int getSpan() {
		return span;
	}
	public int getValue() {
		return value;
	}
	public double getRatio() {
		return ratio;
	}
	public String toString() {
		return "Job:" + getIndex() + "; Span:" + getSpan() + "; Value:" 
		+ getValue() + "; Ratio:" + getRatio();
	}
	public int compareTo(Job x) {
		Job j = (Job) x;
		int compareRatio = ratio.compareTo(j.ratio);
		return compareRatio;
	}
} 
	
