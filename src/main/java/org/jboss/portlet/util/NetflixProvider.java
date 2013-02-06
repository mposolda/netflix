package org.jboss.portlet.util;

import java.util.List;

import org.jboss.portlet.NetflixMovie;
import org.jboss.portlet.NetflixPerson;

/**
 * Contract for obtaining informations about movies and actors. Users are able to provide their own implementation, which can use 
 * netflix or completely different source of informations.
 * 
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface NetflixProvider 
{
	/**
	 * Search for list of people in Netflix database according to given name (or part of the name).
	 * 
	 * @param personNameToken for search. Value of this parameter can be "Tom Cruise", "Cruise", "Tom" etc. 
	 * @param maxResults maximum number of search results from netflix.
	 * @param startIndex start for search (useful if number of results is bigger than maxResults)
	 * @return list with parsed results from netflix
	 * @throws WrapperNetflixException if error occured during sending request to netflix or parsing result.
	 */
	public List<NetflixPerson> getPeoples(String personNameToken, int maxResults, int startIndex) throws WrapperNetflixException;
	
	/**
	 * Send requests to netflix and get all informations about person with given id (including filmography)
	 * 
	 * @param personId id of person to search.
	 * @return person with filled all attributes (including "movies" attribute)
	 * @throws WrapperNetflixException if error occured during sending request to netflix or parsing result.
	 */
	public NetflixPerson getPersonFromId(Long personId) throws WrapperNetflixException;
	
	/**
	 * Search for list of movies in Netflix database according to given name (or part of the name).
	 * 
	 * @param movieNameToken for search. Value of this parameter can be name of movie or part of name. 
	 * @param maxResults maximum number of search results from netflix.
	 * @param startIndex start for search (useful if number of results is bigger than maxResults)
	 * @return list with parsed results from netflix
	 * @throws WrapperNetflixException if error occured during sending request to netflix or parsing result.
	 */	
	public List<NetflixMovie> getMovies(String movieNameToken, int maxResults, int startIndex) throws WrapperNetflixException;
	
	/**
	 * Send requests to netflix and get all informations about movie with given id (including directors, casts, awards)
	 * 
	 * @param movieId id of mvoie to search (represents whole movie URL which can be different for movies and for series).
	 * @return movie with filled all attributes (including "cast", "directors" etc.)
	 * @throws WrapperNetflixException if error occured during sending request to netflix or parsing result.
	 */	
	public NetflixMovie getMovieFromId(String movieId) throws WrapperNetflixException;
	
}
