package org.zkoss.mongodb.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.zkoss.mongodb.dao.CuratorDAO;
import org.zkoss.mongodb.model.AnswersList;
import org.zkoss.mongodb.model.Datasets;
import org.zkoss.mongodb.model.QueryList;
import org.zkoss.mongodb.model.Question;
import org.zkoss.mongodb.model.Questions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;


import org.zkoss.sparql.Query;

public class CuratorController extends GenericForwardComposer {
private static final long serialVersionUID = 4084521215385235831L;
	
	Listbox curator;
	Textbox question;
	Textbox answertype;
	Textbox aggregation;
	Textbox onlydbo;
	Textbox hybrid;
	Textbox strquestion;
	Textbox keyword;
	Textbox language;
	Textbox id;
	Textbox datasetversion;
	
	CuratorDAO curatorDao = new CuratorDAO();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		curator.setModel(new ListModelList(curatorDao.findAll()));
		curator.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
			}

			@Override
			public void render(Listitem item, Object data, int index) throws Exception {
				Questions datasets = (Questions) data;
				item.setValue(datasets);
				String vars = datasets.getAnswers().get(0).getHead().getVars().get(0).toString();
				String ansString = "";
				String strUri = "uri";
				String strC = "c";
				String strString = "string";
				String strDate = "date";
				if (vars.equals(strUri)) {
					ansString = datasets.getAnswers().get(0).getResults().getBindings().get(0).getUri().getValue().toString();				
				}
				if (vars.equals(strC)) {
					ansString = datasets.getAnswers().get(0).getResults().getBindings().get(0).getC().getValue().toString();
			    }
				if (vars.equals(strString)) {
					ansString = datasets.getAnswers().get(0).getResults().getBindings().get(0).getString().getValue().toString();
				}
				if (vars.equals(strDate)) {
					ansString = datasets.getAnswers().get(0).getResults().getBindings().get(0).getDate().getValue().toString();
				}
				String strQuery = datasets.getQuery().getSparql();
				Query querySparql = new Query();
				String resultAnswer = querySparql.getQuery(strQuery);
				
				new Listcell(datasets.getQuery().getSparql()).setParent(item);	
				new Listcell("").setParent(item);
				new Listcell(resultAnswer).setParent(item);
				new Listcell(ansString).setParent(item);
				new Listcell("").setParent(item);
				
			}
		
		});
	}
	public void onSelect$curator(SelectEvent evt) {
		Questions datasets = (Questions) curator.getSelectedItem().getValue();
		answertype.setValue(datasets.getAnswertype());
		aggregation.setValue(datasets.getAggregation());
		onlydbo.setValue(datasets.getOnlydbo());
		hybrid.setValue(datasets.getHybrid());
		id.setValue(datasets.getId());
		strquestion.setValue(datasets.getQuestion().get(0).getString());
		keyword.setValue(datasets.getQuestion().get(0).getKeywords());
		language.setValue(datasets.getQuestion().get(0).getLanguage());
		datasetversion.setValue("");
		
	}
	public void onClick$update() {
		Questions datasets = (Questions) curator.getSelectedItem().getValue();
		datasets.setAggregation(aggregation.getValue());
		datasets.setAnswertype(answertype.getValue());
		datasets.setOnlydbo(onlydbo.getValue());
		datasets.setHybrid(hybrid.getValue());
		
		Question questionObj = new Question();
		questionObj.setLanguage(language.getValue());
		questionObj.setKeywords(keyword.getValue());
		questionObj.setString(strquestion.getValue());
		
		@SuppressWarnings("unchecked")
		List<Question> questionList = new ArrayList<Question>();
		questionList.add(questionObj);
		datasets.setQuestion(questionList);
		
		datasets.setQuery(datasets.getQuery());
		datasets.setAnswers(datasets.getAnswers());
		
		try {
			curatorDao.update(datasets);
			curator.setModel(new ListModelList(curatorDao.findAll()));
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void onClick$delete() {
		Questions datasets = (Questions) curator.getSelectedItem().getValue();
		try {
			curatorDao.delete(datasets);
			curator.setModel(new ListModelList(curatorDao.findAll()));
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
