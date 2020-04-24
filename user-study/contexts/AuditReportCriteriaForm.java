/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */



import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import net.rrm.ehour.report.criteria.ReportCriteria;
import net.rrm.ehour.ui.audit.AuditConstants;
import net.rrm.ehour.ui.common.event.AjaxEventType;

public class AuditReportCriteriaForm extends Form<ReportCriteria> {
    private static final long serialVersionUID = -4033279032707727816L;

    public enum Events implements AjaxEventType {
        FORM_SUBMIT
    }

    public AuditReportCriteriaForm(String id, IModel<ReportCriteria> model) {
        super(id, model);

        addDates(model);

        AjaxButton submitButton = new AjaxButton(AuditConstants.PATH_FORM_SUBMIT, this) {
            private static final long serialVersionUID = -627058322154455051L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
}}}}