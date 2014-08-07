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

import java.util.concurrent.Callable;

import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.NullField;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.server.PlanningEngine;
import org.plannifico.server.response.BasicResponse;
import org.plannifico.server.response.RecordResponse;
import org.plannifico.server.response.Response;


public class GetRecordsByKeyExecutor implements Callable<Response> {

	private PlanningEngine calcEngine;
	private String universeName;
	private String measureSet;
	private String query;
	
	public GetRecordsByKeyExecutor (
			PlanningEngine engine, 
			String universe_name, 
			String measure_set, 
			String query) {
		
		calcEngine = engine;
		
		universeName = universe_name;
		
		measureSet = measure_set;
	
		this.query = query;		
	}

	@Override
	public Response call () {
		
		PlanningRecord result;
		
		try {
			
			result = calcEngine.getRecordByKey (universeName, measureSet, query);
			
			if (result instanceof NullField)
				return new BasicResponse ("1", String.format("Error: see log for details"));
				
			else
				return new BasicResponse ("0","Success").append (new RecordResponse (result));
			
		} catch (WrongPlanningRecordKey e) {
			
			return new BasicResponse ("1", 
					String.format("Error: Wrong Planning Record Key"));
		}
	}	
}
