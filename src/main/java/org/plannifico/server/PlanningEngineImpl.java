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
package org.plannifico.server;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.PlanningSet;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.UniverseNotExistException;
import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.WrongQuerySintax;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.configuration.ConfigurationManager;


public class PlanningEngineImpl implements PlanningEngine {
	
	private static final int MAX_THREADS = 20;


	private PlannificoFactory factory;
	private ConfigurationManager configurationManager;
	
	private boolean isRunning = false;
	
	private Map <String, PlanningUniverse> planningUniverses = new HashMap<>();
	
	private ConnectionPoolProvider connectionPoolProvider;
	
	private final static Logger logger = Logger.getLogger (PlanningEngineImpl.class.getName());


	private static final int DB_CONNECTION_POOL = 10;	

	@Override
	public int start (String configuration_file)
			throws ServerAlreadyRunningException {
		
		if (isRunning) throw new ServerAlreadyRunningException();
		
		logger.log(Level.INFO, "Server starting... [START]");
    	
    	factory = PlannificoFactoryProvider.getInstance();
    	
    	configurationManager = factory.getConfigurationManager();
    	
    	if (!configurationManager.loadConfiguration (configuration_file))
    		return 1;
    	
    	logger.log(Level.INFO, "Configuration load [DONE]");
    	    	
    	//Connection conn_working_data;
    	
    	connectionPoolProvider = factory.getConnectionPoolProvider();
    	
    	Set <String> universes = configurationManager.getPlanningUniverses();
    	
    	for (String universe_name : universes) {
    	
    		/*int db_connections = DB_CONNECTION_POOL;
    		
    		while (db_connections-- > 0) {
    			*/
            	if(!connectionPoolProvider.addConnectionPool (
            			universe_name,
            			configurationManager.getDatabaseURL (universe_name), 
            			configurationManager.getPlanningDataDBUser (universe_name), 
            			configurationManager.getPlanningDataDBPwd (universe_name)))
            		
            		return 1;
    		//}
    			
    	}
    	
    	logger.log(Level.INFO, "Populating Planning Universes...");
    	
    	try {
		
    		for (String universe_name : universes) {    		
    			
    			connectionPoolProvider.getConnection (universe_name);
    			
    			PlanningUniverse universe = factory.getPlanningUniverse (universe_name);
            	
            	planningUniverses.put (universe_name, universe);
    		}    	    	
    		
    		
		} catch (SQLException e) {
			
			logger.log(Level.SEVERE, "Error Populating Planning Universes: " + e.getMessage());

			return 1;			
		}

    	
		//= new HashMap <String, PlanningUniverse> ();
    	    	
		logger.log(Level.INFO, "Populating Planning Universes [DONE]");
		
    	logger.log(Level.INFO, "Server starting [DONE]");

    	isRunning = true;
    	
		return 0;
	}

	@Override
	public int stop () {
		
		isRunning = false;
			
		connectionPoolProvider.close ();			
		
		return 0;
	}

	@Override
	public int getStatus() {
		
		if (isRunning) return PlanningEngine.STARTED;
	
		else return PlanningEngine.STOPPED;
	}

	@Override
	public ExecutorService getThreadsPool() {
		
		return Executors.newFixedThreadPool (MAX_THREADS);
	}

	@Override
	public Collection<String> getUniverses () {
		
		return planningUniverses.keySet();
	}
	
	@Override
	public PlanningRecord getRecordByKey (String universe_name,
			String measure_set_name, String key)
			throws WrongPlanningRecordKey {
		
		return planningUniverses.get(universe_name).
				getRecordByKey (measure_set_name, key);
	}

	@Override
	public PlanningField getAggregatedValue (String universe_name,
			String measure_set_name, String measure_name, String fields)
			throws ActionNotPermittedException {
		
		return planningUniverses.get(universe_name).
				getAggregatedValue (measure_set_name, measure_name, fields);
	}

	@Override
	public int setAggregatedValue(String universe_name,
			String measure_set_name, String measure_name, double new_value,
			String fields) throws ActionNotPermittedException, LogicCalculationException {
		
		return planningUniverses.get(universe_name).
				setAggregatedValue (measure_set_name, measure_name, new_value, fields);
	}

