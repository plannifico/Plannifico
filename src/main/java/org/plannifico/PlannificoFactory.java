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

import java.awt.TextField;

import org.plannifico.data.MeasureSet;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.PlannificoLogic;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ConnectionPoolProvider;
import org.plannifico.server.PlanningEngine;
import org.plannifico.server.configuration.ConfigurationManager;


public interface PlannificoFactory {
	
	/**
	 * Return an instance of the {@link ConfigurationManager}
	 * 
	 * @return
	 */
	public ConfigurationManager getConfigurationManager ();

	/**
	 * Return an instance of a {@link PlanningEngine}
	 * 
	 * @return
	 */
	public PlanningEngine getPlanningEngine();
	
	/**
	 * Return an instance of a {@link ConnectionPoolProvider}
	 * 
	 * @return
	 */
	public ConnectionPoolProvider getConnectionPoolProvider ();

	/**
	 * Return an instance of a {@link PlanningUniverse}
	 * @param universe_name 
	 * 
	 * @return
	 */
	public PlanningUniverse getPlanningUniverse (String universe_name);

	/**
	 * Return an instance of a {@link MeasureSet}
	 * 
	 * @param name
	 * @param string 
	 * @return
	 */
	public MeasureSet getMeasureSet (String planning_univese, String name);

	/**
	 * Return an instance of {@link NumberField}
	 * 
	 * @param measure_name
	 * @param rel
	 * @return
	 */
	public PlanningField getNumberField(String measure_name, double rel);
	
	/**
	 * Return an instance of {@link TextField}
	 * 
	 * @param field_name
	 * @param value
	 * @return
	 */
	public PlanningField getTextField(String field_name, String value);

	/**
	 * Return an instance of the Null Field
	 * @return
	 */
	public PlanningField getNullField ();


	/**
	 * Return an EmptyPlanningRecord
	 * 
	 * @return
	 */
	public PlanningRecord getEmptyPlanningRecord();

	/**
	 * Return a {@link PlanningRecord}
	 * @return
	 */
	PlanningRecord getFactPlanningRecord(String universe_name,
			String measure_set_name);

	/**
	 * Create a {@link PlannificoLogic} of the given type
	 * 
	 * @param distribution_logic
	 * @return
	 */
	public PlannificoLogic getPlanningLogic (LogicType distribution_logic);


	

}
