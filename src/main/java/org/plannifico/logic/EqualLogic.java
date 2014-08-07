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
package org.plannifico.logic;

import org.plannifico.data.MeasureSet;


/**
 * An implementation of {@link PlannificoLogic} that apply to the measure of the given records
 * a new value equally among the records
 * 
 * @author ralfano
 *
 */
public abstract class EqualLogic implements PlannificoLogic {

	@Override
	public int apply (
		MeasureSet measure_set,
		String query, 
		String measure_name,
		double new_value) throws LogicCalculationException {
		
		double record_numbers = getNumberOfRecords  (measure_set, measure_name, query);
		
		double remainder = new_value % record_numbers;
		
		double value = (new_value - remainder) / record_numbers;		
		
		applyAValue (measure_set, measure_name, value, query);
		
		if (remainder > 0 ) 
			applyRemainder (measure_set, measure_name, remainder, query);
		
		return 0;
	}

	protected abstract void applyAValue (
			MeasureSet measure_set, 
			String measure_name, 
			double value,
			String query) throws LogicCalculationException;

	protected abstract int getNumberOfRecords (
			MeasureSet measure_set, 
			String measure_name, 
			String query) throws LogicCalculationException;

	protected abstract void applyRemainder(
			MeasureSet measure_set, 
			String measure_name,
			double remainder, 
			String query) throws LogicCalculationException;

}
