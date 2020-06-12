package org.fifthgen.evervet.ezyvet.api.callback;

import org.fifthgen.evervet.ezyvet.api.model.AppointmentV2;

import java.util.List;

public interface GetAppointmentV2Callback extends BasicCallback {

    void onCompleted(List<AppointmentV2> appointmentList);
}
