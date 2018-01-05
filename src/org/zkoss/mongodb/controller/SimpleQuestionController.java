package org.zkoss.mongodb.controller;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.zkoss.mongodb.dao.QuestionDAO;
import org.zkoss.mongodb.model.Question;
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

public class SimpleQuestionController extends GenericForwardComposer {
private static final long serialVersionUID = 4084521215385235831L;
	
	Listbox questions;
	Textbox question;
	Textbox answertype;
	Textbox aggregation;
	Textbox onlydbo;
	Textbox hybrid;
	Textbox strquestion;
	Textbox keyword;
	Textbox language;
	Textbox id;
	
	QuestionDAO questionDao = new QuestionDAO();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		questions.setModel(new ListModelList(questionDao.findAll()));
		questions.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data) throws Exception {
				Question question = (Question) data;
				item.setValue(question);
				new Listcell(question.getSparql()).setParent(item);	
				new Listcell("").setParent(item);
				new Listcell("").setParent(item);
				new Listcell(question.getQuery()).setParent(item);
				new Listcell("").setParent(item);
			}

			@Override
			public void render(Listitem item, Object data, int index) throws Exception {
				Question question = (Question) data;
				item.setValue(question);
				new Listcell(question.getSparql()).setParent(item);	
				new Listcell("").setParent(item);
				new Listcell("").setParent(item);
				new Listcell(question.getQuery()).setParent(item);
				new Listcell("").setParent(item);
				
			}
		});
	}
	public void onSelect$questions(SelectEvent evt) {
		Question questionx = (Question) questions.getSelectedItem().getValue();
		answertype.setValue(questionx.getAnswertype());
		aggregation.setValue(questionx.getAggregation());
		onlydbo.setValue(questionx.getOnlydbo());
		hybrid.setValue(questionx.getHybrid());
		strquestion.setValue(questionx.getStrquestion());
		keyword.setValue(questionx.getKeyword());
		language.setValue(questionx.getLang());
		id.setValue(questionx.getId());
	}

}
