package webapp.services;

import webapp.model.Dataset;

import java.util.List;

public interface DatasetService {

    String saveDataset(Dataset dataset);

    List<Dataset> getAllDatasets();

    Dataset findDatasetByID(long id);

}
