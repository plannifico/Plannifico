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
package org.plannifico.server;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mchange.v2.c3p0.*;


/**
 * An implementation of {@link ConnectionPoolProvider} that uses MySQL DB
 * 
 * @author Rosario Alfano
 *
 */
public class C3P0ConnectionPoolProvider implements ConnectionPoolProvider {
	
	private static C3P0ConnectionPoolProvider instance = null;
	
	private final static Logger logger = Logger.getLogger (C3P0ConnectionPoolProvider.class.getName());
	
	private Map <String, ComboPooledDataSource> connectionPools = new HashMap <String, ComboPooledDataSource>();
	
	public C3P0ConnectionPoolProvider () {
		
		instance = this;
	};
	
	public static ConnectionPoolProvider getInstance () {
		
		if (instance == null) return new C3P0ConnectionPoolProvider();
		
		return instance;
	}
	
	@Override
	public boolean addConnectionPool (String universe, String driver, String URL, String user_name, String passwd) {
		
		try {
			
			ComboPooledDataSource cpds = new ComboPooledDataSource();
			
			cpds.setMaxStatements( 180 );
			
			cpds.setDriverClass( driver );
				         
			cpds.setJdbcUrl( URL );
			cpds.setUser(user_name);                                  
			cpds.setPassword(passwd);  
			
			connectionPools.put (universe, cpds);					
			
			logger.log(Level.INFO, "Connection pool created: " + 
					connectionPools.get(universe).getMaxPoolSize());

			return true;
			
		} catch (PropertyVetoException e) {
			
			logger.log(Level.SEVERE, "Connection pool creation error: " + 
					e.getMessage());
			
			return false;
		}
	}


	@Override
	public void close() {

		for (ComboPooledDataSource pool : connectionPools.values()) {
		
			pool.close ();
			
		}		
	}

	
	@Override
	public Connection getConnection (String universe) throws SQLException {
		
		return connectionPools.get (universe).getConnection();
	}

}
