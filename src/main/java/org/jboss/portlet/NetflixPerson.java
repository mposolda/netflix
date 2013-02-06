package org.jboss.portlet;

import java.util.List;

/**
 * Wrap informations about single person (movie star).
 *  
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NetflixPerson 
{
	// used internaly for saving order of search
	private int searchIndex;
	
	// these attributes are filled by simple query 
	private long id;
	private String name;
	private String biography;
	private String netflixUrl;		
	
	// this attribute can be filled from filmography query
	private List<NetflixMovie> movies;
	
	public NetflixPerson(long id, String name)
	{
		this(id, name, null, null);
	}
	
	public NetflixPerson(long id, String name, String bio, String webLink)
	{
		this.id = id;
		this.name = name;
		this.biography = bio;
		this.netflixUrl = webLink;
		this.movies = null;
	}
	
	public int getSearchIndex() 
	{
		return searchIndex;
	}

	public void setSearchIndex(int searchIndex) 
	{
		this.searchIndex = searchIndex;
	}	
	
	public long getId() 
	{
		return id;
	}
	
	public void setId(long id) 
	{
		this.id = id;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getBiography() 
	{
		return biography;
	}
	
	public void setBiography(String biography) 
	{
		this.biography = biography;
	}
	
	public String getNetflixUrl() 
	{
		return netflixUrl;
	}
	
	public void setNetflixUrl(String netflixUrl) 
	{
		this.netflixUrl = netflixUrl;
	}
	
	public List<NetflixMovie> getMovies() 
	{
		return movies;
	}
	
	public void setMovies(List<NetflixMovie> movies) 
	{
		this.movies = movies;
	}		
	
	public String toString()
	{
		return new StringBuilder("NetflixPerson \n[ ")
			.append("\n id=").append(id)
			.append("\n name=").append(name)
			.append("\n biography=").append(biography)
			.append("\n netflixUrl=").append(netflixUrl)
			.append("\n movies=").append(movies)
			.append("\n]")
			.toString();
	}
	
	/* helper jsf methods */
	
	public String getPictureUrl()
	{
		return "http://cdn-0.nflximg.com/us/headshots/" + id + ".jpg";
	}
	
}
