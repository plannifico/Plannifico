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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.data.records.RelationalFactPlanningRecord;

public class RelationalPlanningSet implements PlanningSet {
	
	private final static Logger logger = Logger.getLogger (RelationalMeasureSet.class.getName());
	
	private Collection<PlanningRecord> setData = new ArrayList<>();
	
	private PlannificoFactory factory = PlannificoFactoryProvider.getInstance();
	
	private String measures;
	private String filter;
	private String groupby;
	
	public RelationalPlanningSet(String measures, String filter, String groupby) {
		
		this.measures = measures;
		this.filter = filter;
		this.groupby = groupby;
	}

	public void populateFromRelationalDB (Connection conn, String planning_universe, String measure_set_name) {
		
		try {			
			
			String join_and_where = "";
			
			join_and_where = getJoinAndWhere (filter, groupby, measure_set_name);
			
			String select_fields_clause = getSelectClause ();
			
			String measure_clause = getMeasuresClause();
			
			String sql_stm = 
				"SELECT " + select_fields_clause.toUpperCase() + "," + 
				measure_clause +  
				" FROM " + RelationalMeasureSet.MEASURE_SET_PREFIX + measure_set_name + 
				join_and_where + 
				" GROUP BY " + select_fields_clause.toUpperCase();
			
			PreparedStatement stmt = conn.prepareStatement (sql_stm);
			
			ResultSet rels_rs = stmt.executeQuery();
			
			while (rels_rs.next ()) {
				
				PlanningRecord record = factory.getFactPlanningRecord (planning_universe, measure_set_name);
				
				if (record instanceof RelationalFactPlanningRecord)
						
					((RelationalFactPlanningRecord)record).
						populatePlanningRecordFromResultSet(rels_rs);
				
				setData.add (record);								
			}			
			
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving records for set %s : %s", 
							measure_set_name, 
							e.getMessage()));
		}		
	}

	private String getMeasuresClause() {
		
		String measure_clause = "";
		
		String [] measures_split = measures.split(";");
		
		for (String measure_str : measures_split) {
			
			measure_clause +=  
					"SUM (" + RelationalMeasureSet.MEASURE_PREFIX + 
					measure_str + ") as " + measure_str + ",";
		}
		
		return measure_clause.substring(0, measure_clause.length()-1);
	}
	
	private String getSelectClause() {
		
		String select_clause = "";
		
		String [] groupby_split = groupby.split(";");
		
		for (String groupby_str : groupby_split) {
			
			select_clause +=  
					RelationalMeasureSet.DIM_PREFIX + groupby_str.replace(".", ".\"") + "\",";
		}
		
		return select_clause.substring(0, select_clause.length()-1);
	}

	@Override
	public Collection<PlanningRecord> getData () {
		
		return setData;
	}
	
	private static String getJoinAndWhere (String filter, String groupby, String measure_set_name) {
		
		String join_and_where;
		String where_clause = "WHERE ";
		String join_clause = "";
		
		List<String> filter_elements = Arrays.asList (filter.split(";"));
		
		boolean is_first_where = true;
		
		HashMap <String, String> dimension_already_in_join = new HashMap<>();
		
		for (String filter_element : filter_elements) {
			
			String[] eq_elements = filter_element.split("=");
			
			String[] dimension_attribute = eq_elements [0].split("\\.") ;
			
			String dimension = dimension_attribute [0];			
			String attribute = dimension_attribute [1];
			
			dimension_already_in_join.put(dimension, dimension);
			
			if (is_first_where) {
				
				where_clause += RelationalMeasureSet.DIM_PREFIX + dimension + "." + attribute + " = '" + eq_elements [1] + "'";
				is_first_where = false;				
			}
				
			else 
				where_clause += " AND " + RelationalMeasureSet.DIM_PREFIX + dimension + "." + attribute + " = '" + eq_elements [1] + "'";
			
			join_clause += " JOIN DIM_" + 
					dimension + " on " +
					RelationalMeasureSet.DIM_PREFIX + dimension + "." + dimension + " = " + 
					RelationalMeasureSet.MEASURE_SET_PREFIX + measure_set_name + "." + 
					dimension;
		}			
		
		List<String> groupby_elements = Arrays.asList (groupby.split(";"));
		
		is_first_where = true;
		
		for (String groupby_element : groupby_elements) {
			
			String[] eq_elements = groupby_element.split("=");
			
			String[] dimension_attribute = eq_elements [0].split("\\.") ;
			
			String dimension = dimension_attribute [0];			
			String attribute = dimension_attribute [1];
			
			if (dimension_already_in_join.containsKey(dimension))
				continue;
						
			join_clause += " JOIN DIM_" + 
					dimension + " on " +
					RelationalMeasureSet.DIM_PREFIX + dimension + "." + dimension + " = " + 
					RelationalMeasureSet.MEASURE_SET_PREFIX + measure_set_name + "." + 
					dimension;
		}			
		
		join_and_where = join_clause + " " + where_clause;
		
		return join_and_where;
	}

}
