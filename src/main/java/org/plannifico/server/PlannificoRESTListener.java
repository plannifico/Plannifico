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

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.Utils;
import org.plannifico.data.UniverseNotExistException;
import org.plannifico.logic.PlannificoLogic.LogicType;
import org.plannifico.server.configuration.XMLBasedConfigurationManager;
import org.plannifico.server.executors.GetAggregatedValueExecutor;
import org.plannifico.server.executors.GetDatasetExecutor;
import org.plannifico.server.executors.GetRecordsByAggregationExecutor;
import org.plannifico.server.executors.GetRecordByKeyExecutor;
import org.plannifico.server.executors.setAggregatedValueExecutor;
import org.plannifico.server.response.BasicResponse;
import org.plannifico.server.response.NumberResponse;
import org.plannifico.server.response.RecordResponse;
import org.plannifico.server.response.Response;
import org.plannifico.server.response.StatusResponse;
import org.plannifico.server.response.StrCollectionResponse;
import org.plannifico.server.response.StrMapResponse;

@Path ("/plannifico")
public class PlannificoRESTListener {
	
	private static String configurationFile;

	private static PlannificoFactory factory;
	
	private static PlanningEngine engine;

	private final static Logger logger = Logger.getLogger (PlannificoRESTListener.class.getName());


	public PlannificoRESTListener () {}
	
	public static void main(String[] args) {		
		
		initRESTWebAPIs();				
		
		configurationFile = 
				XMLBasedConfigurationManager.DEFAULT_CONFIGURATION_FILE;
    	
    	if (args.length >= 1)
    		configurationFile = args [0];
    	
    	factory = PlannificoFactoryProvider.getInstance();
    	
    	engine = factory.getPlanningEngine ();
	}

	public static boolean initRESTWebAPIs() {
		
		logger.log (Level.INFO, "initRESTWebAPIs called");
		
		URI baseUri = UriBuilder.fromUri ("http://localhost/").port(9998).build();
		
		ResourceConfig config = new ResourceConfig (PlannificoRESTListener.class)
				.packages("org.glassfish.jersey.examples.jackson")
		        .register(JacksonFeature.class);		
		
		JdkHttpServerFactory.createHttpServer (baseUri, config);
		
		return true;
	}
	
	@Path("/helloworld")
	public class HelloWorldResources {
	 
	    // The Java method will process HTTP GET requests
	    @GET
	    // The Java method will produce content identified by the MIME Media
	    // type "text/plain"
	    @Produces("text/plain")
	    public String getClichedMessage() {
	        // Return some cliched textual content
	        return "Hello World";
	    }
	}
	
	@GET
	@Path("/adm/start")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response start () {
				
		logger.log (Level.INFO, "Received /start");
		
		try {
			
			if( engine.start (configurationFile) == 1)
				return new BasicResponse("1", 
						"Severe Error starting the server: read log for more information");
			
		} catch (ServerAlreadyRunningException e) {
			
			return new BasicResponse("1", "Failure: Server already running");
		}
		
		return new BasicResponse("0","Success: server started");
	}
	
	@GET
	@Path("/adm/stop")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response stop () {
		
		logger.log (Level.FINE, "Received /stop");	
						
		return new BasicResponse ("0","Success");			
	}
	
	@GET
	@Path("/adm/getStatus")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getStatus () {
		
		logger.log (Level.FINE, "Received /getStatus");	
		
		return new BasicResponse("0","Success")
			.append(new StatusResponse(engine.getStatus ()));
	
										
	}
	
	@GET
	@Path("/adm/getUniverses")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getUniverses () {
		
		logger.log (Level.FINE, "Received /getUniverses");	
				
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");			
		
		return new BasicResponse("0","Success")
			.append(new StrCollectionResponse(engine.getUniverses()));										
	}
	
	@GET
	@Path("/adm/getMeasureSetsCount/{universe_name}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getMeasureSetsCount (
			@PathParam("universe_name") final String universe_name) {
		
		logger.log (Level.FINE, "Received /getMeasureSetsCount");	
				
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");			
			
		int result = engine.getMeasureSetsNumber (universe_name);
		
		return new BasicResponse("0","Success")
			.append(new NumberResponse (result));										
	}
	
	@GET
	@Path("/adm/getMeasureSetsNames/{universe_name}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getMeasureSetsNames (
			@PathParam("universe_name") final String universe_name) {
		
		logger.log (Level.FINE, "Received /getMeasureSetsNames");	
				
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");			
			
		return new BasicResponse("0","Success")
			.append(new StrCollectionResponse (engine.getMeasureSetsNames (universe_name)));										
	}
	
