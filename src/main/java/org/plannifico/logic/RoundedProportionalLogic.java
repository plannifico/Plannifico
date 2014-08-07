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
 * a new value proportionally rounding its value
 * 
 * @author ralfano
 *
 */
public abstract class RoundedProportionalLogic implements PlannificoLogic {

	@Override
	public int apply (
		MeasureSet measure_set,
		String query, 
		String measure_name,
		double new_value) throws LogicCalculationException {
		
		double current_total = getTotalSum  (measure_set, measure_name, query);
		
		double proportional_increment = new_value / current_total;
		
		double floor_value = 
				applyProportionalIncrement (measure_set, measure_name, proportional_increment, query);
		
		if ((new_value - floor_value) > 0) {
		
			applyRemainder (measure_set, 
				 measure_name, 
				 new_value - floor_value,
				 query);
		}
			
		return 0;
	}

	protected abstract double applyProportionalIncrement (
			MeasureSet measure_set, 
			String measure_name, 
			double proportional_increment,
			String query) throws LogicCalculationException;

	protected abstract double getTotalSum (
			MeasureSet measure_set, 
			String measure_name, 
			String query) throws LogicCalculationException;
	
	protected abstract void applyRemainder (
			MeasureSet measure_set, 
			String measure_name, 
			double remaining,
			String query) throws LogicCalculationException;

}
