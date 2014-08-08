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
package org.plannifico.server.executors;

import java.util.ArrayList;
import java.util.Collection;

import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.NullField;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.records.FieldAlreadyExistsException;
import org.plannifico.data.records.MissingFieldException;
import org.plannifico.data.records.PlanningRecord;

public class NullRecord implements PlanningRecord {

	@Override
	public void populateRecordByKey(String key) throws WrongPlanningRecordKey {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<PlanningField> getFields() {
		
		return new ArrayList<PlanningField>();
	}

	@Override
	public Collection<PlanningField> getMeasures() {
		
		return new ArrayList<PlanningField>();
	}

	@Override
	public NumberField getMeasureValue(String measure_name) {
		
		return new NumberField("null", 0);
	}

	@Override
	public String getRecordKey() throws MissingFieldException {
		
		return "null";
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

}
