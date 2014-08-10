/*Copyright 2014 Rosario Alfano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package org.plannifico.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ActionNotPermittedException;
import org.plannifico.server.ConnectionPoolProvider;
import org.plannifico.server.H2ConnectionPoolProvider;
import org.plannifico.server.PlanningEngineImpl;


/**
 * A relational-based implementation of a {@link PlanningUniverse}
 * 
 * @author ralfano
 *
 */
public class RelationalPlanningUniverse implements PlanningUniverse {

	private static final String DIM_PREFIX = "DIM_%";

	private Map <String, MeasureSet> measureSets 
		= new HashMap <String, MeasureSet> ();
	
	private String name;

	private ArrayList <String> dimensions;	
	
	private final static Logger logger = 
		Logger.getLogger (RelationalPlanningUniverse.class.getName());


	public RelationalPlanningUniverse (String universe_name) {
		
		name = universe_name;
		
		ConnectionPoolProvider cp = H2ConnectionPoolProvider.getInstance();
		
		loadDimensions (cp);
		
		loadMeasureSet (cp);
	}

	private void loadDimensions (ConnectionPoolProvider cp) {
		
		dimensions = new ArrayList<> ();
		
		Connection conn;
		
		try {
		
			conn = cp.getConnection (this.name);
			
			DatabaseMetaData md = conn.getMetaData();
			
			ResultSet rs = md.getTables(null, "PUBLIC", DIM_PREFIX, new String [] {"TABLE"});
			
			while (rs.next()) {
				
				String dimension_name = rs.getString ("TABLE_NAME").replace("DIM_", "");
				
				dimensions.add (dimension_name);
			}
			
			conn.close();
		
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving universe %s dimensions: %s", this.name, e.getMessage()));
			
		} 		
		
	}
	
	private void loadMeasureSet (ConnectionPoolProvider cp) {
		
		PlannificoFactory factory = PlannificoFactoryProvider.getInstance();
		
		try {
		
			Connection conn = cp.getConnection (this.name);
			
			DatabaseMetaData md = conn.getMetaData();
			
			ResultSet rs = md.getTables(null, 
					"PUBLIC", 
					RelationalMeasureSet.MEASURE_SET_PREFIX + "%", 
					new String [] {"TABLE"});
			
			while (rs.next()) {
				
				String measure_set_name = 
						rs.getString ("TABLE_NAME").replace(RelationalMeasureSet.MEASURE_SET_PREFIX, "");
				
				MeasureSet measure_set = 
						factory.getMeasureSet (
								this.name,
								measure_set_name);
				
				measureSets.put (measure_set_name, measure_set);
			}
		
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving universe %s dimensions: %s", this.name, e.getMessage()));
			
		}
	}

	@Override
	public String getName() {
		
		return name;
	}

	@Override
	public PlanningRecord getRecordByKey(String measure_set_name, String key) throws WrongPlanningRecordKey {		
		
		return measureSets.get(measure_set_name).getRecordByKey(key);
	}

	@Override
	public PlanningField getAggregatedValue (String measure_set_name,
			String measure_name, String fields)
			throws ActionNotPermittedException {
		
		return measureSets.get(measure_set_name).
				getAggregatedValue (measure_name, fields);
				
	}

	@Override
	public int setAggregatedValue (
			String measure_set_name, 
			String measure_name,
			double new_value, String fields) throws ActionNotPermittedException, LogicCalculationException {
		
		return measureSets.get(measure_set_name).
				setAggregatedValue (measure_name, new_value, fields);
	}

	@Override
	public int setAggregatedValue (String measure_set_name, String measure_name,
			LogicType distribution_logic, double new_value, String fields)
			throws ActionNotPermittedException, LogicCalculationException {
		
		return measureSets.get(measure_set_name).
				
				setAggregatedValue (measure_name, distribution_logic, new_value, fields);
	}	

	@Override
	public long getMasureSetRecordsNumber (String measure_set_name)
			throws ActionNotPermittedException, UniverseNotExistException {
		
		return measureSets.get(measure_set_name).
				getMasureSetRecordsNumber();
	}

	@Override
	public Collection<String> getMeasureSetsMeasureNames (String measure_set_name)
			throws UniverseNotExistException {
		
		return measureSets.get(measure_set_name).
				getMeasureSetsMeasureNames (); 
	}