	@Override
	public int setAggregatedValue (String universe_name,
			String measure_set_name, String measure_name,
			LogicType distribution_logic, double new_value, String fields)
			throws ActionNotPermittedException, LogicCalculationException {

		return planningUniverses.get(universe_name).
				setAggregatedValue (measure_set_name, measure_name, distribution_logic, new_value, fields);
	}

	@Override
	public Collection<PlanningRecord> getRecordsByAggregation (
			String universe_name, String measure_set_name, String fields)
			throws ActionNotPermittedException {
		
		return planningUniverses.get(universe_name).
				getRecordsByAggregation (measure_set_name, fields);
	}

	@Override
	public long getMasureSetRecordsNumber (String universe_name,
			String measure_set_name) throws ActionNotPermittedException,
			UniverseNotExistException {
		
		return planningUniverses.get(universe_name).
				getMasureSetRecordsNumber (measure_set_name);
	}

	@Override
	public Collection<String> getMeasureSetsMeasureNames(String universe_name,
			String measure_set_name) throws UniverseNotExistException {
		
		return planningUniverses.get(universe_name).
				getMeasureSetsMeasureNames (measure_set_name);
	}

	@Override
	public Collection<String> getPlanningDimensions (String universe_name) 
			throws UniverseNotExistException {
		
		return planningUniverses.get(universe_name).getPlanningDimensions ();
	}


	@Override
	public Collection<String> getDimensionAttributes(String universe_name,
			String dimension_name) throws UniverseNotExistException {
		
		return planningUniverses.get (universe_name).getDimensionAttribute (dimension_name);
		
	}
	
	@Override
	public Map<String, Collection<String>>  getAllDimensionAttributes(String universe_name)
			throws UniverseNotExistException {
		
		HashMap <String, Collection<String>> dims_attributes = new HashMap<String, Collection<String>>();
		
		Collection<String> dimensions = planningUniverses.get(universe_name).getPlanningDimensions ();
		
		for (String dimension:dimensions) {
			
			Collection<String> attributes = getDimensionAttributes(universe_name, dimension);
			
			dims_attributes.put(dimension, attributes);
		}
		
		return dims_attributes;
	}
	
	@Override
	public Collection<String> getPlanningDimensionRelationship (
			String universe_name, String dimension, String dimension_key)
			throws UniverseNotExistException {		
		
		return planningUniverses.get(universe_name).
			getPlanningDimensionRelationship(dimension,dimension_key);
	}

	@Override
	public int getMeasureSetsNumber (String universe_name) {
				
		return planningUniverses.get (universe_name).getMeasureSetsNumber();
	}
	

	@Override
	public Collection<String> getMeasureSetsNames(String universe_name) {
		
		if (!planningUniverses.containsKey (universe_name))
			return new ArrayList<>();
		
		return planningUniverses.get(universe_name).getMeasureSetsName();
	}

	@Override
	public Collection<String> getDimensionAttributeElements(
			String universe_name, String dimension, String attribute)
			throws UniverseNotExistException {
		
		if (!planningUniverses.containsKey (universe_name))
			throw new UniverseNotExistException();
		
		return planningUniverses.get(universe_name).getDimensionAttributeElements (dimension, attribute);
	}
	
	@Override
	public Map<String, Collection<String>> getAllDimensionRelationships (
			String universe, String dimension) throws UniverseNotExistException {
		
		if (!planningUniverses.containsKey (universe))
			throw new UniverseNotExistException();
		
		return planningUniverses.get(universe).getAllDimensionRelationships (dimension);
	}
	
	

	@Override
	public PlanningSet getDataSet(String universe, 
			String measureset,
			String measures, String filter, String groupby) throws UniverseNotExistException, WrongQuerySintax {
		
		if (!planningUniverses.containsKey (universe))
			throw new UniverseNotExistException();
		
		return planningUniverses.get(universe).getDataSet (measureset, measures, filter, groupby);
	}

}
