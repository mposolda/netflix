package org.jboss.portlet;

import java.util.List;

import javax.faces.context.FacesContext;

/**
 * Wrap informations about single movie.
 *  
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NetflixMovie 
{
	// used internaly for saving order of search
	private int searchIndex;
	
	// basic attributes from simple movie search
	private String id;
	private String shortTitle;
	private String title;
	private String pictureSmallUrl;
	private String pictureNormalUrl;
	private String pictureBigUrl;
	private String mpaaRating;
	private List<String> genres;
	private int releaseYear;
	private int runtimeInSeconds;
	private String officialUrl;
	private float averageRating;
	private String netflixUrl;
	
	// additional movie search
	private String synopsis;
	private List<NetflixPerson> directors;
	private List<NetflixPerson> cast;
	private List<NetflixAward> awards;	
	
	public NetflixMovie(String id, String shortTitle, String title, String pictureSmallUrl, String pictureNormalUrl, String pictureBigUrl, int releaseYear, String mpaaRating, List<String> genres, int runtimeInSeconds, float averageRating, String netflixUrl, String officialUrl)
	{
		this.id = id;
		this.shortTitle = shortTitle;
		this.title = title;
		this.pictureSmallUrl = pictureSmallUrl;
		this.pictureNormalUrl = pictureNormalUrl;
		this.pictureBigUrl = pictureBigUrl;
		this.releaseYear = releaseYear;
		this.mpaaRating = mpaaRating;
		this.genres = genres;
		this.runtimeInSeconds = runtimeInSeconds;
		this.averageRating = averageRating;
		this.netflixUrl = netflixUrl;
		this.officialUrl = officialUrl;
	}

	public int getSearchIndex() 
	{
		return searchIndex;
	}

	public void setSearchIndex(int searchIndex) 
	{
		this.searchIndex = searchIndex;
	}

	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}

	public String getShortTitle() 
	{
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) 
	{
		this.shortTitle = shortTitle;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getPictureSmallUrl() 
	{
		return pictureSmallUrl;
	}

	public void setPictureSmallUrl(String pictureSmallUrl) 
	{
		this.pictureSmallUrl = pictureSmallUrl;
	}

	public String getPictureNormalUrl() 
	{
		return pictureNormalUrl;
	}

	public void setPictureNormalUrl(String pictureNormalUrl) 
	{
		this.pictureNormalUrl = pictureNormalUrl;
	}

	public String getPictureBigUrl() 
	{
		return pictureBigUrl;
	}

	public void setPictureBigUrl(String pictureBigUrl) 
	{
		this.pictureBigUrl = pictureBigUrl;
	}

	public String getMpaaRating() 
	{
		return mpaaRating;
	}

	public void setMpaaRating(String mpaaRating) 
	{
		this.mpaaRating = mpaaRating;
	}

	public List<String> getGenres() 
	{
		return genres;
	}

	public void setGenres(List<String> genres) 
	{
		this.genres = genres;
	}

	public int getReleaseYear() 
	{
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) 
	{
		this.releaseYear = releaseYear;
	}

	public int getRuntimeInSeconds() 
	{
		return runtimeInSeconds;
	}

	public void setRuntimeInSeconds(int runtimeInSeconds) 
	{
		this.runtimeInSeconds = runtimeInSeconds;
	}

	public String getOfficialUrl() 
	{
		return officialUrl;
	}

	public void setOfficialUrl(String officialUrl) 
	{
		this.officialUrl = officialUrl;
	}

	public float getAverageRating() 
	{
		return averageRating;
	}

	public void setAverageRating(float averageRating) 
	{
		this.averageRating = averageRating;
	}

	public String getNetflixUrl() 
	{
		return netflixUrl;
	}

	public void setNetflixUrl(String netflixUrl) 
	{
		this.netflixUrl = netflixUrl;
	}

	public String getSynopsis() 
	{
		return synopsis;
	}

	public void setSynopsis(String synopsis) 
	{
		this.synopsis = synopsis;
	}

	public List<NetflixPerson> getDirectors() 
	{
		return directors;
	}

	public void setDirectors(List<NetflixPerson> directors) 
	{
		this.directors = directors;
	}

	public List<NetflixPerson> getCast() 
	{
		return cast;
	}

	public void setCast(List<NetflixPerson> cast) 
	{
		this.cast = cast;
	}

	public List<NetflixAward> getAwards() 
	{
		return awards;
	}

	public void setAwards(List<NetflixAward> awards) 
	{
		this.awards = awards;
	}
	
	public String toString()
	{
		return new StringBuilder("NetflixMovie \n[ ")
		.append("\n id=").append(id)
		.append("\n shortTile=").append(shortTitle)
		.append("\n title=").append(title)
		.append("\n pictureSmallUrl=").append(pictureSmallUrl)
		.append("\n pictureNormalUrl=").append(pictureNormalUrl)
		.append("\n pictureBigUrl=").append(pictureBigUrl)
		.append("\n mpaaRating=").append(mpaaRating)
		.append("\n genres=").append(genres)
		.append("\n releaseYear=").append(releaseYear)
		.append("\n runtimeInSeconds=").append(runtimeInSeconds)
		.append("\n averageRating=").append(averageRating)
		.append("\n netflixUrl=").append(netflixUrl)
		.append("\n officialUrl=").append(officialUrl)
		.append("\n synopsis=").append(synopsis)
		.append("\n directors=").append(directors)
		.append("\n cast=").append(cast)
		.append("\n awards=").append(awards)
		.append("\n]")
		.toString();
	}
	
	/* helper jsf methods */
	
	public String getGenresAsString()
	{
		StringBuilder result = new StringBuilder(128);
		int i=0;
		for (String genre : genres)
		{
			i++;
			if (i != 1) 
			{
				result.append(", ");
			}
			result.append(genre);			
		}
		return result.toString();
	}
	
	public String getRuntimeInMinutesAsString()
	{
		return runtimeInSeconds / 60 + " minutes";
	}
	
	public String getAwardsAsString()
	{
		if (awards == null)
		{
			return "";
		}
		
		StringBuilder builder = new StringBuilder("<b>Awards: </b>");	
		int i = 0;
		for (NetflixAward award : awards)
		{
			i++;
			if (i != 1) 
			{
				builder.append(", ");
			}
			String awardPrefix = award.isWinner() ? "Winner of " : "";
			builder.append(awardPrefix);
			builder.append(award.getName());
			if (award.getPerson() != null)
			{
				builder.append(" - " + award.getPerson().getName());
			}			
		}
		return builder.append("<br />").toString();
	}
	
	public String getOfficialUrlAsString()
	{
		if (officialUrl == null)
		{
			return "";
		}		
		return "<b>Official URL: </b><a href=\"" + officialUrl + "\">" + officialUrl + "</a><br />"; 
	}
	
	/**
	 * @return html String which identifies asterisk pictures according to average rating.
	 */
	public String getAverageRatingAsPicture()
	{
		int ratingInt = (int)(averageRating * 10);
		StringBuilder result = new StringBuilder(128);
		while (ratingInt > 0)
		{
			if (ratingInt >= 10)
			{
				result.append("<img height=\"20px\" src=\"" + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/images/star10.gif" +   "\" />");
			}
			else
			{
				result.append("<img height=\"20px\" src=\"" + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/images/star" + ratingInt + ".gif" +   "\" />");
			}
			ratingInt -= 10;
		}
		return result.toString();
	}
	
}
