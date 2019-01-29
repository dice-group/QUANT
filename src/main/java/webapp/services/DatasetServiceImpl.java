package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.DatasetRepository;
import webapp.model.Dataset;

import java.util.List;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    DatasetRepository datasetRepository;

    @Override
    public String saveDataset (Dataset dataset) {
        datasetRepository.save(dataset);
        return "Dataset successfully saved";
    }

    @Override
    public List<Dataset> getAllDatasets() {return datasetRepository.findAll();}

    @Override
    public Dataset findDatasetByID(long id) {return datasetRepository.findDatasetById(id);}
}
