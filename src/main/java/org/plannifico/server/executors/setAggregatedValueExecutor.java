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

import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.ActionNotPermittedException;
import org.plannifico.server.PlanningEngine;

import org.plannifico.server.response.BasicResponse;
import org.plannifico.server.response.Response;


public class setAggregatedValueExecutor implements Callable<Response> {

	private PlanningEngine calcEngine;
	private String universeName;
	private String measure;
	private String fields;
	private String measureSet;
	private double newValue;
	private LogicType logicType;
	
	public setAggregatedValueExecutor (
			PlanningEngine engine, 
			String universe_name, 
			String measure_set, 
			String measure, 
			double new_value,
			LogicType logic_type, 
			String fields) {
		
		calcEngine = engine;
		
		universeName = universe_name;
		
		measureSet = measure_set;
		
		this.measure = measure;
		
		this.fields = fields;
		
		this.newValue = new_value;
		
		this.logicType = logic_type;
	}
	
	@Override
	public Response call () {
		
		int result;
		
		try {
			
			result = calcEngine.setAggregatedValue (
					universeName, 
					measureSet, 
					measure, 
					logicType, 
					newValue, 
					fields);
			
			return new BasicResponse (result + "", (result == 0) ? "Success" : "Error");
			
		} catch (ActionNotPermittedException e) {
			
			return new BasicResponse ("1", String.format("Error: the planning action is not permitted"));
			
		} catch (LogicCalculationException e) {
			
			return new BasicResponse ("1", String.format("Error: logic calculation error. See log for details"));
		}
	}	

}
