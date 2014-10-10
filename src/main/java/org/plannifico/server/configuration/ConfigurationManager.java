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
package org.plannifico.server.configuration;

import java.util.Set;

public interface ConfigurationManager {
	
	/**
	 * Load the server configuration
	 * 
	 * @param configuration_file 
	 * 
	 * @return true if the load succeeded
	 */
	public boolean loadConfiguration (String configuration_file);	


	/**
	 * Return the database driver where Plannifico planning data are stored
	 * @return
	 */
	public String getDriver(String universe_name);

	
	/**
	 * Return the databaseURL where Plannifico planning data are stored
	 * @return
	 */
	public String getDatabaseURL (String universe_name);

	/**
	 * Return the databaseURL where Plannifico working data are stored
	 * @return
	 */
	public String getWorkDatabaseURL ();

	/**
	 * Return true if the configuration has completely been loaded
	 * 
	 * @return
	 */
	public boolean isConfigurationLoaded ();

	/**
	 * Return the user of the planning data DB
	 * 
	 * @return
	 */
	public String getPlanningDataDBUser(String universe_name);

	/**
	 * Return the password of the planning data DB 
	 * 
	 * @return
	 */
	public String getPlanningDataDBPwd(String universe_name);

	/**
	 * Return the user of the working data DB
	 * 
	 * @return
	 */
	public String getWorkDataDBUser();
	
	/**
	 * Return the password of the working data DB
	 * 
	 * @return
	 */
	public String getWorkDataDBPwd();
	
	/**
	 * Return the planning universes to manage
	 * @return
	 */
	public Set <String> getPlanningUniverses ();

}
