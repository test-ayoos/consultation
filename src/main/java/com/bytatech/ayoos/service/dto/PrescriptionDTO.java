package com.bytatech.ayoos.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Prescription entity.
 */
public class PrescriptionDTO implements Serializable {

    private Long id;

    private String prescriptionDMSURL;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrescriptionDMSURL() {
        return prescriptionDMSURL;
    }

    public void setPrescriptionDMSURL(String prescriptionDMSURL) {
        this.prescriptionDMSURL = prescriptionDMSURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PrescriptionDTO prescriptionDTO = (PrescriptionDTO) o;
        if (prescriptionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), prescriptionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PrescriptionDTO{" +
            "id=" + getId() +
            ", prescriptionDMSURL='" + getPrescriptionDMSURL() + "'" +
            "}";
    }
}
