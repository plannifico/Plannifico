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
package org.plannifico;



import org.plannifico.data.MeasureSet;
import org.plannifico.data.PlanningSet;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.RelationalMeasureSet;
import org.plannifico.data.RelationalPlanningSet;
import org.plannifico.data.RelationalPlanningUniverse;
import org.plannifico.data.fields.NullField;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.TextField;
import org.plannifico.data.records.EmptyPlanningRecord;
import org.plannifico.data.records.FactPlanningRecord;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.data.records.RelationalFactPlanningRecord;
import org.plannifico.logic.PlannificoLogic;
import org.plannifico.logic.RelationalBasicLogic;
import org.plannifico.logic.RelationalEqualLogic;
import org.plannifico.logic.RelationalProportionalLogic;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.logic.RelationalRoundedProportionalLogic;
import org.plannifico.server.ConnectionPoolProvider;
import org.plannifico.server.H2ConnectionPoolProvider;
import org.plannifico.server.PlanningEngine;
import org.plannifico.server.PlanningEngineImpl;
import org.plannifico.server.configuration.ConfigurationManager;
import org.plannifico.server.configuration.XMLBasedConfigurationManager;


/**
 * PlannificoFactory implementation
 * 
 * @author Rosario Alfano
 *
 */
public class PlannificoFactoryImpl implements PlannificoFactory {

	@Override
	public ConfigurationManager getConfigurationManager() {		
		
		return new XMLBasedConfigurationManager ();
	}

	@Override
	public PlanningEngine getPlanningEngine() {
		
		return new PlanningEngineImpl();
	}

	@Override
	public ConnectionPoolProvider getConnectionPoolProvider() {
		
		return new H2ConnectionPoolProvider();
	}

	@Override
	public PlanningUniverse getPlanningUniverse (String universe_name) {
		
		return new RelationalPlanningUniverse (universe_name);
	}

	@Override
	public MeasureSet getMeasureSet (String planning_univese, String name) {
		
		return new RelationalMeasureSet (planning_univese, name);
	}

	@Override
	public PlanningField getNumberField(String measure_name, double value) {
		
		return new NumberField (measure_name, value);
	}
	
	@Override
	public PlanningField getTextField (String field_name, String value) {
		
		return new TextField (field_name, value);
	}

	@Override
	public PlanningField getNullField() {
		
		return new NullField();
	}

	@Override
	public PlanningRecord getFactPlanningRecord (String universe_name, String measure_set_name) {
		
		return new RelationalFactPlanningRecord (universe_name, measure_set_name);
	}

	@Override
	public PlanningRecord getEmptyPlanningRecord() {
		
		return new EmptyPlanningRecord ();
	}

	@Override
	public PlannificoLogic getPlanningLogic (LogicType distribution_logic) {
		
		switch (distribution_logic) {
		
	        case Proportional:
	            return new RelationalProportionalLogic();
	                
	        case Equal:
	        	return new RelationalEqualLogic(new RelationalBasicLogic ());
	           
	         
	        case RoundedProportional:
	        	return new RelationalRoundedProportionalLogic(new RelationalBasicLogic ());
	        	
	        default:
	        	return new RelationalRoundedProportionalLogic (new RelationalBasicLogic ());
		}
		
	}

	@Override
	public PlanningSet createPlanningSet(String measures, String filter,
			String groupby) {
		
		return new RelationalPlanningSet (measures, filter, groupby);
	}

}
