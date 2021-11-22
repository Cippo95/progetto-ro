public class JobIndexed extends Job
{
	protected int index;
	public JobIndexed(int span, int value, int index)
	{
		super(span, value);
		this.index = index;
	}
	public int getIndex()
	{
		return this.index;
	}
	public String toString() {return "Job:"+getIndex()+"; Span:"+getSpan()+"; Value:"+getValue()+"; Ratio:"+getRatio();}
}
