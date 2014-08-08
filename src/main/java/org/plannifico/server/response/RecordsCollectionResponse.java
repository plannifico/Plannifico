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

import org.plannifico.data.records.PlanningRecord;


public class RecordsCollectionResponse extends ResponseImpl implements Response  {

	private Collection<Response> responses = new ArrayList<>();
	
	public RecordsCollectionResponse (Collection<PlanningRecord> responses) {
		
		for (PlanningRecord record: responses) {
			
			this.responses.add (new RecordResponse(record));
		}	
	}	
	
	public Collection <Response> getRecords () {
		
		return this.responses;
	}
}
