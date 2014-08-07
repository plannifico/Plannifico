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

import java.util.Collection;


public class StrCollectionResponse implements Response {

	private Collection<String> collection;

	public StrCollectionResponse (Collection <String> collection) {
		
		this.collection = collection;
	}
	
	public Collection <String> getCollection () {
		
		return collection;
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