	@GET
	@Path("/adm/getPlanningDimensionRelationship/{universe_name}/{dimension}/{key}")
	@Consumes("text/plain")  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getPlanningDimensionRelationship (
			@PathParam("universe_name") final String universe_name,
			@PathParam("dimension") final String dimension,
			@PathParam("key") final String key) {
				
		logger.log (Level.FINE, "Received /getPlanningDimensionRelationship");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		if ((universe_name == null) || (dimension == null) || (key == null))
			return new BasicResponse ("1","Error: Missing param.");
		
		try {
					
			return new BasicResponse("0","Success")
				.append(new StrCollectionResponse(
						engine.getPlanningDimensionRelationship (universe_name, dimension, key)));
		
		} catch (UniverseNotExistException e) {
		
			return new BasicResponse ("1", String.format("Error: Universe %s does not exist)",universe_name));
		}										
	}
	
	@GET
	@Path("/adm/getAllDimensionRelationships/{universe_name}/{dimension}")
	@Consumes("text/plain")  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getPlanningDimensionRelationship (
			@PathParam("universe_name") final String universe_name,
			@PathParam("dimension") final String dimension) {
				
		logger.log (Level.FINE, "Received /getAllDimensionRelationships");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		if ((universe_name == null) || (dimension == null))
			return new BasicResponse ("1","Error: Missing param.");
		
		try {
					
			return new BasicResponse("0","Success")
				.append(new StrMapResponse(
						engine.getAllDimensionRelationships (universe_name, dimension)));
		
		} catch (UniverseNotExistException e) {
		
			return new BasicResponse ("1", String.format("Error: Universe %s does not exist)",universe_name));
		}										
	}
	
	@GET
	@Path("/adm/getMeasureSetRecordsCount/{universe_name}/{measure_set}")
	@Consumes("text/plain")  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getMeasureSetRecordsCount (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set) {
		
		logger.log (Level.FINE, "Received /getMeasureSetRecordsCount");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
				
		if (universe_name == null) 
			return new BasicResponse ("1","Error: Missing parameter universe_name.");
		
		try {
		
			return new BasicResponse("0","Success")
				.append(new NumberResponse (
						engine.getMasureSetRecordsNumber (universe_name, measure_set)));
		
		} catch (ActionNotPermittedException e) {
			
			return new BasicResponse ("1",
					"Error: the action cannot be run on the server. Check the server status.");
			
		} catch (UniverseNotExistException e) {
			
			return new BasicResponse ("1", 
					String.format("Error: Universe %s does not exist)",universe_name));
		}										
	}
	
	@GET
	@Path("/adm/getMeasureSetMeasures/{universe_name}/{measure_set}")
	@Consumes("text/plain")  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getMeasureSetMeasures (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set) {
				
		logger.log (Level.FINE, "Received /getMeasureSetMeasures");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		if (universe_name == null) 
			return new BasicResponse ("1","Error: Missing param universe_name.");
		
		try {
					
			return new BasicResponse("0","Success")
				.append(new StrCollectionResponse (
						engine.getMeasureSetsMeasureNames (universe_name,measure_set)));
		
		} catch (UniverseNotExistException e) {
		
			return new BasicResponse ("1", String.format("Error: Universe %s does not exist)",universe_name));
		}										
	}
	
	@GET
	@Path("/adm/getPlanningDimensions/{universe_name}")
	@Consumes("text/plain")  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getPlanningDimensions (
			@PathParam("universe_name") final String universe_name) {
				
		logger.log (Level.FINE, "Received /getPlanningDimensions");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		if (universe_name == null) 
			return new BasicResponse ("1","Error: Missing param universe_name.");
		
		try {
					
			return new BasicResponse("0","Success")
				.append(new StrCollectionResponse (
						engine.getPlanningDimensions (universe_name)));
		
		} catch (UniverseNotExistException e) {
		
			return new BasicResponse ("1", String.format("Error: Universe %s does not exist)",universe_name));
		}										
	}
	
	
	
	@GET
	@Path("/planning_action/getRecordByKey/{universe_name}/{measure_set}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getRecordByKey (
			
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@QueryParam("field") List<String> fields) {
				
		logger.log (Level.FINE, "Received /getRecordByKey");			
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
			
	    Callable<Response> callable = 
	    		new GetRecordByKeyExecutor (engine, 
	    				universe_name, 
	    				measure_set, 
	    				Utils.listToStringQuery (fields));
	    
	    Future <Response> future = engine.getThreadsPool().submit (callable);	    
	
	    try {
		
	    	Response resp = future.get();
	    	
	    	return resp;
		
	    } catch (InterruptedException | ExecutionException e) {
			
	    	return new BasicResponse ("1", 
	    		String.format("Error: error getting records by keys. Check the log for details", fields));
		}										
	}
	
