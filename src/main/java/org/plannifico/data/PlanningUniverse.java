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

import java.util.Collection;
import java.util.Map;

import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ActionNotPermittedException;
//import org.plannifico.server.logic.DistributionLogic;


public interface PlanningUniverse {	
	
	/**
	 * Return true if the {@link PlanningUniverse} is loaded
	 * 
	 * @return
	 */
	public boolean isLoadedFromSource ();
	
	/**
	 * Load a {@link PlanningUniverse} from a source for the first time
	 * 
	 * @return
	 */
	public boolean loadFromSource ();
	
	/**
	 * Populate the universe from the implementation data management system
	 */
	void populate();

	/**
	 * Return the universe name
	 * @return
	 */
	public String getName ();
	
	/**
	 * Return a planning record by its key for the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * @return
	 *
	 * @throws WrongPlanningRecordKey 
	 */
	public PlanningRecord getRecordByKey (
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
			String measure_set_name,
			String measure_name, 
			String fields) throws ActionNotPermittedException;
	

	/**
	 * Set an aggregated value for the given measure in the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * @param universe_name
	 * @param new_value
	 * @param fields
	 * @return 
	 * @throws ActionNotPermittedException 
	 * @throws LogicCalculationException 
	 */
	public int setAggregatedValue (
			String measure_set_name,
			String measure_name,
			double new_value,
			String fields) throws ActionNotPermittedException, LogicCalculationException;
	
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
	int setAggregatedValue(
			String measure_set_name, 
			String measure_name,
			LogicType distribution_logic, 
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
			String measure_set_name,
			String fields) throws ActionNotPermittedException;
	
	
	/**
	 * Return the {@link PlanDataset} number of records contained in 
	 * the given {@link PlanningUniverse}
	 * @return TODO
	 * @throws ActionNotPermittedException 
	 * @throws UniverseNotExistException 
	 */
	public long getMasureSetRecordsNumber (
			String measure_set_name) 
			throws ActionNotPermittedException, UniverseNotExistException;

	/**
	 * Return the measures' names contained in  the {@link PlanningUniverse} 
	 * and the Masure Set
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getMeasureSetsMeasureNames (
			String measure_set_name) throws UniverseNotExistException;
	
	/**
	 * Return the dimensions contained in  the {@link PlanningUniverse}
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getPlanningDimensions () throws UniverseNotExistException;
	

	/**
	 * Return the dimension attributes contained in  the {@link PlanningUniverse}
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection<String> getDimensionAttribute (String dimension);
	
	/**
	 * Return the dimension relationships in  the {@link PlanningUniverse} for the
	 * given dimension and the given dimension key
	 * 
	 * @param instance_name
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getPlanningDimensionRelationship (
			String dimension,
			String dimension_key
			) throws UniverseNotExistException;

	
	/**
	 * Return the number of Planning Universe
	 * @return
	 */
	public int getMeasureSetsNumber ();
	
	/**
	 * Return the {@link MeasureSet} names contained in 
	 * the {@link PlanningUniverse}
	 * 
	 * @return
	 */
	public Collection<String> getMeasureSetsName ();

	/**
	 * Return the given dimension relationships
	 * 
	 * @return
	 */
	public Map<String, Collection<String>> getAllDimensionRelationships (String dimension);
	
	/**
	 * Return the elements of the given attribute
	 * @param dimension
	 * @param attribute
	 * @return
	 */
	public Collection<String>  getDimensionAttributeElements(
			String dimension, String attribute);


	
	/**
	 * Return a planning set calculated on the {@link MeasureSet}
	 * aggregating the given measures by the given groupby fields filtering the records on the
	 * given filter
	 * 
	 * @param universe
	 * @param measureset
	 * @param measures
	 * @param filter
	 * @param groupby
	 * @return
	 * @throws WrongQuerySintax 
	 */
	public PlanningSet getDataSet(String measureset, String measures,
			String filter, String groupby) throws WrongQuerySintax;


	
}
