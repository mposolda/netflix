/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portlet.ui;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portlet.NetflixMovie;
import org.jboss.portlet.NetflixPerson;
import org.jboss.portlet.util.NetflixProvider;
import org.jboss.portlet.util.NetflixProviderImpl;
import org.jboss.portlet.util.WrapperNetflixException;


/**
 * JSF bean for encapsulating ui state and calling ui actions.
 * 
 * @author Prabhat Jha pjha@redhat.com
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 *
 */
@ManagedBean(name = "netflix")
@SessionScoped
public class NetflixBean 
{
	private static final String OUTCOME_SUCCESS = "netflix";
	private static final String OUTCOME_ERROR = "netflix";
	private static final String MOVIES_TAB = "Movies and TV shows";
	private static final String PEOPLE_TAB = "Actors and directors";
	private static final Log LOG = LogFactory.getLog(NetflixBean.class);
	
	// instance of netflixProvider to call netflix operations
	private NetflixProvider netflixProvider = new NetflixProviderImpl();
	
	// jsf form fields
	private String currentMovieName;
	private String currentStarName;
	
	// movies
	private List<NetflixMovie> searchedMovies;
	private String idOfMovieForDetail;
	private NetflixMovie movieForDetail;
	
	// stars
	private List<NetflixPerson> searchedStars;
	private Long idOfStarForDetail;
	private NetflixPerson starForDetail;
	
	private String selectedTab;
	
	public NetflixBean()
	{
	}

	/* **************************** GETTERS AND SETTERS *****************************/
	
	public String getCurrentMovieName() 
	{
		return currentMovieName;
	}

	public void setCurrentMovieName(String currentMovieName) 
	{
		this.currentMovieName = currentMovieName;
	}

	public String getCurrentStarName() 
	{
		return currentStarName;
	}

	public void setCurrentStarName(String currentStarName) 
	{
		this.currentStarName = currentStarName;
	}
	
	public String getIdOfMovieForDetail() 
	{
		return idOfMovieForDetail;
	}

	public void setIdOfMovieForDetail(String idOfMovieForDetail) 
	{
		this.idOfMovieForDetail = idOfMovieForDetail;
	}

	public List<NetflixMovie> getSearchedMovies() 
	{
		return searchedMovies;
	}	
	
	public NetflixMovie getMovieForDetail() 
	{
		return movieForDetail;
	}

	public Long getIdOfStarForDetail() 
	{
		return idOfStarForDetail;
	}

	public void setIdOfStarForDetail(Long idOfStarForDetail) 
	{
		this.idOfStarForDetail = idOfStarForDetail;
	}

	public List<NetflixPerson> getSearchedStars() 
	{
		return searchedStars;
	}

	public NetflixPerson getStarForDetail() 
	{
		return starForDetail;
	}

	/**
	 * 
	 * @return true if table with search results for movies should be visible
	 */
	public boolean isSearchResultsVisible()
	{
		return currentMovieName != null && idOfMovieForDetail == null;
	}
	
	/**
	 * @return true if movie detail should be visible
	 */
	public boolean isMovieDetailVisible()
	{
		return idOfMovieForDetail != null;
	}	

	/**
	 *  
	 * @return true if table with search results for stars should be visible
	 */
	public boolean isStarSearchResultsVisible()
	{
		return currentStarName != null && idOfStarForDetail == null;
	}
	
	/**
	 * 
	 * @return true if movie detail should be visible
	 */
	public boolean isStarDetailVisible()
	{
		return idOfStarForDetail != null;
	}	

	public String getSelectedTab() 
	{
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) 
	{
		this.selectedTab = selectedTab;
	}
	
	public String getMoviesTabText()
	{
		return MOVIES_TAB;
	}
	
	public String getPeopleTabText()
	{
		return PEOPLE_TAB;
	}	
	
	/* **************************** JSF ACTIONS *****************************/

