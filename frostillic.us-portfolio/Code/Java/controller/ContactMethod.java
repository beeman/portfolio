package controller;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import frostillicus.controller.BasicXPageController;

public class ContactMethod extends BasicXPageController {
	private static final long serialVersionUID = 9212949750653111577L;

	public String save() {
		model.ContactMethod method = (model.ContactMethod)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), "contactMethod");
		return method.save() ? "xsp-success" : "xsp-failure";
	}
}
