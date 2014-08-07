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
public class RelationalProportionalLogic extends ProportionalLogic {

	private PlannificoFactory factory = PlannificoFactoryProvider.getInstance();
	
	private final static Logger logger = Logger.getLogger (RelationalProportionalLogic.class.getName());


	@Override
	protected double applyProportionalIncrement (
			MeasureSet measure_set,
			String measure_name,
			double proportional_increment, 
			String query) {
		
		Connection conn = null;
		
		int update_return = 0;
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {
				
				conn = ((RelationalMeasureSet) measure_set).provideAConnection();
				
				update_return = RelationalMeasureSet.incrementMeasureValueBy (
						measure_set.getName(),
						measure_name, 
						query,
						proportional_increment,
						conn);
				
				
			} catch (SQLException e) {
				
				update_return = -1;
				
				logger.warning(
						String.format ("Error applying increment for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
			}
		}	
		
		return update_return;
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
						String.format ("Error getting total sum for %s for measure %s : %s", 
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
			
			logger.warning(
					String.format ("Error calculating total sum for %s for measure %s : %s", 
							measure_set.getName(), 
							measure_name,
							"Not a relational logic"));
			
			throw new LogicCalculationException (
					String.format ("Error calculating Total for %s for measure %s", 
					measure_set.getName(), 
					measure_name));		
		}
			
		
		try {
			
			return return_value.getNumberValue();
			
		} catch (WrongFieldTypeException e) {
			
			logger.warning(
					String.format ("Error getting total sum for %s for measure %s : %s", 
							measure_set.getName(), 
							measure_name,
							e.getMessage()));
			
			throw new LogicCalculationException (
					String.format ("Error calculating Total for %s for measure %s : %s", 
					measure_set.getName(), 
					measure_name, e.getMessage()));	
		}
	}

}
