package app.dao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.DatasetModel;
import app.model.Login;
import app.model.User;

public class UserDAO  {
	
	public ModelAndView isCookieValid(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		return null;
	}
		
	public Boolean validateUser(Login login) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("username", login.getUsername());
		 searchObj.put("password", login.getPassword());
		 try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("User"); //Collection
				DBCursor cursor = coll.find(searchObj); //Find All
				
				while (cursor.hasNext()) {					
					return true;
				}
				
			}catch (Exception e) {}
			return false;
	}
	/**
	 * This method used to get all users
	 * @return List<User>
	 */
	public List<User> getAll() {
		 List<User> users = new ArrayList<User>();
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", 1);
		 try {
			//call mongoDb
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("User"); //Collection
			DBCursor cursor = coll.find().sort(searchObj); //Find All
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				
				User q = gson.fromJson(dbobj.toString(), User.class);
				
				User itemUser = new User();
				itemUser.setId(q.getId());
				itemUser.setName(q.getName());
				itemUser.setEmail(q.getEmail());
				itemUser.setRole(q.getRole());
				itemUser.setUsername(q.getUsername());
				itemUser.setPassword(q.getPassword());
				users.add(itemUser);
				
			}
			return users;
		 }catch (Exception e) {
			 
		 }
		return null;
	}
	/*
	  * This method is used to add User to MongoDB
	  */
	 public void addUser(User user) {
		 try {
			BasicDBObject newDbObj = toBasicDBObject(user);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("User");
			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 } 
	 /*
	  * This method is used to create an object for update or save purpose in MongoDB
	  */
	private BasicDBObject toBasicDBObject(User user) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("id", user.getId());
		newdbobj.put("username", user.getUsername());
		newdbobj.put("password", user.getPassword());
		newdbobj.put("email", user.getEmail());
		newdbobj.put("name", user.getName());
		newdbobj.put("role", user.getRole());
		
		return newdbobj;
	}
	/**
	 * This method is used to update User in MongoBD
	 */
	 public void updateUser(User user) {
		 try {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", user.getId());
			BasicDBObject newDbObj = toBasicDBObject(user);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("User");
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 } 
	 /**
	  * Get last record
	  */
	 public int getUserId() {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", -1);
		 try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("User"); //Collection
				DBCursor cursor = coll.find().sort(searchObj).limit(1); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					
					User q = gson.fromJson(dbobj.toString(), User.class);
					int id = q.getId();
					id++;
					return id;
					
				}
				
			}catch (Exception e) {}
		 return 0;
	 }
	 /**
	  * Delete User
	  */
	 public void deleteUser(int id) {
		 try {
				BasicDBObject searchObj = new BasicDBObject();
				searchObj.put("id", id);
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("User");
				
				coll.remove(searchObj);
			 } catch (Exception e) {
				 
			 }
	 }
	 /**
	  * Get User by Username
	  * @param username
	  * @return
	  */
	 public User getUserByUsername(String username) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("username", username);
		 User itemUser = new User();
		 try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("User"); //Collection
				DBCursor cursor = coll.find(searchObj); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					
					User q = gson.fromJson(dbobj.toString(), User.class);
					
					
					itemUser.setId(q.getId());
					itemUser.setName(q.getName());
					itemUser.setEmail(q.getEmail());
					itemUser.setRole(q.getRole());
					itemUser.setUsername(q.getUsername());
					itemUser.setPassword(q.getPassword());
				}
				
			}catch (Exception e) {}
			return itemUser;
	 }
}
