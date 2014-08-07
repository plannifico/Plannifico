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

import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.WrongFieldTypeException;
import org.plannifico.data.WrongPlanDataFieldTypeException;

public class FieldResponse implements Response {

	private PlanningField resultField;

	public FieldResponse(PlanningField result) {
		
		this.resultField = result;
	}

	public String getFieldKey () {
				
		return resultField.getKey();
	}
	
	public String getFieldValue () throws WrongFieldTypeException {
		
		return String.valueOf(resultField.getNumberValue());
	}
	
	@Override
	public Response append(Response response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getContent() {
		// TODO Auto-generated method stub
		return null;
	}

}
