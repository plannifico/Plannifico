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

/**
 * A Relational DB - based implementation of the {@link EqualLogic}
 * 
 * @author ralfano
 *
 */
public class RelationalEqualLogic extends EqualLogic {
	
	private final static Logger logger = 
			Logger.getLogger (RelationalEqualLogic.class.getName());
	
	private RelationalBasicLogic basicLogic;

	public RelationalEqualLogic(RelationalBasicLogic basic_logic) {
		
		basicLogic = basic_logic;
	}

	@Override
	protected void applyAValue (MeasureSet measure_set,
			String measure_name, double value, String query) {		
		
		basicLogic.applyAValue(
				measure_set, 
				measure_name, 
				value, 
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

	@Override
	protected int getNumberOfRecords (MeasureSet measure_set,
			String measure_name, String query) throws LogicCalculationException {
		
		Connection conn = null;
		
		int record_count = 0;
		
		if (measure_set instanceof RelationalMeasureSet) {
			
			try {
				
				conn = ((RelationalMeasureSet) measure_set).provideAConnection();
				
				record_count = RelationalMeasureSet.getNumberOfRecords (
						measure_set.getName(),
						measure_name, 
						query,
						conn);
				
				
			} catch (SQLException e) {
	
				logger.warning(
						String.format ("Error getting number of records for %s for measure %s : %s", 
								measure_set.getName(), 
								measure_name,
								e.getMessage()));
				
				throw new LogicCalculationException (
						String.format ("Error calculating Total for %s for measure %s", 
						measure_set.getName(), 
						measure_name));	
			}
		}	
		
		return record_count;
	}

}
