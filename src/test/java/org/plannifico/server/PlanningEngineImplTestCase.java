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

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.plannifico.PlannificoFactory;
import org.plannifico.PlannificoFactoryProvider;
import org.plannifico.data.PlanningSet;
import org.plannifico.data.UniverseNotExistException;
import org.plannifico.data.WrongPlanningRecordKey;
import org.plannifico.data.fields.NumberField;
import org.plannifico.data.fields.PlanningField;
import org.plannifico.data.fields.WrongFieldTypeException;
import org.plannifico.data.records.PlanningRecord;
import org.plannifico.logic.LogicCalculationException;
import org.plannifico.logic.PlannificoLogic.LogicType;


public class PlanningEngineImplTestCase {

	private PlannificoFactory factory;
	private PlanningEngine engine;
	
	static Connection conn = null;
	Statement stmt = null;
	
	private String core_configuration_file = 
			"src/test/plannifico-server.xml";
	
	@BeforeClass
	public static void setUpBeforeClass () throws Exception {		
			
		Class.forName("org.h2.Driver");
		
		conn = 
				DriverManager.getConnection(
						"jdbc:h2:~/PLN_UNIVERSE_TEST", 
						"sa", 
						"");
		
		/*
		conn = 
				DriverManager.getConnection(
						"jdbc:h2:tcp://localhost/~/PLN_UNIVERSE_TEST", 
						"sa", 
						"");*/		
	}
	
