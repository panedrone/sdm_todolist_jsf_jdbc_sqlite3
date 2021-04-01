package com.sqldalmaker.todolist;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * JSFPhaseListener - JSF hook which will be called at jsf processing lifecycle.
 * Verify user session and redirect to login page if needed.
 */
public class JSFPhaseListener implements PhaseListener {

    public void afterPhase(PhaseEvent phaseEvent) {
    }

    public void beforePhase(PhaseEvent phaseEvent) {
//        FacesContext facesContext = phaseEvent.getFacesContext();
//        String s = "null";
//        if (facesContext.getViewRoot() != null) {
//            UIViewRoot viewRoot = facesContext.getViewRoot();
//            s = viewRoot.getViewId();
//        }
//        logger.info("BEFORE phase : \"" + phaseEvent.getPhaseId() + "\" id :\"" + s + "\"");
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }


}