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
package org.plannifico.server.response;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.WrongFieldTypeException;
import org.plannifico.data.records.MissingFieldException;
import org.plannifico.data.records.PlanningRecord;


public class RecordResponse implements ResponseContent {

	private PlanningRecord record;

	public RecordResponse (PlanningRecord record) {
		
		this.record = record;
	}
	
	public Collection<Map<String,String>> getAttributes () {
		
		Collection<Map<String,String>> response = new ArrayList<>();
		
		for (PlanningField field : record.getFields()) {
			
			Map <String,String> field_map = new HashMap<>();
			
			field_map.put(field.getKey(), field.getValue());
			
			response.add (field_map);
		}
		
		return response;
	}
	
	public Collection<Map<String,Double>> getMeasures () {
				
		Collection<Map<String,Double>> response = new ArrayList<>();
		
		for (PlanningField field : record.getMeasures()) {
			
			Map <String,Double> field_map = new HashMap<>();
			
			try {
				
				field_map.put(field.getKey(), field.getNumberValue());
				
			} catch (WrongFieldTypeException e) {
				
				field_map.put("Null", new Double (-1));
			}
			
			response.add (field_map);
		}
		
		return response;
	}	
}
