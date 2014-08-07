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

import java.util.List;

import org.plannifico.logic.PlannificoLogic.LogicType;

public class Utils {
	
	public static String listToStringQuery (List <String> fields) {
		
		String query = "";
		
		for (String field : fields) {
			
			query += field.replace(":", "=") + ";";
		}
		
		return query.substring(0,query.length()-1);
	}

	public static LogicType string2LogicType (String logic_type) {
		
		switch (logic_type) {
		
			case "Equal": return LogicType.Equal; 
			case "Proportional": return LogicType.Proportional;
			case "RoundedProportional": return LogicType.RoundedProportional;
			
			default: return LogicType.RoundedProportional;
		}
	}

}