	@Before
	public void setUp() throws Exception {
		
		factory = PlannificoFactoryProvider.getInstance();
    	
    	engine = factory.getPlanningEngine ();    	
    	
    	stmt = conn.createStatement();
    	    	
        String sql = "CREATE TABLE DIM_AAAA " +
                     "(AAAA VARCHAR(255) not NULL, " +
                     " A VARCHAR(255), " + 
                     " AA VARCHAR(255), " + 
                     " AAA VARCHAR(255), " + 
                     " PRIMARY KEY ( AAAA ))"; 

        stmt.executeUpdate (sql);
        System.out.println("DIM_AAAA created");
        
        
        sql = "CREATE TABLE DIM_BBB " +
                "(BBB VARCHAR(255) not NULL, " +
                " B VARCHAR(255), " + 
                " BB VARCHAR(255), " +                  
                " PRIMARY KEY ( BBB ))"; 

        stmt.executeUpdate (sql);
        System.out.println("DIM_BBB created");
        
        sql = "CREATE TABLE DIM_CCC " +
                "(CCC VARCHAR(255) not NULL, " +
                " C VARCHAR(255), " + 
                " CC VARCHAR(255), " +                 
                " PRIMARY KEY ( CCC ))"; 
        
        stmt.executeUpdate (sql);        
        System.out.println("DIM_CCC created");
        
        sql = "CREATE TABLE MEASURE_SET_MSET1 " +
                "(AAAA VARCHAR(255) not NULL, " +
                " BBB VARCHAR(255) not NULL, " + 
                " CCC VARCHAR(255) not NULL, " +
                " VALUE_M1 DOUBLE, " +
                " VALUE_M2 DOUBLE, " +
                " PRIMARY KEY ( AAAA,BBB,CCC ))"; 
        
        stmt.executeUpdate (sql);        
        System.out.println("DIM_CCC created");     

        stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA1','A1','AA1','AAA1')");
        stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA2','A1','AA1','AAA1')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA3','A1','AA1','AAA1')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA4','A1','AA2','AAA1')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA5','A2','AA2','AAA1')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA6','A2','AA2','AAA1')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA7','A2','AA3','AAA2')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA8','A2','AA3','AAA2')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA9','A2','AA3','AAA2')");
		stmt.executeUpdate ("INSERT INTO DIM_AAAA VALUES ('AAAA10','A2','AA3','AAA2')");		
		
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB1','B1','BB1')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB2','B1','BB1')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB3','B1','BB2')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB4','B2','BB2')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB5','B2','BB3')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB6','B2','BB2')");
		stmt.executeUpdate ("INSERT INTO DIM_BBB VALUES ('BBB7','B2','BB3')");
		
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC1','C4','CC3')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC2','C2','CC3')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC3','C1','CC3')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC4','C12','CC3')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC5','C4','CC7')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC6','C4','CC8')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC7','C4','CC9')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC8','C4','CC2')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC9','C4','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC10','C2','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC11','C4','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC12','C5','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC13','C5','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC14','C5','CC1')");
		stmt.executeUpdate ("INSERT INTO DIM_CCC VALUES ('CCC15','C5','CC1')");

        
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA1','BBB2','CCC5',20,10)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA2','BBB5','CCC4',10,50)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA3','BBB6','CCC12',110,90)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA4','BBB3','CCC1',120,10)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA5','BBB8','CCC1',130,30)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA6','BBB2','CCC6',44,33)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA7','BBB1','CCC7',25,80)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA8','BBB1','CCC9',23,90)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA9','BBB7','CCC8',220,10)");
		stmt.executeUpdate ("INSERT INTO MEASURE_SET_MSET1 VALUES('AAAA10','BBB2','CCC1',10,44)");	
		
		/*
		  
		  	SELECT * 
			FROM MEASURE_SET_MSET1 M 
			join DIM_AAAA D_A on D_A.AAAA = M.AAAA 
			join DIM_BBB D_B on D_B.BBB = M.BBB
			join DIM_CCC D_C on D_C.CCC = M.CCC
		  
		 */
	}
	
	@After
	public void after () throws Exception {
		
		stmt = conn.createStatement();
    			
        stmt.executeUpdate ("DROP TABLE DIM_AAAA;");
        
        System.out.println("DIM_AAAA dropped");
        		
        stmt.executeUpdate ("DROP TABLE DIM_BBB;");
        
        System.out.println("DIM_BBB dropped");
        		
        stmt.executeUpdate ("DROP TABLE DIM_CCC;");
        
        System.out.println("DIM_CCC dropped");
        
        stmt.executeUpdate ("DROP TABLE MEASURE_SET_MSET1;");
        
        System.out.println("MEASURE_SET_MSET1 dropped");
        
	}
		
	@AfterClass	
	public static void afterClass () throws Exception {
		
		conn.close ();
	}


	@Test
	public void testGetRecordByKey () throws ServerAlreadyRunningException, WrongPlanningRecordKey {
		
		engine.start(core_configuration_file);
		
		PlanningRecord record = 
				engine.getRecordByKey ("TEST", "MSET1", "AAAA=AAAA2;BBB=BBB5;CCC=CCC4");
		
		Collection<PlanningField> fields = record.getAttributes();

		String BB3 = "";
		String C12 = "";
		
		for (PlanningField field: fields) {
			
			if (field.getKey().equals("BBB")) BB3 = field.getValue().toString();
			if (field.getKey().equals("CCC")) C12 = field.getValue().toString();
		}
		
		assertEquals ("10.0_BBB5_CCC4_50.0", 
				record.getMeasureValue ("M1").getNumberValue() + "_" + BB3 + "_" + C12 + "_" + 
				record.getMeasureValue("M2").getNumberValue()
				
		);		
	}


	@Test
	public void testSetAggregatedValueWeighted() 
			throws ActionNotPermittedException, WrongFieldTypeException, ServerAlreadyRunningException, LogicCalculationException {
		
		engine.start(core_configuration_file);
		
		//242
		engine.setAggregatedValue ("TEST", "MSET1", "M1", 968, "BBB.B=B1;CCC.C=C4"); 
		//Multiply by 4
		
		assertEquals ((20*4) + (25*4) + 968, 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.AAAA=AAAA1;BBB.BBB=BBB2;CCC.CCC=CCC5").getNumberValue() + 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.AAAA=AAAA7;BBB.BBB=BBB1;CCC.CCC=CCC7").getNumberValue()	+
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "BBB.B=B1;CCC.C=C4").getNumberValue(),0);
	}
	
	@Test
	public void testSetAggregatedValueWeightedRounded() 
			throws ActionNotPermittedException, WrongFieldTypeException, ServerAlreadyRunningException, LogicCalculationException {
		
		engine.start(core_configuration_file);
		
		engine.setAggregatedValue ("TEST", "MSET1", "M1", 
				LogicType.RoundedProportional, 1115, "BBB.B=B1;CCC.C=C4"); 
		
		assertEquals (1115, 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "BBB.B=B1;CCC.C=C4").getNumberValue(),0);
	}
	
	@Test
	public void testSetAggregatedValueEqual() throws ActionNotPermittedException, WrongFieldTypeException, ServerAlreadyRunningException, LogicCalculationException {
		
		engine.start(core_configuration_file);
		
		engine.setAggregatedValue ("TEST", "MSET1", "M1", LogicType.Equal, 2000, "AAAA.A=A2;BBB.BB=BB1"); 
		
		assertEquals (500 + 2000, 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.AAAA=AAAA6;BBB.BBB=BBB2;CCC.CCC=CCC6").getNumberValue() + 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.A=A2;BBB.BB=BB1").getNumberValue(),0);
	}
	
	@Test
	public void testSetAggregatedValueEqualRounded() throws ActionNotPermittedException, WrongFieldTypeException, ServerAlreadyRunningException, LogicCalculationException {
		
		engine.start(core_configuration_file);
		
		engine.setAggregatedValue ("TEST", "MSET1", "M1", LogicType.Equal, 2005, "AAAA.A=A2;BBB.BB=BB1"); 
		
		assertEquals (2005, 
 
			engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.A=A2;BBB.BB=BB1").getNumberValue(),0);
	}

	@Test
	public void testStart() throws ServerAlreadyRunningException {
		
		int status = engine.start(core_configuration_file);
		
		assertEquals(0, status);		
	}
	
	@Test(expected=ServerAlreadyRunningException.class)
	public void testAlreadyStart () throws ServerAlreadyRunningException {
		
		
		engine.start(core_configuration_file);
		
		engine.start(core_configuration_file);		
	}

	
	@Test
	public void testGetRecordsByAggregation () 
			throws ActionNotPermittedException, WrongFieldTypeException, ServerAlreadyRunningException {
		/*
		SELECT * 
		FROM MEASURE_SET_MSET1 M 
		join DIM_AAAA D_A on D_A.AAAA = M.AAAA 
		join DIM_BBB D_B on D_B.BBB = M.BBB
		join DIM_CCC D_C on D_C.CCC = M.CCC
		WHERE D_A.A = 'A2' AND D_B.BB = 'BB1'
				
		*/
		engine.start(core_configuration_file);
		
		Collection <PlanningRecord> records = 
				engine.getRecordsByAggregation ("TEST", "MSET1", "AAAA.A=A2;BBB.BB=BB1");
		
		double tot_M1 = 0;
		double tot_M2 = 0;
		
		for (PlanningRecord record : records) {
			
			PlanningField M1 = record.getMeasureValue ("M1");
			PlanningField M2 = record.getMeasureValue ("M2");
			
			tot_M1 += M1.getNumberValue();
			tot_M2 += M2.getNumberValue();
		}
		
		//for (PlanningEngine)
		
		assertEquals (349 + 4, records.size() + tot_M1 + tot_M2,0);
		
		engine.stop();
	}
	
	@Test
	public void testGetAggregation () throws ActionNotPermittedException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		/*
		SELECT SUM(VALUE_M1)
		FROM MEASURE_SET_MSET1 M 
		join DIM_AAAA D_A on D_A.AAAA = M.AAAA 
		join DIM_BBB D_B on D_B.BBB = M.BBB
		join DIM_CCC D_C on D_C.CCC = M.CCC
		WHERE D_A.A = 'A2' AND D_B.BB = 'BB1'
				
		*/
		NumberField field = 
				(NumberField) engine.getAggregatedValue ("TEST", "MSET1", "M1", "AAAA.A=A2;BBB.BB=BB1");
			
		
		//for (PlanningEngine)
		
		assertEquals (102, field.getNumberValue(),0);
		
		engine.stop ();
	}
	
	@Test
	public void testGetPlanningSet () 
			throws ActionNotPermittedException, ServerAlreadyRunningException, UniverseNotExistException {
		
		engine.start(core_configuration_file);
				
		/*
		SELECT D_A.AA, D_C.CC, SUM(VALUE_M1) as M1, SUM(VALUE_M2) as M2
		FROM MEASURE_SET_MSET1 M 
		join DIM_AAAA D_A on D_A.AAAA = M.AAAA 
		join DIM_BBB D_B on D_B.BBB = M.BBB
		join DIM_CCC D_C on D_C.CCC = M.CCC
		WHERE D_B.BB = 'BB1' and D_A.A = 'A2'
		GROUP BY D_A.AA, D_C.CC
		*/
		
		PlanningSet data_set = 
				engine.getDataSet ("TEST", "MSET1", "M1;M2", "AAAA.A=A2;BBB.BB=BB1", "AAAA.AA;CCC.CC");
			
		Collection <PlanningRecord> dataset = data_set.getData();
		
		String AA = "";
		String CC = "";
		double M1 = 0;
		double M2 = 0;
		
		for (PlanningRecord record: dataset) {
			
			AA += record.getAttributeValue ("AA").getValue() + ":";
			CC += record.getAttributeValue ("CC").getValue() + ":";
			
			M1 += record.getMeasureValue ("M1").getNumberValue();
			M2 += record.getMeasureValue ("M2").getNumberValue();
		}
		
		assertEquals ("4_102.0_247.0_true_true_true_true_true_true", dataset.size() + "_" + 
				M1 + "_" + 
				M2 + "_" + 
				AA.contains("AA3") + "_" +
				AA.contains("AA2") + "_" +
				CC.contains("CC9") + "_" +
				CC.contains("CC3") + "_" +
				CC.contains("CC1") + "_" +
				CC.contains("CC8"));
		
		engine.stop ();
	}


	@Test
	public void testStop() throws ServerAlreadyRunningException {
				
		engine.start(core_configuration_file);
		
		assertEquals(0, engine.stop());
		
	}

	@Test
	public void testGetStatus() throws ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		assertEquals (engine.STARTED, engine.getStatus());		
		
		engine.stop ();
	}


	@Test
	public void testGetMasureSetRecordsNumber () 
			throws UniverseNotExistException, ActionNotPermittedException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		assertEquals(10, 
				engine.getMasureSetRecordsNumber ("TEST", "MSET1"));
	}

	@Test
	public void testGetMeasureSetsMeasureNames () 
			throws UniverseNotExistException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		Collection <String> m_names = 
				engine.getMeasureSetsMeasureNames ("TEST", "MSET1");
		
		String measures = "";
		
		for (String m_name : m_names) {
			
			measures += m_name;
		}
		
		assertTrue(measures.contains("M1") && measures.contains("M2"));
	}

	@Test
	public void testGetPlanningDimensions() throws UniverseNotExistException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		Collection <String> d_names = 
				engine.getPlanningDimensions ("TEST");
		
		String dimensions = "";
		
		for (String d_name : d_names) {
			
			dimensions += d_name;
		}
		
		assertTrue(
			dimensions.contains("AAAA") && 
			dimensions.contains("BBB") &&
			dimensions.contains("CCC"));
		
		engine.stop ();
	}
	
	@Test
	public void testGetPlanningDimensionRelationship () 
			throws UniverseNotExistException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		Collection <String> r_names = 
				engine.getPlanningDimensionRelationship ("TEST", "AAAA", "AAAA4");
		
		String rels = "";
		
		for (String r_name : r_names) {
			
			rels += r_name;
		}
		
		assertTrue(
			rels.contains("A=A1") && 
			rels.contains("AA=AA2") &&
			rels.contains("AAA=AAA1"));
		
		engine.stop();
	}
	
	@Test
	public void testGetAllDimensionRelationships () 
			throws UniverseNotExistException, ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		Map<String, Collection <String>> rels = 
				engine.getAllDimensionRelationships ("TEST", "AAAA");
		
		//'AAAA6','A2','AA2','AAA1'
		
		Collection <String> rel = rels.get ("AAAA6");
		
		String attrs = "";
		
		for (String attr : rel) {
			
			attrs += attr + ";";
		}
		
		assertTrue(
				attrs.contains("A2;") && 
				attrs.contains("AA2;") &&
				attrs.contains("AAA1;"));
		
		engine.stop();
	}


	@Test
	public void testGetMeasureSetsNumber () throws ServerAlreadyRunningException {
		
		engine.start(core_configuration_file);
		
		assertEquals(1, 
			engine.getMeasureSetsNumber ("TEST"));
	}

}
