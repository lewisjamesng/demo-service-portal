package com.example.demo.portal;

/** A service-centre appointment. All data is synthetic. */
public class Appointment {
    private String appointmentId;
    private String vehicleId;
    private String serviceCentre;
    private String slotStart;
    private String serviceType;
    private String customerNote;

    public Appointment() {
    }

    public Appointment(String appointmentId, String vehicleId, String serviceCentre,
                       String slotStart, String serviceType, String customerNote) {
        this.appointmentId = appointmentId;
        this.vehicleId = vehicleId;
        this.serviceCentre = serviceCentre;
        this.slotStart = slotStart;
        this.serviceType = serviceType;
        this.customerNote = customerNote;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String v) { this.appointmentId = v; }
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String v) { this.vehicleId = v; }
    public String getServiceCentre() { return serviceCentre; }
    public void setServiceCentre(String v) { this.serviceCentre = v; }
    public String getSlotStart() { return slotStart; }
    public void setSlotStart(String v) { this.slotStart = v; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String v) { this.serviceType = v; }
    public String getCustomerNote() { return customerNote; }
    public void setCustomerNote(String v) { this.customerNote = v; }
}