	@GET
	@Path("/planning_action/getAggregatedValue/{universe_name}/{measure_set}/{measure}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getAggregatedValue (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@PathParam("measure") final String measure,
			@QueryParam("field") List<String> fields) {
				
		logger.log (Level.FINE, "Received /getAggregatedValue");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		 Callable <Response> callable = 
	    		new GetAggregatedValueExecutor (engine, 
	    				universe_name, 
	    				measure_set, 
	    				measure, 
	    				Utils.listToStringQuery(fields));
	    	
	    try {
	    	
	    	Future<Response> future = engine.getThreadsPool().submit (callable);
	    	
	    	return future.get();
		
	    } catch (Exception e) {
			
	    	e.printStackTrace();
	    	
	    	return new BasicResponse ("1", 
	    		String.format("Error: error getting the aggregation for %s. Check the log for details.", fields));
		}
	}
	
	@GET
	@Path("/planning_action/setAggregatedValue/{universe_name}/{measure_set}/{measure}/{new_value}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response setAggregatedValue (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@PathParam("measure") final String measure,
			@PathParam("new_value") final double new_value,
			@QueryParam("field") List<String> fields) {
				
		logger.log (Level.FINE, "Received /setAggregatedValue");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
			
	    Callable<Response> callable = 
	    	new setAggregatedValueExecutor (engine, 
	    			universe_name, 
	    			measure_set, 
	    			measure, 
	    			new_value, 
	    			LogicType.RoundedProportional, 
	    			Utils.listToStringQuery(fields));
	    
	    Future<Response> future = engine.getThreadsPool().submit (callable);
	
	    try {
		
	    	return future.get();
		
	    } catch (InterruptedException | ExecutionException e) {
			
	    	return new BasicResponse ("1", 
	    		String.format("Error: error getting the aggregation. Check the log for details", fields));
		}										
	}
	
	@GET
	@Path("/planning_action/setAggregatedValue/{universe_name}/{measure_set}/{measure}/{logic_type}/{new_value}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response setAggregatedValue (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@PathParam("measure") final String measure,
			@PathParam("new_value") final double new_value,
			@PathParam("logic_type") final String logic_type,
			@QueryParam("field") List<String> fields) {
				
		logger.log (Level.FINE, "Received /setAggregatedValue");	
		
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
						
		Callable<Response> callable = 
		    	new setAggregatedValueExecutor (engine, 
		    			universe_name, 
		    			measure_set, 
		    			measure, 
		    			new_value, 
		    			Utils.string2LogicType (logic_type), 
		    			Utils.listToStringQuery(fields));
		    
		    Future<Response> future = engine.getThreadsPool().submit (callable);
		
		    try {
			
		    	return future.get();
			
		    } catch (InterruptedException | ExecutionException e) {
				
		    	return new BasicResponse ("1", 
		    		String.format("Error: error getting the aggregation. Check the log for details", 
		    				fields));
			}										
	}
	
	@GET
	@Path("/planning_action/getRecordsByAggregation/{universe_name}/{measure_set}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response setAggregatedValue (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@QueryParam("field") List<String> fields) {
				
		logger.log (Level.FINE, "Received /getRecordsByAggregation");							
				
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		Callable<Response> callable = 
	    		new GetRecordsByAggregationExecutor (engine, 
	    				universe_name, 
	    				measure_set, 
	    				Utils.listToStringQuery(fields));
	    
	    Future <Response> future = engine.getThreadsPool().submit (callable);
	
	    try {
		
	    	return future.get();
		
	    } catch (InterruptedException | ExecutionException e) {
			
	    	return new BasicResponse ("1", 
	    		String.format("Error: error getting records by aggregations. Check the log for details", fields));
		}										
	}
	
	@GET
	@Path("/planning_action/getDataset/{universe_name}/{measure_set}")
	@Consumes(javax.ws.rs.core.MediaType.TEXT_PLAIN)  
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public org.plannifico.server.response.Response getDataset (
			@PathParam("universe_name") final String universe_name,
			@PathParam("measure_set") final String measure_set,
			@QueryParam("measure") final List<String> measures,
			@QueryParam("filter") final List<String> filters,
			@QueryParam("groupby") final List<String> groupby) {
				
		logger.log (Level.FINE, "Received /getRecordsByAggregation");							
				
		if (engine.getStatus () != PlanningEngine.STARTED)
			return new BasicResponse ("1",
					"Error: the server is not started.");
		
		Callable<Response> callable = 
	    		new GetDatasetExecutor (engine, 
	    				universe_name, 
	    				measure_set, 
	    				Utils.listToSemicomaSeparated (measures),
	    				Utils.listToStringQuery (filters),
	    				Utils.listToSemicomaSeparated (groupby));
	    
	    Future <Response> future = engine.getThreadsPool().submit (callable);
	
	    try {
		
	    	return future.get();
		
	    } catch (InterruptedException | ExecutionException e) {
			
	    	return new BasicResponse ("1", 
	    		String.format("Error: error getting dataset. Check the log for details."));
		}										
	}
	
	
}
