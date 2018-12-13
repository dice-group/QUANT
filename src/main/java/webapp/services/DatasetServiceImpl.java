package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.DatasetRepository;
import webapp.model.Dataset;

@Service
public class DatasetServiceImpl implements DataService {

    @Autowired
    DatasetRepository datasetRepository;

    @Override
    public String saveDataset (Dataset dataset) {
        datasetRepository.save(dataset);
        return "Dataset successfully saved";
    }
}
