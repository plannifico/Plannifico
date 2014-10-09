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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;
/**
 * An implementation of the ConfigurationManager that reads the configuration
 * from an XML file
 * 
 * @author Rosario Alfano
 *
 */
public class XMLBasedConfigurationManager implements ConfigurationManager {

	public static String DEFAULT_CONFIGURATION_FILE = "conf/plannifico-server.xml";
	
	private final Logger logger = Logger.getLogger (XMLBasedConfigurationManager.class.getName());
	
	private Map<String,String> databaseURL = new HashMap<>();
	private Map<String,String>  planningDataDBUser = new HashMap<>();
	private Map<String,String>  planningDataDBPwd = new HashMap<>();
	private Map<String,String>  planningDBDriver = new HashMap<>();
	
	private String workDatabaseURL;

	private boolean configurationLoaded = false;

	private String workDataDBUser;

	private String workDataDBPwd;
	
	@Override
	public boolean loadConfiguration (String configuration_file) {
		
		try {
			
			logger.log(Level.INFO, "Loading configuration from:" + configuration_file);
			
			XMLConfiguration config = new XMLConfiguration (configuration_file); 
			
			config.load();
			
			config.setThrowExceptionOnMissing (true);
			
			if (config.isEmpty()) {
				logger.log(Level.WARNING,
						"Configuration file is empty:" + DEFAULT_CONFIGURATION_FILE);
				
				return false;
			}
				
			
			List<Object> planning_universes = config.getList("Server.PlanningUniverses.Universe");
			
			for (Object universe : planning_universes) {
				
				String universe_name = (String) universe;
				
				databaseURL.put (universe_name, config.getString ("Server." + universe + ".planningDB.databaseURL"));
				//"Plannifico.Server.databaseURL");
		
				logger.log (Level.INFO, 
					String.format("Configuration for universe %s: databaseURL: %s", universe_name, databaseURL));
							
				/*workDatabaseURL = config.getString ("Server.workingDB.databaseURL");
				
				logger.log(Level.FINE, "Configuration: workDatabaseURL: " + workDatabaseURL);
				*/
				planningDataDBUser.put (universe_name, config.getString ("Server." + universe + ".planningDB.dBUser"));
				
				logger.log(Level.INFO, 
						String.format("Configuration for universe %s: user name: %s", universe_name, planningDataDBUser));
				
				planningDataDBPwd.put (universe_name, config.getString ("Server." + universe + ".planningDB.dBPwd"));
				
				planningDBDriver.put (universe_name, config.getString ("Server." + universe + ".planningDB.driver"));
				
				logger.log(Level.INFO, 
						String.format("Configuration for universe %s: password: %s", universe_name, planningDataDBPwd));
					
			}				
			
			/*
			workDataDBUser = config.getString ("Server.workingDB.dBUser");
			
			logger.log(Level.FINE, "Configuration: workDataDBUser: " + workDataDBUser);
			
			workDataDBPwd = config.getString ("Server.workingDB.dBPwd");
			
			logger.log(Level.FINE, "Configuration: workDataDBPwd: " + workDataDBPwd);
			*/
			configurationLoaded = true;
		}					
		catch (Exception e) {

			logger.log(Level.SEVERE, "Error loading configuration " + e.getMessage());
			
			return false;
		}	
		
		return true;
	}

	public String getDatabaseURL (String universe_name) {
		
		return databaseURL.get(universe_name);
	}

	public String getWorkDatabaseURL () {
		
		return workDatabaseURL;
	}

	public boolean isConfigurationLoaded () {
		
		return configurationLoaded;
	}

	@Override
	public String getPlanningDataDBUser(String universe_name) {
		
		return planningDataDBUser.get(universe_name);
	}

	@Override
	public String getPlanningDataDBPwd(String universe_name) {
		
		return planningDataDBPwd.get(universe_name);
	}

	@Override
	public String getWorkDataDBUser() {
		
		return workDataDBUser;
	}

	@Override
	public String getWorkDataDBPwd() {
		
		return workDataDBPwd;
	}

	@Override
	public Set <String> getPlanningUniverses() {
		
		return databaseURL.keySet();
	}

	@Override
	public String getDriver(String universe_name) {
		
		return planningDBDriver.get (universe_name);
	}
	
}
