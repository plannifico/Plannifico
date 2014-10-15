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
package org.plannifico.data.records;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.MeasureSet;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.RelationalMeasureSet;
import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.server.C3P0ConnectionPoolProvider;
import org.plannifico.server.ConnectionPoolProvider;

public class RelationalFactPlanningRecord extends FactPlanningRecord {
	
	private final static Logger logger = Logger.getLogger (RelationalMeasureSet.class.getName());

	public RelationalFactPlanningRecord (String universe, String measure_set) {
		
		super (universe, measure_set);
	}

	@Override
	public void populateRecordByKey (String key) throws WrongPlanningRecordKey {
		
		ConnectionPoolProvider cp = C3P0ConnectionPoolProvider.getInstance ();
		
		try {
			
			Connection conn = cp.getConnection (this.universeName);
			
			List<String> aggregation_eqs = Arrays.asList (key.split(";"));
			
			boolean is_first_where = true;
			
			String where = " WHERE ";
			
			for (String aggregation_eq : aggregation_eqs) {
				
				String[] eq_elements = aggregation_eq.split("=");
				
				if (is_first_where) {
					
					where += eq_elements [0] + " = '" + eq_elements [1] + "'";
					is_first_where = false;
					
				} else {
					
					where += " AND " + eq_elements [0] + " = '" + eq_elements [1] + "'";
				}
				
			}		
			
			PreparedStatement stmt = 
					conn.prepareStatement (
							"SELECT *" +
							" FROM " + RelationalMeasureSet.MEASURE_SET_PREFIX + this.measureSetName + 
							where);
			
			ResultSet rels_rs = stmt.executeQuery();
			
			//logger.fine ("is first record: " + rels_rs.first());
			
			if (!rels_rs.first()) 
				throw new WrongPlanningRecordKey ("No record found for the given key");
			
			else {				
				 
				populatePlanningRecordFromResultSet (rels_rs);				
				
				if (!rels_rs.isLast()) 
					throw new WrongPlanningRecordKey ("More than one record returned");
			}			
			
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving records for set %s : %s", 
							this.measureSetName, 
							e.getMessage()));
		}	

	}
	
	public void populatePlanningRecordFromResultSet (ResultSet result) throws SQLException {
	
		PlannificoFactory factory = PlannificoFactoryProvider.getInstance();
			
		ResultSetMetaData rsmd = result.getMetaData ();
	
		int columnsNumber = rsmd.getColumnCount ();
		
	
		for (int i = 1; i <= columnsNumber; i++) {				
	
			Object field = result.getObject (i);
			
			try {
			
				if (field instanceof String) {
					
					String column_name = rsmd.getTableName(i) + "." + rsmd.getColumnName(i);
					
					logger.finer (
							String.format ("populating record with field: %s", 
									column_name));
					
					addField (factory.getTextField (
							column_name
								.replace (RelationalMeasureSet.MEASURE_SET_PREFIX, "")
								.replace (RelationalMeasureSet.DIM_PREFIX, "")
								.replace (RelationalMeasureSet.DIM_SUFFIX, ""), 
							(String) field));					
					
				} else if (field instanceof Number) {
					
					addField (factory.getNumberField (
							rsmd.getColumnName(i)
								.replace (RelationalMeasureSet.MEASURE_PREFIX, ""), 
							(Double) field));		
				}
				
				
			} catch (FieldAlreadyExistsException e) {
					
					logger.finer (
							String.format ("Field %s already exists: %s", 
									rsmd.getColumnName(i), e.getMessage()));
					
			}
		}			
	}
	
}
