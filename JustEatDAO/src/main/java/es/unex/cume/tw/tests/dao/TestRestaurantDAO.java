package es.unex.cume.tw.tests.dao;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Restaurant;

import org.junit.Test;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestRestaurantDAO {

	static DBConn dbConn;
	static RestaurantDAO RestaurantDAO;
	static Connection conn;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dbConn = new DBConn();
		conn = dbConn.create();
	    RestaurantDAO = new JDBCRestaurantDAOImpl();
		RestaurantDAO.setConnection(conn);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		dbConn.destroy(conn);
	    
	}

	@Before
	public void setUpBeforeMethod() throws Exception {
	
	}

	@Test
	public void test1BaseData() {
		
		Restaurant restaurant01 = RestaurantDAO.get(0);
		assertEquals(restaurant01.getId(),0);
		assertEquals(restaurant01.getName(),"Burger King");

		Restaurant restaurant02 = RestaurantDAO.get(1);
		assertEquals(restaurant02.getId(),1);
		assertEquals(restaurant02.getName(),"Atrio");
		
		Restaurant restaurant03 = RestaurantDAO.get(2);
		assertEquals(restaurant03.getId(),2);
		assertEquals(restaurant03.getName(),"Big House");
		
		Restaurant restaurant04 = RestaurantDAO.get(3);
		assertEquals(restaurant04.getId(),3);
		assertEquals(restaurant04.getName(),"Ciao ciao");
		
		Restaurant restaurant05 = RestaurantDAO.get(4);
		assertEquals(restaurant05.getId(),4);
		assertEquals(restaurant05.getName(),"Miss Sushi");
		
	}
	
	
	@Test
	public void test2Add(){
		Restaurant restaurant01 = new Restaurant();
		restaurant01.setName("newRestaurant");
		restaurant01.setTelephone("777777777");
		restaurant01.setIdu(0);
		restaurant01.setGradesAverage(15.0);
		restaurant01.setDescription("Descripcion de prueba");
		restaurant01.setCity("Mérida");
		restaurant01.setAvailable(1);
		restaurant01.setContactEmail("info@merida.com");
		
		
		RestaurantDAO.add(restaurant01);
		
		Restaurant restaurant02 = RestaurantDAO.getAllBySearchName("newRestaurant").iterator().next();
		assertEquals(restaurant01.getTelephone(),restaurant02.getTelephone());
		assertEquals(restaurant01.getDescription(),restaurant02.getDescription());
	}
	
	@Test
	public void test3Modify(){
		Restaurant restaurant01 = RestaurantDAO.getAllBySearchName("newRestaurant").iterator().next();
		restaurant01.setName("newRestaurantUpdated");
		RestaurantDAO.update(restaurant01);
		
		Restaurant restaurant02 = RestaurantDAO.getAllBySearchName("newRestaurantUpdated").iterator().next();
		assertEquals(restaurant01.getTelephone(),restaurant02.getTelephone());
		
		RestaurantDAO.getAll();
	}
	
	@Test
	public void test4Delete(){
		
		 Restaurant restaurant01 = RestaurantDAO.getAllBySearchName("newRestaurantUpdated").iterator().next();
		 long idRestaurant= restaurant01.getId();
		 RestaurantDAO.delete(idRestaurant);
		 
		 Restaurant restaurant02 = RestaurantDAO.get(idRestaurant);
		 assertEquals(null,restaurant02);
		 
		RestaurantDAO.getAll();
	}

}
