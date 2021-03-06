package com.techelevator.jdbc;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import model.Site;
import model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Site> getAllSites() {
		ArrayList<Site> allSites = new ArrayList<>();
		String sqlListAllParks =  "SELECT * " +
									"FROM site ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlListAllParks);
		while(results.next()) {
			Site name = mapRowToSite(results);
			allSites.add(name);
		}
		return allSites;
	}
	
	
	@Override
	public List<Site> getSitesByCampgroundIdAndDates(long campgroundId, String arrival, String departure, int arrivalMonth, int departureMonth) {
		ArrayList<Site> siteReservation = new ArrayList<>();
		String sqlListSites = "SELECT distinct site.site_id, site.campground_id, site.site_number, site.max_occupancy, site.accessible, site.max_rv_length, site.utilities, campground.daily_fee "
								   + "FROM site JOIN campground ON site.campground_id = campground.campground_id WHERE site.campground_id = ? AND site.site_id "
								   + "NOT IN "
								   + "(SELECT site.site_id FROM site "
								   + "JOIN reservation ON reservation.site_id = site.site_id "
								   + "WHERE ((to_date(?, 'YYYY/MM/DD')) <= reservation.to_date AND (to_date(?, 'YYYY/MM/DD')) >= reservation.from_date)) "
								   + "AND ? > CAST(campground.open_from_mm AS int) AND ? < CAST(campground.open_to_mm AS int) "
								   + "ORDER BY site.site_number LIMIT 5 ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlListSites, campgroundId, arrival, departure, arrivalMonth, departureMonth);
		System.out.println("Sites: ");		
		while(results.next() ) {
			Site name = mapRowToSite2(results);
			siteReservation.add(name);
		}
		return siteReservation;
		
	}
	
	private Site mapRowToSite(SqlRowSet results) {
		Site site;
		site = new Site();
		site.setSite_id(results.getLong("site_id"));
		site.setCampground_id(results.getLong("campground_id"));
		site.setSite_number(results.getInt("site_number"));
		site.setMax_occupancy(results.getInt("max_occupancy"));
		site.setAccessible(results.getBoolean("accessible"));
		site.setMax_rv_length(results.getInt("max_rv_length"));
		site.setUtilities(results.getBoolean("utilities"));
		return site;
	}

	private Site mapRowToSite2(SqlRowSet results) {
		Site site;
		site = new Site();
		site.setSite_id(results.getLong("site_id"));
		site.setCampground_id(results.getLong("campground_id"));
		site.setSite_number(results.getInt("site_number"));
		site.setMax_occupancy(results.getInt("max_occupancy"));
		site.setAccessible(results.getBoolean("accessible"));
		site.setMax_rv_length(results.getInt("max_rv_length"));
		site.setUtilities(results.getBoolean("utilities"));
		site.setDailyFee(results.getDouble("daily_fee"));
		return site;
	}
	

}
