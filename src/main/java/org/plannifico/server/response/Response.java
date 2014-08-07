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

/**
 * Interface implemented by a Plannifico server response
 * 
 * @author Rosario Alfano
 * @version 0.1
 */
public interface Response {
	
	/**
	 * Append a piece of response to another response
	 * @param response
	 * @return
	 */
	Response append (Response response);
	
	/**
	 * Get the content of the response
	 * @return
	 */
	public Response getContent ();

}