	@Override
	public Collection<PlanningRecord> getRecordsByAggregation (
			String measure_set_name, String fields)
			throws ActionNotPermittedException {
		
		return measureSets.get(measure_set_name).
				
				getRecordsByAggregation (fields);
	}
	
	@Override
	public Collection <String> getPlanningDimensions ()
			throws UniverseNotExistException {
		
		return dimensions;		
		
	}
	
	@Override
	public Map<String, Collection<String>> getAllDimensionRelationships (String dimension) {
		
		ConnectionPoolProvider cp = H2ConnectionPoolProvider.getInstance();
		
		Map<String, Collection<String>> rels = new HashMap<>();
		
		
		try {
		
			Connection conn = cp.getConnection (this.name);
			
			DatabaseMetaData md = conn.getMetaData();
			
			ResultSet rs = md.getTables(null, "PUBLIC", "DIM_" + dimension.toUpperCase(), new String [] {"TABLE"});
			
			if (!rs.next()) return rels;
			
			logger.fine (String.format ("Table %s exists.", "DIM_" + dimension.toUpperCase()));
			
			rs = md.getColumns(null, "PUBLIC", "DIM_" + dimension.toUpperCase(), null);
			
			int i=1;
			
			String cols = "";
			
			while (rs.next()) {			
				
				String col = "\"" + rs.getString ("COLUMN_NAME") + "\"";
				
				logger.fine (String.format ("Retrived column: %s", col));
				
				cols += col + ",";
			}
			
			PreparedStatement stmt = 
					conn.prepareStatement (
							"SELECT " + cols + "1" +
							" FROM DIM_" + dimension.toUpperCase());
			
			ResultSet rels_rs = stmt.executeQuery();
			
			ResultSetMetaData rsmd = rels_rs.getMetaData();

			int columnsNumber = rsmd.getColumnCount();
			
			while (rels_rs.next ()) {
			
				Collection<String> attrs = new ArrayList<>();
				
				String key = rels_rs.getString (dimension.toUpperCase());
				
				int col = 1;
				
				while (col <= columnsNumber) {
					
					attrs.add (rels_rs.getString(col));
					col++;
				}
				
				rels.put(key, attrs);
			}
			
		
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving universe %s relationships for dimension %s: %s", 
							this.name, 
							dimension ,
							e.getMessage()));
			
		}
		
		return rels;
	}

	@Override
	public Collection<String> getPlanningDimensionRelationship (
			String dimension, 
			String dimension_key)
			throws UniverseNotExistException {
		
		ConnectionPoolProvider cp = H2ConnectionPoolProvider.getInstance();
		
		ArrayList<String> rels = new ArrayList<> ();
		
		try {
		
			Connection conn = cp.getConnection (this.name);
			
			DatabaseMetaData md = conn.getMetaData();
			
			ResultSet rs = md.getTables(null, "PUBLIC", "DIM_" + dimension.toUpperCase(), new String [] {"TABLE"});
			
			if (!rs.next()) return rels;
			
			logger.fine (String.format ("Table %s exists.", "DIM_" + dimension.toUpperCase()));
			
			rs = md.getColumns(null, "PUBLIC", "DIM_" + dimension.toUpperCase(), null);
			
			int i=1;
			
			while (rs.next()) {			
				
				String col = rs.getString ("COLUMN_NAME");
				
				logger.fine (String.format ("Retrived column: %s", col));
				
				PreparedStatement stmt = 
						conn.prepareStatement (
								"SELECT *" +  
								" FROM DIM_" + dimension.toUpperCase() + 
								" WHERE " +dimension + " = '"+ dimension_key + "'");
				
				ResultSet rels_rs = stmt.executeQuery();
				
				while (rels_rs.next ()) {
					
					String rel = rels_rs.getString (col);
					
					rels.add (col + "=" + rel);
				}					
			}			
		
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving universe %s relations for dimension %s: %s", 
							this.name, 
							dimension ,
							e.getMessage()));
			
		}
		
		return rels;		
	}

	@Override
	public int getMeasureSetsNumber() {
		
		return measureSets.size();
	}

	@Override
	public Collection<String> getMeasureSetsName() {
		
		return measureSets.keySet();
	}

	@Override
	public PlanningSet getDataSet(String measureset, String measures,
			String filter, String groupby) {
		
		return measureSets.get (measureset)
				.getDataSet (measures, filter, groupby);
	}

	

}
