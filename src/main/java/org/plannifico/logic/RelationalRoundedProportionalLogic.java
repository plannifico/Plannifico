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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.MeasureSet;
import org.plannifico.data.RelationalMeasureSet;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.WrongFieldTypeException;

/**
 * Implement the {@link ProportionalLogic} based on a Relational db
 * 
 * @author ralfano
 *
 */
public class RelationalRoundedProportionalLogic extends RoundedProportionalLogic {

	protected PlannificoFactory factory = PlannificoFactoryProvider.getInstance();

	private RelationalBasicLogic basicLogic;
	
	private final static Logger logger = Logger.getLogger (RelationalRoundedProportionalLogic.class.getName());

	public RelationalRoundedProportionalLogic (RelationalBasicLogic basic_logic) {
		
		basicLogic = basic_logic;
		
	}

	@Override
	protected double getTotalSum (
			MeasureSet measure_set,
			String measure_name, 
			String query) throws LogicCalculationException {
		
		Connection conn = null;
		
		PlanningField return_value = factory.getNullField();
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {
				
				conn = ((RelationalMeasureSet)measure_set).provideAConnection();
				
				return_value = RelationalMeasureSet.getAggregatedValue (
						measure_set.getName(),
						measure_name, 
						query,
						conn);
				
			} catch (SQLException e) {
	
				logger.warning(
						String.format ("Error calculating Total for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
				throw new LogicCalculationException (String.format ("Error calculating Total for %s for measure %s : %s", 
						measure_set.getName(), 
						measure_name,
						e.getMessage()));
			}
		}
		else {
			
			throw new LogicCalculationException (
					String.format ("Not a relational measure set for measure %s", 
					measure_set.getName(), 
					measure_name));	
		}			
		
		try {
			
			return return_value.getNumberValue();
			
		} catch (WrongFieldTypeException e) {
			
			throw new LogicCalculationException (
					String.format ("Wrong field type for measure %s", 
					measure_set.getName(), 
					measure_name));
		}
	}
	
	@Override
	protected double applyProportionalIncrement (
			MeasureSet measure_set,
			String measure_name,
			double proportional_increment, 
			String query) throws LogicCalculationException {
		
	
		return basicLogic.applyProportionalIncrement(
				measure_set, 
				measure_name, 
				proportional_increment, 
				query);
	}

	@Override
	protected void applyRemainder (
			MeasureSet measure_set, 
			String measure_name, 
			double remainder,
			String query) throws LogicCalculationException {
		
		basicLogic.applyRemainder(measure_set, measure_name, remainder, query);
		
	}
}
