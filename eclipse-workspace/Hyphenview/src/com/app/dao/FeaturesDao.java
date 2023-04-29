package com.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.app.constant.QueryConstants;
import com.app.object.Features;

public class FeaturesDao {
	
	public List<Features> fetchFeatures(){
		List<Features> featuresList = null;
		String query = null;
		
		Connection conn = null;
		try{
			EraviewDBconnection dBconnection = new EraviewDBconnection();
			conn = dBconnection.getLocalDBConnection();
			query = QueryConstants.fetch_all_features;
			PreparedStatement stmnt = conn.prepareStatement(query);
			ResultSet res = stmnt.executeQuery();
			
			while(res.next())
			{
				if(featuresList == null){
					featuresList = new ArrayList<Features>();
				}
				Features features = new Features();
				features.setFeatureId(res.getString(1));
				features.setFeatureName(res.getString(2));
				features.setFeatureDescription(res.getString(3));
				features.setFeatureCost(res.getInt(4));
				featuresList.add(features);
			}
			
		 }catch (Exception e) {
			e.printStackTrace();
		}
		
		return featuresList;
	}

}