	/**
	 * Called from UI initial page when user press "enter" (or "login") button.
	 * 
	 * @return loginSuccess if login has been successful.
	 */
	public String login()
	{
		return "loginSuccess";
	}
	
	/**
	 * Called from UI when user press button for searching movies.
	 * 
	 */
	public String searchForMovie()
	{
		// we are searching for movies so current detail is no longer valid. 
		idOfMovieForDetail = null;
		
		try
		{
			searchedMovies = netflixProvider.getMovies(getCurrentMovieName(), 100, 0);
			return OUTCOME_SUCCESS;	
		}
		catch (WrapperNetflixException wne)
		{
			searchedMovies = null;
    		LOG.error("Error when searching for movies with search token: " + getCurrentMovieName(), wne);        		    		     		
    		FacesContext.getCurrentInstance().addMessage("movie", new FacesMessage("Unexpected error when searching for movies with search token: " + getCurrentMovieName() + ". Check server log for details."));
    		return OUTCOME_ERROR;			
		}		
	}
	
	/**
	 * Called from UI when user press link to some movie.
	 * 
	 */	
	public String showMovieDetail()
	{
		// seting of selected rich:tab to tab with "Movie"
		selectedTab = MOVIES_TAB;		
		try
		{
			movieForDetail = netflixProvider.getMovieFromId(getIdOfMovieForDetail());
			return OUTCOME_SUCCESS;	
		}
		catch (WrapperNetflixException wne)
		{
			movieForDetail = null;
    		LOG.error("Error when obtaining detail of movie with netflix id: " + getIdOfMovieForDetail(), wne);        		    		     		
    		FacesContext.getCurrentInstance().addMessage("movie", new FacesMessage("Unexpected error when obtaining detail of movie with netflix id: " + getIdOfMovieForDetail() + ". Check server log for details."));
    		return OUTCOME_ERROR;			
		}				
	}
	
	/**
	 * Called when press 'back' button in movie detail page.
	 */
	public String backFromMovieDetail()
	{
		idOfMovieForDetail = null;
		return OUTCOME_SUCCESS;	
	}
	
	/**
	 * Called from UI when user press button for searching stars.
	 * 
	 */
	public String searchForStar()
	{
		// we are searching for stars so current detail is no longer valid. 
		idOfStarForDetail = null;
		
		try
		{
			searchedStars = netflixProvider.getPeoples(getCurrentStarName(), 100, 0);
			return OUTCOME_SUCCESS;	
		}
		catch (WrapperNetflixException wne)
		{
			searchedStars = null;
    		LOG.error("Error when searching for stars with search token: " + getCurrentStarName(), wne);        		    		     		
    		FacesContext.getCurrentInstance().addMessage("star", new FacesMessage("Unexpected error when searching for stars with search token: " + getCurrentStarName() + ". Check server log for details."));
    		return OUTCOME_ERROR;			
		}		
	}
	
	/**
	 * Called from UI when user press link to some star.
	 * 
	 */	
	public String showStarDetail()
	{
		// seting of selected rich:tab to tab with "Star"
		selectedTab = PEOPLE_TAB;
		try
		{
			starForDetail = netflixProvider.getPersonFromId(getIdOfStarForDetail());
			return OUTCOME_SUCCESS;	
		}
		catch (WrapperNetflixException wne)
		{
			starForDetail = null;
    		LOG.error("Error when obtaining detail of star with netflix id: " + getIdOfStarForDetail(), wne);        		    		     		
    		FacesContext.getCurrentInstance().addMessage("star", new FacesMessage("Unexpected error when obtaining detail of star with netflix id: " + getIdOfStarForDetail() + ". Check server log for details."));
    		return OUTCOME_ERROR;			
		}				
	}
	
	/**
	 * Called when press 'back' button in star detail page.
	 */
	public String backFromStarDetail()
	{
		idOfStarForDetail = null;
		return OUTCOME_SUCCESS;	
	}	
	
}
