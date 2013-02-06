package org.jboss.portlet;

/**
 * Wrap informations about single award and all informations about award.
 *  
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NetflixAward 
{	
	private boolean winner;
	private int year;
	private String name;
	private NetflixPerson person;
	
	public NetflixAward(boolean winner, int year, String name, NetflixPerson person)
	{
		this.winner = winner;
		this.year = year;
		this.name = name;
		this.person = person;
	}

	public boolean isWinner() 
	{
		return winner;
	}

	public void setWinner(boolean winner) 
	{
		this.winner = winner;
	}

	public int getYear() 
	{
		return year;
	}

	public void setYear(int year) 
	{
		this.year = year;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public NetflixPerson getPerson() 
	{
		return person;
	}

	public void setPerson(NetflixPerson person) 
	{
		this.person = person;
	}
	
	public String toString()
	{
		return new StringBuilder("NetflixAward \n[ ")
			.append("\n winner=").append(winner)
			.append("\n name=").append(name)
			.append("\n year=").append(year)
			.append("\n person=").append(person)			
			.append("\n]")
			.toString();
	}

}
