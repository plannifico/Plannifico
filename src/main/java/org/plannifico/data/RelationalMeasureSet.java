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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.WrongFieldTypeException;
import org.plannifico.data.records.NullRecord;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.data.records.RelationalFactPlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ActionNotPermittedException;
import org.plannifico.server.C3P0ConnectionPoolProvider;
import org.plannifico.server.ConnectionPoolProvider;

/**
 * A {@link MeasureSet} implementation based on a relational database
 * 
 * @author Rosario Alfano
 *
 */
public class RelationalMeasureSet implements MeasureSet {
	
	public static final String MEASURE_SET_PREFIX = "MEASURE_SET_";
	public static final String MEASURE_PREFIX = "VALUE_";
	public static final String DIM_PREFIX = "DIM_";
	public static final String DIM_SUFFIX = "$M";
	
	private String name;

	private String planningUniverse;

	private static PlannificoFactory factory;	
	
	private final static Logger logger = Logger.getLogger (RelationalMeasureSet.class.getName());
	

	
	public RelationalMeasureSet (String planning_universe, String name) {
		
		this.name = name;
		this.planningUniverse = planning_universe;
		
		factory = PlannificoFactoryProvider.getInstance();
	}
	
	public Connection provideAConnection () throws SQLException {
		
		ConnectionPoolProvider cp = C3P0ConnectionPoolProvider.getInstance();
		
		return cp.getConnection (this.planningUniverse);
	}

	@Override
	public String getName() {
		
		return name;
	}	

