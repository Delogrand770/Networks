package ex3;



/**
 * This class is a sample class used to demonstrate object serialization
 * @author CS467
 *
 */
public class Novel 
{
	// Private Instance Variables
	private String title;
	private String author;
	private int    yearPublished;
	
	/**
	 * Constructor
	 * @param name - the Name of this object
	 */
	public Novel(String title, String author, int yearPublished)
	{
		this.title 		   = title;
		this.author 	   = author;
		this.yearPublished = yearPublished;
	}
	
	public String toString()
	{
		return String.format("This is the book '%s' by %s,  published in %d", title, author, yearPublished);
	}
}
