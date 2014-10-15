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
package org.plannifico.data;

import org.plannifico.server.PlanningEngine;

/**
 * A {@link DataPreparator} provides management methods needed
 * to prepare data consumable by a {@link PlanningEngine}
 * 
 * @author Alfano Rosario
 *
 */
public interface DataPreparator {
	
	/**
	 * Prepare the data for the given universe
	 * 
	 * @param universe
	 */
	public void prepare (String universe);

}
