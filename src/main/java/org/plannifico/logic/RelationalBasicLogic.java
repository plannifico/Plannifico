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

import org.plannifico.data.MeasureSet;
import org.plannifico.data.RelationalMeasureSet;
import org.plannifico.data.fields.WrongFieldTypeException;

public class RelationalBasicLogic {
	
	private final static Logger logger = 
			Logger.getLogger (RelationalBasicLogic.class.getName());
	

	protected double applyProportionalIncrement  (
			MeasureSet measure_set,
			String measure_name,
			double proportional_increment, 
			String query) throws LogicCalculationException {
		
		Connection conn = null;
		
		double floor_value = 0;
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {
				
				conn = ((RelationalMeasureSet) measure_set).provideAConnection();
				
				floor_value = RelationalMeasureSet.incrementRoundedMeasureValueBy (
						measure_set.getName(),
						measure_name, 
						query,
						proportional_increment,
						conn);
				
				
			} catch (SQLException | WrongFieldTypeException e) {
				
				logger.warning(
						String.format ("Error applying increment for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
				throw new LogicCalculationException (
						String.format ("Error applying proportinal increment for measure %s: %s", 
						measure_set.getName(), 
						measure_name,
						e.getMessage()));
			}
		}	
		
		return floor_value;
	}


	protected void applyRemainder (
			MeasureSet measure_set, 
			String measure_name, 
			double remainder,
			String query) throws LogicCalculationException {
		
		Connection conn = null;
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {				
				conn = ((RelationalMeasureSet) measure_set).provideAConnection();
				
				RelationalMeasureSet.distributeValue (
						measure_set.getName(),
						measure_name, 
						query,
						remainder,
						conn);
				
				
			} catch (SQLException | WrongFieldTypeException e) {
				
				logger.warning(
						String.format ("Error applying reminder for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
				throw new LogicCalculationException (
						String.format ("Error applying reminder for measure %s: %s", 
						measure_set.getName(), 
						measure_name,
						e.getMessage()));
			}
		}	
		
	}


	public void applyAValue(MeasureSet measure_set, String measure_name,
			double value, String query) {
		
		Connection conn = null;
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {				
				conn = ((RelationalMeasureSet) measure_set).provideAConnection();
				
				RelationalMeasureSet.applyAValue (
						measure_set.getName(),
						measure_name, 
						query,
						value,
						conn);
				
				
			} catch (SQLException e) {
				
				logger.warning(
						String.format ("Error applying equal value for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
			}
		}	
	}

}
