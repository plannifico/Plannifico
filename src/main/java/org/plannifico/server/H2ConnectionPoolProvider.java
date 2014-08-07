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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.h2.jdbcx.JdbcConnectionPool;

/**
 * An implementation of {@link ConnectionPoolProvider} that uses H2 DB
 * 
 * @author Rosario Alfano
 *
 */
public class H2ConnectionPoolProvider implements ConnectionPoolProvider {
	
	private static H2ConnectionPoolProvider instance = null;
	
	private final static Logger logger = Logger.getLogger (H2ConnectionPoolProvider.class.getName());

	private Map <String, JdbcConnectionPool> connectionPools = new HashMap <String, JdbcConnectionPool>();
	
	public H2ConnectionPoolProvider () {
		
		instance = this;
	};
	
	public static ConnectionPoolProvider getInstance () {
		
		if (instance == null) return new H2ConnectionPoolProvider();
		
		return instance;
	}
	
	@Override
	public boolean addConnectionPool (String universe, String URL, String user_name, String passwd) {
		
		try {
			
			Class.forName("org.h2.Driver");
			
			JdbcConnectionPool pool = JdbcConnectionPool.create (URL, user_name, passwd);
			
			pool.setMaxConnections (50);
			
			pool.setLoginTimeout (30);
			
			connectionPools.put (universe, pool);					
			
			logger.log(Level.INFO, "Connection pool created: " + 
					connectionPools.get(universe).getActiveConnections());

			return true;
	    			
			
		} catch (ClassNotFoundException e) {
			
			logger.log(Level.SEVERE, "Error connecting to the planning DB");
			
			return false;
		}
	}


	@Override
	public void close() {

		for (JdbcConnectionPool pool : connectionPools.values()) {
		
			pool.dispose();
			
		}		
	}

	
	@Override
	public Connection getConnection (String universe) throws SQLException {
		
		return connectionPools.get (universe).getConnection();
	}

}
