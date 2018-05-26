package app.model;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
	List<DatasetList> datasetVersionList = new ArrayList<DatasetList>();
	  public Dataset() {
	        initData();
	    }
	//initial data dataset version list
	private void initData() {
		datasetVersionList.add(new DatasetList(1, "QALD1_Test_dbpedia"));
		datasetVersionList.add(new DatasetList(2, "QALD1_Train_dbpedia"));
		datasetVersionList.add(new DatasetList(3, "QALD2_Test_dbpedia"));
		datasetVersionList.add(new DatasetList(4, "QALD2_Train_dbpedia"));
		datasetVersionList.add(new DatasetList(5, "QALD3_Test_dbpedia"));
		datasetVersionList.add(new DatasetList(6, "QALD3_Train_dbpedia"));
		/*datasetVersionList.add(new DatasetList(7, "QALD4_Test_Multilingual"));
		datasetVersionList.add(new DatasetList(8, "QALD4_Train_Multilingual"));
		datasetVersionList.add(new DatasetList(9, "QALD5_Test_Multilingual"));
		datasetVersionList.add(new DatasetList(10, "QALD5_Train_Multilingual"));
		datasetVersionList.add(new DatasetList(11, "QALD6_Test_Multilingual"));
		datasetVersionList.add(new DatasetList(12, "QALD6_Train_Multilingual"));
		datasetVersionList.add(new DatasetList(13, "QALD7_Test_Multilingual"));
		datasetVersionList.add(new DatasetList(14, "QALD7_Train_Multilingual"));
		datasetVersionList.add(new DatasetList(15, "QALD8_Test_Multilingual"));
		datasetVersionList.add(new DatasetList(16, "QALD8_Train_Multilingual"));*/
	}
	public List<DatasetList> getDatasetVersionLists() {
		return datasetVersionList;
	}
	public int getSize(){
        return datasetVersionList.size();
    }
}
