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


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.PlanningUniverse;
import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.TextField;

public abstract class FactPlanningRecord implements PlanningRecord {

	protected Map<String, PlanningField> fields = new HashMap <> ();
	protected Map<String, PlanningField> measures = new HashMap <> ();

	protected String universeName;
	protected String measureSetName;
	
	protected final static Logger logger = Logger
			.getLogger(FactPlanningRecord.class.getName());

	

	public FactPlanningRecord (String universe, String measure_set) {
		
		this.universeName = universe;
		this.measureSetName = measure_set;
	}
	
	@Override
	public Collection<PlanningField> getFields () {

		return fields.values();
	}

	@Override
	public Collection<PlanningField> getMeasures() {

		return measures.values();
	}

	@Override
	public NumberField getMeasureValue (String measure_name) {
		
		return (NumberField) measures.get (measure_name);
	}
	
	@Override
	public String getRecordKey() throws MissingFieldException {

		return null;
	}

	@Override
	public int getColumnNumber() {

		return fields.size();
	}

	@Override
	public void addField (PlanningField field)
			throws FieldAlreadyExistsException {

		if (fields.containsKey (field.getKey()))
			throw new FieldAlreadyExistsException();

		if (field instanceof TextField)
			fields.put (field.getKey(), field);

		else if (field instanceof NumberField)
			measures.put (field.getKey(), field);
	}

	@Override
	public void updateFieldValue(PlanningField field)
			throws MissingFieldException {

		if (!fields.containsKey(field.getKey()))
			throw new MissingFieldException();

	}

	@Override
	public abstract void populateRecordByKey (String key) throws WrongPlanningRecordKey;
}
