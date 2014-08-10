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
package org.plannifico.data.records;

import java.util.ArrayList;
import java.util.Collection;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.fields.NullField;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;

public class EmptyPlanningRecord implements PlanningRecord {

	private PlannificoFactory factory;

	public EmptyPlanningRecord () {
		
		factory = PlannificoFactoryProvider.getInstance();
	}
	
	@Override
	public Collection<PlanningField> getAttributes() {
		
		return new ArrayList<>();
	}

	@Override
	public Collection<PlanningField> getMeasures() {
		
		return new ArrayList<>();
	}

	@Override
	public NumberField getMeasureValue (String measure_name) {
		
		return (NumberField) factory.getNumberField("NA", 0);
	}

	@Override
	public String getRecordKey() throws MissingFieldException {
		
		return "";
	}

	@Override
	public int getColumnNumber() {
		
		return 0;
	}

	@Override
	public void addField(PlanningField field)
			throws FieldAlreadyExistsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFieldValue(PlanningField field)
			throws MissingFieldException {
		// TODO Auto-generated method stub

	}

	@Override
	public void populateRecordByKey(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PlanningField getAttributeValue(String attribute_name) {
		
		return new NullField();
	}

}
