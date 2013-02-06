package org.jboss.portlet.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portlet.NetflixAward;
import org.jboss.portlet.NetflixMovie;
import org.jboss.portlet.NetflixPerson;
import org.jdom.Element;

/**
 * Parsing of netflix responses with usage of xpath and dom.
 * 
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
class NetflixParserUtil 
{
	private static final Log LOG = LogFactory.getLog(NetflixParserUtil.class);
	
	/**
	 * Get list of people from "people" response in XML format.
	 * 
	 * @param netflixResponse XML response from netflix
	 * @return list of all people from given XML
	 * @throws Exception
	 */
	static List<NetflixPerson> getPeoplesFromResponse(String netflixResponse) throws Exception
	{		
		List<NetflixPerson> result = new ArrayList<NetflixPerson>();
		
		// if we are parsing from search results, base element is people/person. If we are using only single search, base element is person
		List<Element> elements = XMLUtils.getXPathElementListFromString(netflixResponse, "/people/person");
		int i = 0;
		for (Element element : elements)
		{			
			NetflixPerson person = getPersonFromElement(element);
			person.setSearchIndex(++i);
			result.add(person);
		}
		return result;
	}	
	
	/**
	 * Get person from "person" response in XML format.
	 * 
	 * @param personXMLString XML response from netflix
	 * @return person
	 * @throws Exception
	 */	
	static NetflixPerson getPersonFromXMLString(String personXMLString) throws Exception
	{
		Element personElement = XMLUtils.getXPathElementFromString(personXMLString, "/person");
		return getPersonFromElement(personElement);
	}
	
	/**
	 * Get movies from "catalog_titles" response in XML format.
	 * 
	 * @param netflixResponse
	 * @return
	 * @throws Exception
	 */
	static List<NetflixMovie> getMoviesFromResponse(String netflixResponse) throws Exception
	{
		List<NetflixMovie> result = new ArrayList<NetflixMovie>();
				
		List<Element> elements = XMLUtils.getXPathElementListFromString(netflixResponse, "/catalog_titles/catalog_title");
		int i = 0;
		for (Element element : elements)
		{			
			NetflixMovie movie = getMovieFromElement(element);
			movie.setSearchIndex(++i);
			result.add(movie);
		}
		return result;		
	}
	
	/**
	 * get movie from string with single XML element "catalog_title".
	 * 
	 * @param movieXMLString
	 * @return
	 * @throws Exception
	 */
	static NetflixMovie getMovieFromXMLCatalogString(String movieXMLString) throws Exception
	{
		Element movieElement = XMLUtils.getXPathElementFromString(movieXMLString, "/catalog_title");
		return getMovieFromElement(movieElement);
	}	
	
	/**
	 * get list of movies from filmography.
	 * 
	 * @param filmographyXMLString
	 * @return
	 * @throws Exception
	 */
	static List<NetflixMovie> getFilmography(String filmographyXMLString) throws Exception
	{
		List<NetflixMovie> result = new ArrayList<NetflixMovie>();
		
		List<Element> elements = XMLUtils.getXPathElementListFromString(filmographyXMLString, "/filmography/filmography_item");
		int i = 0;
		for (Element element : elements)
		{			
			NetflixMovie movie = getMovieFromElement(element);
			movie.setSearchIndex(++i);
			result.add(movie);
		}
		return result;
	}
	
	/**
	 * get synopsis from synopsis XML.
	 * 
	 * @param synopsisXMLString
	 * @return
	 * @throws Exception
	 */
	static String getSynopsis(String synopsisXMLString) throws Exception
	{
		Element synopsisEl = XMLUtils.getXPathElementFromString(synopsisXMLString, "/synopsis");
		return synopsisEl.getText();
	}
	
	/**
	 * get awards from given XMl string. 
	 * 
	 * @param awardsXMLString
	 * @return
	 * @throws Exception
	 */
	static List<NetflixAward> getAwardsFromResponse(String awardsXMLString) throws Exception
	{
		List<NetflixAward> result = new ArrayList<NetflixAward>();
		
		List<Element> elements = XMLUtils.getXPathElementListFromString(awardsXMLString, "/awards/*");
		for (Element awardElement : elements)
		{
			// winner
			boolean winner = "award_winner".equals(awardElement.getName());
			// year
			String yearStr = awardElement.getAttributeValue("year");
			int year = (yearStr != null) ? parseIntSafe(yearStr) : -1;
			// name
			String name = awardElement.getChild("category").getAttributeValue("label");
			// person
			NetflixPerson person = null;
			Element linkEl = awardElement.getChild("link");
			if (linkEl != null)
			{
				String personName = linkEl.getAttributeValue("title");
				String href = linkEl.getAttributeValue("href");
				Long id = parseLongSafe(href.substring(href.lastIndexOf('/') + 1));
				person = new NetflixPerson(id, personName);
			}
			
			NetflixAward award = new NetflixAward(winner, year, name, person);
			result.add(award);
		}
		return result;
	}
	
	
	/* ************************************* PRIVATE METHODS ********************************************/
	
	/**
	 * Parsing of person from given element
	 * 
	 * @param personElement
	 * @return person
	 */
	private static NetflixPerson getPersonFromElement(Element personElement)
	{
		String idEl = personElement.getChildText("id");
		long id = parseLongSafe(idEl.substring(idEl.lastIndexOf('/') + 1));
		String name = personElement.getChildText("name");
		String bio = personElement.getChildText("bio");
		String webLink = null;

		for (Object el2 : personElement.getChildren("link"))
		{
			Element ell = (Element)el2;
			if ("web page".equals(ell.getAttributeValue("title")))
			{
				webLink = ell.getAttributeValue("href");
			}								
		}
		
		// now we have all informations parsed so we can create and return NetflixPeople instance
		return new NetflixPerson(id, name, bio, webLink);	
	}
	
	/**
	 * get movie from given movie element.
	 * 
	 * @param movieElement
	 * @return
	 */
	private static NetflixMovie getMovieFromElement(Element movieElement)
	{
		// id
		String idEl = movieElement.getChildText("id");

		//		id will be saved as String, not long.		
//		long id = parseLongSafe(idEl.substring(idEl.lastIndexOf('/') + 1));
		
		// titles
		Element titleElem = movieElement.getChild("title");
		String shortTitle = titleElem.getAttributeValue("short");
		String title = titleElem.getAttributeValue("regular");
		
		// pictures
		Element boxArt = movieElement.getChild("box_art");
		String smallPict = boxArt.getAttributeValue("small");
		String mediumPict = boxArt.getAttributeValue("medium");
		String largePict = boxArt.getAttributeValue("large");
		
		// release year
		int releaseYear = parseIntSafe(movieElement.getChildText("release_year"));
		
		// genres and mpaa_ratings
		String mpaaRating = null;
		List<String> genres = new LinkedList<String>();
		List<Element> categories = movieElement.getChildren("category");
		for (Element categoryEl : categories)
		{
			String scheme = categoryEl.getAttributeValue("scheme");
			if ("http://api.netflix.com/categories/mpaa_ratings".equals(scheme))
			{
				mpaaRating = categoryEl.getAttributeValue("label");
			}
			else if ("http://api.netflix.com/categories/genres".equals(scheme))
			{
				genres.add(categoryEl.getAttributeValue("label"));
			}
		}
		
		// runtime in seconds
		int runtimeInSeconds = parseIntSafe(movieElement.getChildText("runtime"));
		
		// averageRating
		float averageRating = parseFloatSafe(movieElement.getChildText("average_rating"));
		
		
		// netflix page and official web page
		String webLink = null;
		String officialWebLink = null;
		for (Object el2 : movieElement.getChildren("link"))
		{
			Element ell = (Element)el2;
			if ("web page".equals(ell.getAttributeValue("title")))
			{
				webLink = ell.getAttributeValue("href");
			}	
			else if ("official webpage".equals(ell.getAttributeValue("title")))
			{
				officialWebLink = ell.getAttributeValue("href");
			}
		}
		
		return new NetflixMovie(idEl, shortTitle, title, smallPict, mediumPict, largePict, releaseYear, mpaaRating, genres, runtimeInSeconds, averageRating, webLink, officialWebLink);
	}
	
	/**
	 * Parse number without throwing NumberFormatException. If parsing fails, it returns -1 as result. 
	 * 
	 * @param numberAsString
	 * @return
	 */
	private static int parseIntSafe(String numberAsString)
	{
		int result = -1;
		try
		{
			result = Integer.parseInt(numberAsString);
		}
		catch (NumberFormatException nfe)
		{			
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Formatting of number failed for string " + numberAsString + " . Using default value " + result + " .", nfe);
			}			
		}
		return result;
	}
	
	/**
	 * Parse number without throwing NumberFormatException. If parsing fails, it returns -1 as result. 
	 * 
	 * @param numberAsString
	 * @return
	 */
	private static float parseFloatSafe(String numberAsString)
	{
		float result = -1f;
		try
		{
			result = Float.parseFloat(numberAsString);
		}
		catch (NumberFormatException nfe)
		{			
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Formatting of number failed for string " + numberAsString + " . Using default value " + result + " .", nfe);
			}			
		}
		return result;
	}	
	
	/**
	 * Parse number without throwing NumberFormatException. If parsing fails, it returns -1 as result. 
	 * 
	 * @param numberAsString
	 * @return
	 */
	private static long parseLongSafe(String numberAsString)
	{
		long result = -1;
		try
		{
			result = Long.parseLong(numberAsString);
		}
		catch (NumberFormatException nfe)
		{			
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Formatting of number failed for string " + numberAsString + " . Using default value " + result + " .", nfe);
			}
		}
		return result;
	}	

}
