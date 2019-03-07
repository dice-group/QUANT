package webapp.controller;

import datahandler.WriteJsonFileFromDataset;
import datahandler.WriteQaldDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.repository.DatasetRepository;
import webapp.repository.QuestionsRepository;
import webapp.repository.TranslationsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;
import webapp.services.TranslationsService;
import webapp.services.UserService;
import java.io.IOException;
import java.util.*;


@Controller
public class DatasetController {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    TranslationsRepository translationsRepository;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    TranslationsService translationsService;

    @Autowired
    UserService userService;

    @Autowired
    WriteQaldDataset w;

    @Autowired
    WriteJsonFileFromDataset downloadGenerator;



    @RequestMapping(value = "/datasetlist", method = RequestMethod.GET)
    public ModelAndView datasetList() {
        ModelAndView model = new ModelAndView("/datasetlist");
        model.addObject("Datasets", datasetService.getAllDatasets());
        model.addObject("Title", "QUANT- Dataset Übersicht");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        return model;
    }




    @RequestMapping(value = "/questionslist/{id}", method = RequestMethod.GET)
    public ModelAndView questionList(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/questionslist");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        List<Questions> qL = questionsService.findByDatasetQuestion_IdAndVersionAndRemoved(id, 0, false);
        List<Questions> finalList = new ArrayList<>();
        for (Questions item: qL)
        {
            Questions finalItem = item;
            List<Questions> qSetList = questionsRepository.findQuestionsByQuestionSetId(item.getQuestionSetId());
            if(qSetList.size()>1) {

                for (Questions x : qSetList) {
                    if (x.getAnotatorUser() == user && x.getVersion() != 0) {
                            finalItem =(x);
                    }
                }
            }
            finalList.add(finalItem);
        }

        model.addObject("Questions", finalList);
        model.addObject("DatasetName", datasetService.findDatasetByID(id).getName());
        model.addObject("Title", "QUANT - Dataset Questions");

        return model;
    }

    @RequestMapping(value = "manageDataset/{id}", method = RequestMethod.GET)
    public ModelAndView manageDataset(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/manageDataset");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        model.addObject("Dataset", datasetService.findDatasetByID(id));
        model.addObject("Questions", questionsService.findAllQuestionsByDatasetQuestion_Id(id));

        return model;
    }

    @RequestMapping(value = "/manageDataset/{id}", method = RequestMethod.POST)
    public String deleteQuestion(@RequestParam("deleteId") long deleteId,
                                 @PathVariable("id") long datasetId,
                                 RedirectAttributes attributes) {
        Questions q = questionsService.findDistinctById(deleteId);
        List<Translations> t = translationsRepository.findByQid(q);
        Questions originalQ = questionsService.findDistinctById(q.getQuestionSetId());
        System.out.println("Question to delete: " + deleteId);

        try {
            if (q.isActiveVersion() || q.isOriginal()) {
                attributes.addFlashAttribute("error", "Deleting a question, that is marked as 'active question' or is a original question, is not allowed!");
                System.out.println("is active or original");
                return "redirect:/manageDataset/" + datasetId;
            } else {
                for (Translations item : t) {

                    translationsRepository.delete(item);
                }

                questionsRepository.delete(q);

                List<Questions> qVersions = questionsService.findQuestionsByDatasetQuestionIdAndQuestionSetId(datasetId, q.getQuestionSetId());
                if(qVersions.size() ==1)
                {
                    originalQ.setAnotated(false);
                    questionsRepository.save(originalQ);
                    System.out.println("SetAnotated to false:" +originalQ.getId());
                }
                attributes.addFlashAttribute("success", "The question was successfully deleted!");
                return "redirect:/manageDataset/" + datasetId;
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "An error occured while deleting the question!");
            return "redirect:/manageDataset/" + datasetId;
        }
    }

    @RequestMapping(path = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> download(@PathVariable ("id") long id) throws IOException {

        // ...
        byte[] file = downloadGenerator.generateJsonFileFromDataset(id);
        ByteArrayResource resource = new ByteArrayResource(file);
        HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=datasetdownload_"+id+".json");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}


