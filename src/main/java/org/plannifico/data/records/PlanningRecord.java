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

import java.util.Collection;

import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;

public interface PlanningRecord {

	/**
	 * Populate the record with the given key
	 * 
	 * @param key
	 * @throws WrongPlanningRecordKey 
	 */
	public void populateRecordByKey (String key) throws WrongPlanningRecordKey;
	
	/**
	 * Return an array with all the columns of the record aggregations
	 * @return
	 */
	public Collection <PlanningField> getAttributes ();

	/**
	 * Return an array with all the values of the record
	 * @return
	 */
	public Collection <PlanningField> getMeasures ();
	
	/**
	 * Return the value of the given attribute
	 * @return
	 */
	public PlanningField getAttributeValue (String attribute_name);
	
	/**
	 * Return the value of the given measure
	 * @return
	 */
	public NumberField getMeasureValue (String measure_name);

	/**
	 * Get a string containing the comma-separated key-values forming the key	
	 * @return
	 * @throws MissingFieldException 
	 */
	public String getRecordKey () throws MissingFieldException;
	
	/**
	 * Return the record column number
	 * @return
	 */
	public int getColumnNumber ();
	
	/**
	 * Add a planning field to the record
	 * 
	 * @param field
	 */
	public void addField (PlanningField field) throws FieldAlreadyExistsException;

	/**
	 * Update the field record with the given field
	 * 
	 * @param field
	 * @throws MissingFieldException
	 */
	public void updateFieldValue (PlanningField field) throws MissingFieldException;
	

}
