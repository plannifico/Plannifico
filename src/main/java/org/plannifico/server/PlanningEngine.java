/*Copyright 2013 Rosario Alfano

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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.plannifico.data.MeasureSet;
import org.plannifico.data.PlanningSet;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.UniverseNotExistException;
import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
//import org.plannifico.server.logic.DistributionLogic;


/**
 * Interface implemented by an object capable to run planning operations
 * 
 * @author Rosario Alfano
 * @version 0.1
 *
 */
public interface PlanningEngine {
	
	public final static int STARTED = 1;
	public final static int STOPPED = 0;

	/**
	 * Return a planning record by its key for the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * @return

	 * @throws WrongPlanningRecordKey 
	 */
	public PlanningRecord getRecordByKey (
			String universe_name, 
			String measure_set_name, 
			String key) 
			throws WrongPlanningRecordKey;


	
	/**
	 * Return the aggregated value of the measure at the given aggregation for 
	 * the given universe and measure set 
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * @param measure_name
	 * @param fields
	 * @return
	 * @throws ActionNotPermittedException 
	 */
	public PlanningField getAggregatedValue (
			String universe_name, 
			String measure_set_name,
			String measure_name, 
			String fields) throws ActionNotPermittedException;
	

	/**
	 * Set an aggregated value for the given measure in the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * @param universe_name
	 * @param new_value
	 * @param query
	 * @return 
	 * @throws ActionNotPermittedException 
	 * @throws LogicCalculationException 
	 */
	public int setAggregatedValue (
			String universe_name,
			String measure_set_name,
			String measure_name,
			double new_value,
			String query) throws ActionNotPermittedException, LogicCalculationException;
	
	/**
	 * Set an aggregated value for the given measure in the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * using the given {@link DistributionLogic}
	 * @param universe_name
	 * @param new_value
	 * @param fields
	 * @return 
	 * @throws ActionNotPermittedException 
	 * @throws LogicCalculationException 
	 */
	public int setAggregatedValue(
			String universe_name, 
			String measure_set_name,
			String measure_name, 
			LogicType allocation_logic,
			double new_value, 
			String fields) 
					throws ActionNotPermittedException, LogicCalculationException;


	/**
	 * Return the records under the given aggregation
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * 
	 * @param universe_name
	 * @param fields
	 * @return
	 * @throws ActionNotPermittedException
	 */
	public Collection <PlanningRecord> getRecordsByAggregation (
			String universe_name,
			String measure_set_name,
			String fields) throws ActionNotPermittedException;

	
	/**
	 * Start a {@link PlanningEngine}  
	 * 
	 * @return 0 if the server starts correctly
	 * 
	 * @throws ServerAlreadyRunningException 
	 */
	public int start (String core_configuration_file) 
			throws ServerAlreadyRunningException;
	
	/**
	 * Stop a {@link PlanningEngine}
	 * @return 0 if the server stops correctly
	 */
	public int stop ();
	
	/**
	 * Get the status of the {@link PlanningEngine}
	 * @return
	 */
	public int getStatus ();
	
	
	/**
	 * Return the {@link PlanDataset} number of records contained in 
	 * the given {@link PlanningUniverse}
	 * @return TODO
	 * @throws ActionNotPermittedException 
	 * @throws UniverseNotExistException 
	 */
	public long getMasureSetRecordsNumber (
			String universe_name, 
			String measure_set_name) 
			throws ActionNotPermittedException, UniverseNotExistException;

	/**
	 * Return the measureset names contained in  the {@link PlanningUniverse}
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getMeasureSetsNames (
			String universe_name) ;
	
	/**
	 * Return the measures' names contained in  the {@link PlanningUniverse} 
	 * and the Masure Set
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getMeasureSetsMeasureNames (
			String universe_name, 
			String measure_set_name) throws UniverseNotExistException;
	
	/**
	 * Return the dimensions contained in  the {@link PlanningUniverse}
	 * 
	 * @param universe_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getPlanningDimensions (
			String universe_name) throws UniverseNotExistException;
	
	/**
	 * Return the dimension attributes contained in  the {@link PlanningUniverse}
	 * 
	 * @param universe_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getDimensionAttributes (
			String universe_name, String dimension_name) throws UniverseNotExistException;
	
	/**
	 * Return the dimension attributes contained in  the {@link PlanningUniverse} for all the dimensions
	 * 
	 * @param universe_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Map<String, Collection<String>>  getAllDimensionAttributes (
			String universe_name) throws UniverseNotExistException;
	
	/**
	 * Return the dimension attributes contained in  the {@link PlanningUniverse}
	 * 
	 * @param universe_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection<String>  getDimensionAttributeElements (
			String universe_name, String dimension, String attribute) throws UniverseNotExistException;
	
	/**
	 * Return the dimension relationships in  the {@link PlanningUniverse} for the
	 * given dimension and the given dimension key
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getPlanningDimensionRelationship (
			String universe_name,
			String dimension,
			String dimension_key
			) throws UniverseNotExistException;

	/**
	 * Return the universes loaded into the PlanningEngine
	 *  
	 *  @return
	 */
	public Collection <String> getUniverses ();

	
	/**
	 * Return the thread pool to use to run server jobs
	 * 
	 * @return
	 */
	public ExecutorService getThreadsPool();
	
	/**
	 * Return the number of Planning Universe
	 * @return
	 */
	public int getMeasureSetsNumber (String universe_name);

	/**
	 * Return the given dimension relationships
	 * 
	 * @param universe
	 * @param dimension
	 * @return
	 * @throws UniverseNotExistException 
	 */
	public Map<String, Collection<String>> getAllDimensionRelationships (
			String universe,
			String dimension) throws UniverseNotExistException;

	
	/**
	 * Return a planning set calculated on the {@link MeasureSet} of the given {@link PlanningUniverse}
	 * aggregating the given measures by the given groupby fields filtering the records on the
	 * given filter
	 * 
	 * @param universe
	 * @param measureset
	 * @param measures
	 * @param filter
	 * @param groupby
	 * @return
	 * @throws UniverseNotExistException 
	 */
	public PlanningSet getDataSet (
		String universe, 
		String measureset,
		String measures, 
		String filter, 
		String groupby) throws UniverseNotExistException;
		
}
