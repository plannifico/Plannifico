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

import java.util.List;
import java.util.concurrent.Callable;

import org.plannifico.data.fields.NullField;
import org.plannifico.data.fields.PlanningField;

import org.plannifico.server.ActionNotPermittedException;
import org.plannifico.server.PlanningEngine;
import org.plannifico.server.response.BasicResponse;
import org.plannifico.server.response.FieldResponse;
import org.plannifico.server.response.Response;


public class GetAggregatedValueExecutor implements Callable<Response> {

	private PlanningEngine calcEngine;
	private String universeName;
	private String measure;
	private String fields;
	private String measureSet;

	public GetAggregatedValueExecutor (
			PlanningEngine engine, 
			String universe_name, 
			String measure_set, 
			String measure, 
			String fields) {
		
		calcEngine = engine;
		
		universeName = universe_name;
		
		measureSet = measure_set;
		
		this.measure = measure;
		
		this.fields = fields;
	}
	
	@Override
	public Response call () {		
		
		PlanningField result;
		
		try {
			
			result = calcEngine.getAggregatedValue (universeName, measureSet, measure, fields);
			
			if (result instanceof NullField)
				return new BasicResponse ("1", String.format("Error: see log for details"));
				
			else
				return new BasicResponse ("0","Success").append (new FieldResponse (result));
			
		} catch (ActionNotPermittedException e) {
			
			return new BasicResponse ("1", String.format("Error: the planning action is not permitted"));
		}
	}	
}
