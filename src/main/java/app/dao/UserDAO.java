package app.dao;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.Login;
import app.model.User;

public class UserDAO  {
	public Boolean validateUser(Login login) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("username", login.getUsername());
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

	public List<User> getAll() {
		 List<User> users = new ArrayList<User>();
		 
		 try {
			//call mongoDb
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("User"); //Collection
			DBCursor cursor = coll.find(); //Find All
			while (cursor.hasNext()) {
				System.out.println("Record is found");
				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				
				User q = gson.fromJson(dbobj.toString(), User.class);
				
				User itemUser = new User();
				itemUser.setId(q.getId());
				itemUser.setName(q.getName());
				itemUser.setEmail(q.getEmail());
				itemUser.setRole(q.getRole());
				itemUser.setUsername(q.getUsername());
				users.add(itemUser);
				System.out.println("Record is found");
			}
			return users;
		 }catch (Exception e) {
			 System.out.println("Record is not found");
		 }
		return null;
	}
}
