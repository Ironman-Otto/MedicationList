package model;

import android.graphics.Bitmap;

import utility.StringUtil;

/**
 * Created by Ironman on 4/18/2017.
 * This class contains physician information
 */

public class Physician {

    private int primaryKey;
    private String prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String specialty;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipcode;
    private String phone;
    private String fax;
    private String email;
    private Bitmap physcianImage;

    public Physician() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getPhyscianImage() {
        return physcianImage;
    }

    public void setPhyscianImage(Bitmap physcianImage) {
        this.physcianImage = physcianImage;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        String fullName = "";

        if (StringUtil.isNotNullEmptyBlank(prefix)){
            fullName = prefix.trim();
        }

        if (StringUtil.isNotNullEmptyBlank(firstName)){
            fullName = fullName + " " + firstName.trim();
        }

        if (StringUtil.isNotNullEmptyBlank(middleName)) {
            fullName =  fullName + " " + middleName.trim();
        }

        if (StringUtil.isNotNullEmptyBlank(lastName)) {
            fullName = fullName + " " + lastName.trim();
        }

        if (StringUtil.isNotNullEmptyBlank(suffix)) {
            fullName = fullName + " " + suffix.trim();
        }

        return fullName.trim();
    }
}