	@Override
	public PlanningField getAggregatedValue (String measure_name, String fields)
			throws ActionNotPermittedException {		
		
		PlanningField return_value = factory.getNullField();
		
		try {
			
			Connection conn = provideAConnection ();
			
			return_value = getAggregatedValue (
					this.name,
					measure_name, 
					fields,
					conn);
			
			
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving measure set %s aggregation for measure %s: %s", 
							this.name, 
							measure_name ,
							e.getMessage()));
		}		
		
		return return_value;
	}
	
	
	
	@Override
	public PlanningRecord getRecordByKey (String key) { 
		
		logger.fine ("Received getRecordByKey " + key);

		PlannificoFactory factory = PlannificoFactoryProvider.getInstance();
		
		PlanningRecord record = factory.getFactPlanningRecord (this.planningUniverse, this.name);
		
		try {
		
			record.populateRecordByKey (key);
			
			logger.fine ("record populated " + record.getColumnNumber());
		
		} catch (WrongPlanningRecordKey e) {
			
			logger.warning ("Received WrongPlanningRecordKey " + e.getMessage());
			
			return new NullRecord();
		}
				
		return record;
	}

	@Override
	public Collection<PlanningRecord> getRecordsByAggregation (String fields)
			throws ActionNotPermittedException {

		Collection<PlanningRecord> records = new ArrayList < PlanningRecord>();
		
		try {
			
			Connection conn = provideAConnection ();
			
			String join_and_where = "";
			
			join_and_where = getJoinAndWhere (fields, this.name);

			PreparedStatement stmt = 
					conn.prepareStatement (
							"SELECT *" +
							" FROM " + MEASURE_SET_PREFIX + this.name + 
							join_and_where);
			
			ResultSet rels_rs = stmt.executeQuery();
			
			while (rels_rs.next ()) {
				
				PlanningRecord record = factory.getFactPlanningRecord (this.planningUniverse, this.name);
				
				if (record instanceof RelationalFactPlanningRecord)
						
					((RelationalFactPlanningRecord)record).
						populatePlanningRecordFromResultSet(rels_rs);
				
				records.add (record);								
			}			
			
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving records for set %s : %s", 
							this.name, 
							e.getMessage()));
		}		
		
		return records;
	}

	@Override
	public long getMasureSetRecordsNumber () throws ActionNotPermittedException,
			UniverseNotExistException {
		
		int res = -1;
		
		try {
			
			Connection conn = provideAConnection ();
			
			PreparedStatement stmt = 
					conn.prepareStatement (
							"SELECT count(*)" +
							" FROM " + MEASURE_SET_PREFIX + this.name);
			
			ResultSet rels_rs = stmt.executeQuery();
			
			if (rels_rs.first ()) {
				
				res = rels_rs.getInt (1);								
			}			
			
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving records for set %s : %s", 
							this.name, 
							e.getMessage()));
		}	
		
		return res;
	}

	@Override
	public Collection<String> getMeasureSetsMeasureNames()
			throws UniverseNotExistException {
		
		ArrayList<String> measures = new ArrayList<> ();
		
		try {
		
			Connection conn = provideAConnection ();
			
			DatabaseMetaData md = conn.getMetaData();
			
			ResultSet rs = md.getColumns (null, "PUBLIC", MEASURE_SET_PREFIX + this.name, null);
			
			int i=1;
			
			while (rs.next()) {			
				
				String col = rs.getString ("COLUMN_NAME");
				
				if (col.startsWith (MEASURE_PREFIX))
					measures.add (col.replace(MEASURE_PREFIX, ""));
			}
			
		
		} catch (SQLException e) {
			
			logger.warning (
					String.format ("Error retriving measure set %s measures: %s", 
							this.name, 
							e.getMessage()));
			
		}
		
		return measures;
	}	
	
	@Override
	public int setAggregatedValue (
			String measure_name, 
			double new_value,
			String fields) throws ActionNotPermittedException, LogicCalculationException {
		
		return setAggregatedValue (measure_name, LogicType.Proportional, new_value, fields);
	}

	@Override
	public int setAggregatedValue (
			String measure_name, 
			PlannificoLogic.LogicType distribution_logic,
			double new_value, 
			String fields) throws ActionNotPermittedException, LogicCalculationException {
		
		PlannificoLogic logic  = factory.getPlanningLogic (distribution_logic);
		
		return logic.apply (this, fields, measure_name, new_value);
	}
	
	/**
	 * Increment the given {@link MeasureSet} measure of the given increment
	 * 
	 * @param measure_set_name
	 * @param measure_name
	 * @param fields
	 * @param propotional_increment
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int incrementMeasureValueBy (
			String measure_set_name,
			String measure_name,
			String fields, 
			double propotional_increment,
			Connection conn)
					throws SQLException {
		
		String where = getWhereForUpdate (fields, measure_set_name);

		String update_stm = 
				"UPDATE " + MEASURE_SET_PREFIX + measure_set_name + 
				" SET VALUE_" + measure_name + " = VALUE_" + measure_name + " * " + propotional_increment + " " +
				where;
		
		PreparedStatement stmt = 
				conn.prepareStatement (update_stm);
		
		int rels_rs = stmt.executeUpdate();
		
		return rels_rs;
	}
	
	/**
	 * Increment the given {@link MeasureSet} measure of the given increment applying a 
	 * floor rounding
	 * 
	 * @param measure_set_name
	 * @param measure_name
	 * @param fields
	 * @param propotional_increment
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws WrongFieldTypeException
	 */
	public static double incrementRoundedMeasureValueBy (
			String measure_set_name,
			String measure_name,
			String fields, 
			double propotional_increment,
			Connection conn)
					throws SQLException, WrongFieldTypeException {
		
		String where = getWhereForUpdate (fields, measure_set_name);

		String update_stm = 
				"UPDATE " + MEASURE_SET_PREFIX + measure_set_name + 
				" SET VALUE_" + measure_name + " = FLOOR(VALUE_" + measure_name + " * " + propotional_increment + ") " +
				where + ";";
		
		PreparedStatement stmt = 
				conn.prepareStatement (update_stm);
		
		stmt.executeUpdate();
		
		double rounded_result = 
				getAggregatedValue(measure_set_name, measure_name, fields, conn).getNumberValue();		
		
		
		return rounded_result;
	}
	
	/**
	 * Distribute the given value on the records identified by the given fields
	 * 
	 * @param measure_set_name
	 * @param measure_name
	 * @param fields
	 * @param value
	 * @param conn
	 * @throws SQLException
	 * @throws WrongFieldTypeException
	 */
	public static void distributeValue (
			String measure_set_name,
			String measure_name,
			String fields, 
			double value,
			Connection conn)
					throws SQLException, WrongFieldTypeException {
		
		
		String where = getWhereForUpdate (fields, measure_set_name);
		
		PreparedStatement stmt = 
				conn.prepareStatement (
						"SELECT *" +
						" FROM " + MEASURE_SET_PREFIX + measure_set_name + " " +
						where, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		ResultSet rels_rs = stmt.executeQuery();
		
		while (rels_rs.next ()) {			
			
			ResultSetMetaData rsmd = rels_rs.getMetaData ();
			
			boolean is_first = true;
			
			String update_where = "WHERE ";
			
			for (int i = 1; i <= rsmd.getColumnCount (); i++) {
				
				if (!rsmd.getColumnName(i).contains("VALUE_")) {
					
					if (is_first) {
						
						update_where += " " + rsmd.getColumnName(i) + " = '" + rels_rs.getString (i) + "'";
					
						is_first = false;
						
					} else {	
						
						update_where += " AND " + rsmd.getColumnName(i) + " = '" + rels_rs.getString (i) + "'";
					}	
				}					
			}
			
			update_where += " LIMIT 1";
			
			PreparedStatement update_stmt = 
					conn.prepareStatement (
							"UPDATE " + MEASURE_SET_PREFIX + measure_set_name + 
							" SET VALUE_" + measure_name + " = VALUE_" + measure_name + " + 1 "+
							update_where);
			
			logger.fine (
					String.format ("updating record: %s", update_where));
			
			int upd_result = update_stmt.executeUpdate ();
			
			/*double current_value = rels_rs.getDouble ("VALUE_" + measure_name);
			
			rels_rs.updateDouble("VALUE_" + measure_name, current_value + 1);
			rels_rs.updateRow();
			*/
			
			value --;
			
			if (value == 0) {				
				
				return;
			}
		}
	}

	/**
	 * Return the aggregated value of the given measure
	 * aggregated at the level specified in the query
	 * 
	 * @param measure_set_name
	 * @param measure_name
	 * @param attributes
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static PlanningField getAggregatedValue (
			String measure_set_name,
			String measure_name,
			String query, 
			Connection conn)
					throws SQLException {
		
		PlanningField return_value = factory.getNullField();
		
		String join_and_where = "";
		
		join_and_where = getJoinAndWhere (query, measure_set_name);

		PreparedStatement stmt = 
				conn.prepareStatement (
						"SELECT SUM(VALUE_" + measure_name + ")" +
						" FROM " + MEASURE_SET_PREFIX + measure_set_name + 
						join_and_where);
		
		ResultSet rels_rs = stmt.executeQuery();
		
		while (rels_rs.next ()) {
			
			double rel = rels_rs.getDouble(1);
			
			return_value = factory.getNumberField (measure_name, rel);
		}
		return return_value;
	}
	
	/**
	 * Return the number of records identified by the given query
	 * 
	 * @param measure_set_name
	 * @param measure_name
	 * @param attributes
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static int getNumberOfRecords (
			String measure_set_name,
			String measure_name,
			String query, 
			Connection conn)
					throws SQLException {
		
		int record_count = 0;
		
		String join_and_where = "";
		
		join_and_where = getJoinAndWhere (query, measure_set_name);

		PreparedStatement stmt = 
				conn.prepareStatement (
						"SELECT COUNT(1)" +
						" FROM " + MEASURE_SET_PREFIX + measure_set_name + 
						join_and_where);
		
		ResultSet rels_rs = stmt.executeQuery();
		
		if (rels_rs.next ()) {
			
			record_count = rels_rs.getInt (1);			
			
		}
		return record_count;
	}
	
	/**
	 * 
	 * @param name2
	 * @param measure_name
	 * @param query
	 * @param value
	 * @param conn
	 * @throws SQLException 
	 */
	public static void applyAValue (String measure_set_name, String measure_name,
			String query, double value, Connection conn) throws SQLException {
		
		String where = getWhereForUpdate (query, measure_set_name);

		String update_stm = 
				"UPDATE " + MEASURE_SET_PREFIX + measure_set_name + 
				" SET VALUE_" + measure_name + " = " + value + " " +
				where;
		
		PreparedStatement stmt;
	
		stmt = conn.prepareStatement (update_stm);
		
		stmt.executeUpdate();		
	}

	private static String getJoinAndWhere (String fields, String measure_set_name) {
		
		String join_and_where;
		String where_clause = "WHERE ";
		String join_clause = "";
		
		List<String> aggregation_eqs = Arrays.asList (fields.split(";"));
		
		boolean is_first_where = true;
		
		HashMap <String, String> dimension_already_in_join = new HashMap<>();
		
		for (String aggregation_eq : aggregation_eqs) {
			
			String[] eq_elements = aggregation_eq.split("=");
			
			String[] field_name_components = eq_elements [0].split("\\.") ;
			
			if (is_first_where) {
				
				where_clause += DIM_PREFIX + 
						field_name_components [0] + DIM_SUFFIX +
						".`" + 
						field_name_components [1] +						
						"` = '" + eq_elements [1] + "'";
				is_first_where = false;				
			}
				
			else 
				where_clause += " AND " + 
						DIM_PREFIX + 
						field_name_components [0] + DIM_SUFFIX +
						".`" + 
						field_name_components [1] +						
						"` = '" + eq_elements [1] + "'";
			
			join_clause += 
					RelationalPlanningSet.buildJoin (
							dimension_already_in_join, 
							measure_set_name,
							field_name_components [0]);
			
			dimension_already_in_join.put(field_name_components [0], field_name_components [0]);
		}				
		
		join_and_where = join_clause + " " + where_clause + ";";
		return join_and_where;
	}
	
	private static String getWhereForUpdate (String fields, String measure_set_name) {
		
		String where_clause = "WHERE ";
		
		List<String> aggregation_eqs = Arrays.asList (fields.split(";"));
		
		boolean is_first_where = true;
		
		for (String aggregation_eq : aggregation_eqs) {
			
			String[] eq_elements = aggregation_eq.split("=");
			
			String[] field_name_components = eq_elements [0].split("\\.") ;
			
			if (is_first_where) {
				
				where_clause += getWhereForUpdateClause (eq_elements, field_name_components);
				
				is_first_where = false;				
			}
				
			else 
				where_clause += " AND " + getWhereForUpdateClause (eq_elements, field_name_components);
			
		}						
		
		return where_clause;
	}

	private static String getWhereForUpdateClause (String[] eq_elements,
			String[] field_name_components) {
		
		return field_name_components [0] + 
				" in (SELECT " + field_name_components [0] + 
				" FROM " + RelationalMeasureSet.DIM_PREFIX + field_name_components [0] + RelationalMeasureSet.DIM_SUFFIX +  
				" WHERE `" + field_name_components [1] + "` = '" + eq_elements [1] + "')";
	}

	@Override
	public PlanningSet getDataSet (String measures, String filter, String groupby) throws WrongQuerySintax {
		
		PlanningSet dataset = factory.createPlanningSet (measures, filter, groupby);
		
		try {
			
			((RelationalPlanningSet)dataset)
				.populateFromRelationalDB (provideAConnection(), this.planningUniverse, this.name);
			
		} catch (SQLException e) {
			
			logger.warning (String.format ("Error retrieving the PlanningSet %s", e.getMessage()));
		}
		
		return dataset;
	}	

}
