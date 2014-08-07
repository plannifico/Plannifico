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

import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ActionNotPermittedException;
//import org.plannifico.server.logic.DistributionLogic;


public interface MeasureSet {
	
	/**
	 * Return the {@link MeasureSet} name
	 * @return
	 */
	public String getName ();
	
	/**
	 * Return a planning record by its key for the given universe and measure set
	 * accepting a strings as key with the following syntax: field1=value1;...;fieldn=valuen
	 * 
	 * @return
	 * @throws WrongPlanningRecordKey 
	 */
	public PlanningRecord getRecordByKey (
			String key) 
			throws WrongPlanningRecordKey;
	
	/**
	 * Return the aggregated value of the measure at the given aggregation for 
	 * the given universe and measure set 
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * 
	 * @param measure_name
	 * @param fields
	 * @return
	 * @throws ActionNotPermittedException 
	 */
	public PlanningField getAggregatedValue (
			String measure_name, 
			String fields) throws ActionNotPermittedException;
	

	/**
	 * Set an aggregated value for the given measure in the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * 
	 * @param new_value
	 * @param fields
	 * @return 
	 * @throws ActionNotPermittedException 
	 * @throws LogicCalculationException 
	 */
	public int setAggregatedValue (
			String measure_name,
			double new_value,
			String fields) throws ActionNotPermittedException, LogicCalculationException;
	
	/**
	 * Set an aggregated value for the given measure in the given universe and measure set
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 * using the given {@link DistributionLogic}
	 * 
	 * @param new_value
	 * @param fields
	 * @return 
	 * @throws ActionNotPermittedException 
	 * @throws LogicCalculationException 
	 */

	int setAggregatedValue(
			String measure_name, 
			LogicType distribution_logic,
			double new_value, 
			String fields) throws ActionNotPermittedException, LogicCalculationException;

	
	/**
	 * Return the records under the given aggregation
	 * accepting a strings as key with the following syntax: dimension1.field1=value1;...;dimensionn.fieldn=valuen
	 *  
	 * @param fields
	 * @return
	 * @throws ActionNotPermittedException
	 */
	public Collection <PlanningRecord> 
		getRecordsByAggregation (String fields) throws ActionNotPermittedException;
	
	
	/**
	 * Return the {@link PlanningRecord} number contained in the {@link MeasureSet}
	 * @return TODO
	 * @throws ActionNotPermittedException 
	 * @throws UniverseNotExistException 
	 */
	public long getMasureSetRecordsNumber () 
			throws ActionNotPermittedException, UniverseNotExistException;

	/**
	 * Return the measures' names contained in the {@link MeasureSet}
	 *	 
	 * @return
	 * @throws InstanceNotExistException 
	 */
	public Collection <String> getMeasureSetsMeasureNames () throws UniverseNotExistException;

	

}
