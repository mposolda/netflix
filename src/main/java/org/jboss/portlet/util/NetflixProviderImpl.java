package org.jboss.portlet.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.portlet.NetflixAward;
import org.jboss.portlet.NetflixMovie;
import org.jboss.portlet.NetflixPerson;

import com.netflix.api.NetflixAPIResponse;
import com.netflix.api.client.APIEndpoints;
import com.netflix.api.client.NetflixAPIClient;

/**
 * Class is used to send requests to netflix api and parse results from netflix api calls.
 * 
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class NetflixProviderImpl implements NetflixProvider
{
	// constants
	public static final int DEFAULT_MAX_RESULTS = 10;
	
	// variables
	private NetflixAPIClient apiClient;
	
	/**
	 * initialize netflix api client with credentials from system properties
	 */
	public NetflixProviderImpl()
	{
		// these secrets can be obtained from netflix page after registration. See http://developer.netflix.com
		String consumerKey= System.getProperty("netflix.consumer.key");
		String consumerSecret= System.getProperty("netflix.consumer.secret");

		apiClient = new NetflixAPIClient(consumerKey, consumerSecret);

        Properties props = new Properties();
        props.put("BASE_URI", "http://api-public.netflix.com");
        props.put("LOGIN_PATH", "http://api-user.netflix.com/oauth/login");
        APIEndpoints.init(props);
    }
	
	/**
	 * {@inheritDoc}
	 */
	public List<NetflixPerson> getPeoples(String personNameToken, int maxResults, int startIndex) throws WrapperNetflixException
	{		
		try
		{
			String resultFromNetflix = sendRequestToNetflix(APIEndpoints.PEOPLE_URI, new String[][] {{"term", personNameToken}, {"max_results", String.valueOf(maxResults)}, {"start_index", String.valueOf(startIndex)}});			
			return NetflixParserUtil.getPeoplesFromResponse(resultFromNetflix);			
		}	
		catch (Exception e)
		{
			throw new WrapperNetflixException(e); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public NetflixPerson getPersonFromId(Long personId) throws WrapperNetflixException
	{
		try
		{
			String resultFromNetflix = sendRequestToNetflix(APIEndpoints.PEOPLE_URI + "/" + personId, new String[][] {});
			NetflixPerson person = NetflixParserUtil.getPersonFromXMLString(resultFromNetflix);
			
			// add filmography to our person
			List<NetflixMovie> filmography = getFilmographyFromPersonId(personId);
			person.setMovies(filmography);
			return person;
		}
		catch (Exception e)
		{
			throw new WrapperNetflixException(e); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public List<NetflixMovie> getMovies(String movieNameToken, int maxResults, int startIndex) throws WrapperNetflixException
	{
		try
		{
			String resultFromNetflix = sendRequestToNetflix(APIEndpoints.CATALOG_URI, new String[][] {{"term", movieNameToken}, {"max_results", String.valueOf(maxResults)}, {"start_index", String.valueOf(startIndex)}});			
			return NetflixParserUtil.getMoviesFromResponse(resultFromNetflix);			
		}	
		catch (Exception e)
		{
			throw new WrapperNetflixException(e); 
		}		
	}
	
	/**
	 * {@inheritDoc}
	 */		
	public NetflixMovie getMovieFromId(String movieId) throws WrapperNetflixException
	{
		try
		{
			String resultFromNetflix = sendRequestToNetflix(movieId, new String[][] {});
			NetflixMovie movie = NetflixParserUtil.getMovieFromXMLCatalogString(resultFromNetflix);
			
			// synopsis
			movie.setSynopsis(getSynopsisForMovie(movieId));
			// actors
			movie.setCast(getCastForMovie(movieId));
			// directors
			movie.setDirectors(getDirectorsForMovie(movieId, resultFromNetflix));
			//awards
			movie.setAwards(getAwardsForMovie(movieId, resultFromNetflix));
			
			return movie;
		}
		catch (Exception e)
		{
			throw new WrapperNetflixException(e);
		}
	}	
	
	
	/* ************************************* PRIVATE METHODS ********************************************/
	
	/**
	 * Sent request to netflix to obtain filmography of person.
	 */
	private List<NetflixMovie> getFilmographyFromPersonId(Long peopleId) throws Exception
	{
		String resultFromNetflix = sendRequestToNetflix(APIEndpoints.PEOPLE_URI + "/" + peopleId + "/filmography", new String[][] {});
		return NetflixParserUtil.getFilmography(resultFromNetflix);		
	}
	
	/**
	 * Sent request to netflix to obtain synopsis of movie.
	 */	
	private String getSynopsisForMovie(String movieId) throws Exception
	{
		String resultFromNetflix = sendRequestToNetflix(movieId + "/synopsis", new String[][] {});
		return NetflixParserUtil.getSynopsis(resultFromNetflix);		
	}
	
	/**
	 * Sent request to netflix to obtain cast for movie.
	 */		
	private List<NetflixPerson> getCastForMovie(String movieId) throws Exception
	{
		String resultFromNetflix = sendRequestToNetflix(movieId + "/cast", new String[][] {});
		return NetflixParserUtil.getPeoplesFromResponse(resultFromNetflix);
	}
	
	/**
	 * Sent request to netflix to obtain directors for movie. movieXML contains netflix response for movie and it should contain directors url.
	 */			
	private List<NetflixPerson> getDirectorsForMovie(String movieId, String movieXML) throws Exception
	{
		String directorsUrl = movieId + "/directors";
		
		// old response should contains director's url. If it does not contain it, then we don't need to send another request to netflix
		if (movieXML.contains(directorsUrl))
		{
			String resultFromNetflix = sendRequestToNetflix(directorsUrl, new String[][] {});
			return NetflixParserUtil.getPeoplesFromResponse(resultFromNetflix);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Sent request to netflix to obtain awards for movie. movieXML contains netflix response for movie and it should contain awards url.
	 */			
	private List<NetflixAward> getAwardsForMovie(String movieId, String movieXML) throws Exception
	{
		String awardsUrl = movieId + "/awards";
		
		// old response should contains award's url. If it does not contain it, then we don't need to send another request to netflix
		if (movieXML.contains(awardsUrl))
		{
			String resultFromNetflix = sendRequestToNetflix(awardsUrl, new String[][] {});
			return NetflixParserUtil.getAwardsFromResponse(resultFromNetflix);
		}
		else
		{
			return null;
		}		
	}
	
	/**
	 * Sending of request to netflix with usage of netflix api.
	 * 
	 * note: Needs to be synchronized because NetflixAPIClient is internally using Apache HttpClient with non thread-safe SimpleHttpConnectionManager.
	 *  
	 * @param uri to send netflix request.
	 * @param callParams parameters for uri.
	 * @return response from netflix
	 * @throws Exception if some exception in netflix occured or if response code from netflix is different than 200.
	 */
	private synchronized String sendRequestToNetflix(String uri, String[][] callParams) throws Exception
	{
		Map<String, String> callParamsAsMap = convertParamsToMap(callParams);
		String results = null;
		
		NetflixAPIResponse response = apiClient.makeConsumerSignedApiCall(uri, callParamsAsMap, NetflixAPIClient.GET_METHOD_TYPE);
		results = response.getResponseBody();
		int statusCode = response.getStatusCode();
		if (statusCode == 200)
		{				
			return results;
		}
		else
		{
			throw new IllegalStateException("Got error from Netflix. Status code: " + statusCode + ", response: " + results);
		}
	}
	
	/**
	 * Simply convert array to hashmap (assumption is that fields in first array are tuples)
	 */
	private Map<String, String>convertParamsToMap(String[][] paramsAsArray)
	{
		Map<String, String> result = new HashMap<String, String>();
		for (String[] tuple : paramsAsArray)
		{
			result.put(tuple[0], tuple[1]);
		}
		return result;
	}
	
	/**
	 * Testing purpose
	 * 
	 */
	public static void main(String[] args) throws WrapperNetflixException, Exception
	{
		NetflixProviderImpl provider = new NetflixProviderImpl();
//		System.out.println("Get peoples (Tom Cruise): " + provider.getPeoples("Tom Cruise", DEFAULT_MAX_RESULTS, 0));
//		System.out.println("Get person (id = 20660): " + provider.getPersonFromId(20660l));
//		System.out.println("Get movie (id=70001990): " + provider.getMovieFromId(70001990l));
		System.out.println("Get movies (Terminator): " + provider.getMovies("Robin Hood", DEFAULT_MAX_RESULTS, 0));
		
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles", new String[][] {{"term", "Terminator"}}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/series/70079969", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/series/60030455", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/series/seasons/70012756", new String[][] {}));
		
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70001990/synopsis", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70001990/directors", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70001990/awards", new String[][] {}));		
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70001990/cast", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70047412/format_availability", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70047412/screen_formats", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70047412/languages_and_audio", new String[][] {}));
//		System.out.println(provider.sentRequestToNetflix("http://api.netflix.com/catalog/titles/movies/70001990/similars", new String[][] {{"max_results", "5"}}));
	}
}
