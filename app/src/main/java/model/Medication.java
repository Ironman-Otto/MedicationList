package model;

import android.graphics.Bitmap;

/**
 * Created by Ironman on 4/10/2017.
 * @author Otto L. Lecuons
 * @version 1.0 initial creation
 * @version 1.1 adding brand name, mfg, instructions for taking medication
 * This class contains medication specific information
 *
 */

public class Medication {
    private int primaryKey;
    private String name;
    private String route;
    private String form;
    private String strength;
    private String quantity;
    private String physicianName;
    private String familyMemberName;
    private String pharmacy;
    private String prescriptionNumber;
    private String note;
    private int familyMemberPid;
    private int physicianPid;
    private Bitmap medicationImage;
    private Bitmap prescrLabelImage;
    private String brandName;
    private String manufacturer;
    private String takeInstruct;

    public Medication(){}

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public void setPhysicianName(String physician) {
        this.physicianName = physician;
    }

    public String getFamilyMemberName() {
        return familyMemberName;
    }

    public void setFamilyMemberName(String familyMemberName) {
        this.familyMemberName = familyMemberName;
    }

    public String getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getFamilyMemberPid() {
        return familyMemberPid;
    }

    public void setFamilyMemberPid(int familyMemberPid) {
        this.familyMemberPid = familyMemberPid;
    }

    public int getPhysicianPid() {
        return physicianPid;
    }

    public void setPhysicianPid(int physicianPid) {
        this.physicianPid = physicianPid;
    }

    public Bitmap getMedicationImage() {
        return medicationImage;
    }

    public void setMedicationImage(Bitmap medicationImage) {
        this.medicationImage = medicationImage;
    }

    public Bitmap getPrescrLabelImage() {
        return prescrLabelImage;
    }

    public void setPrescrLabelImage(Bitmap prescrLabelImage) {
        this.prescrLabelImage = prescrLabelImage;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTakeInstruct() {
        return takeInstruct;
    }

    public void setTakeInstruct(String takeInstruct) {
        this.takeInstruct = takeInstruct;
    }
}
