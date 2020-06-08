package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.AppointmentType;

import java.util.List;

public interface GetAppointmentTypeListCallback extends BasicCallback {

    void onCompleted(List<AppointmentType> appointmentTypeList);
}
