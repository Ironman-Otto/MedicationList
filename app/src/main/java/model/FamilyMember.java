package model;

import android.graphics.Bitmap;

import utility.StringUtil;

/**
 * Created by Ironman on 4/17/2017.
 * This class holds the family member information
 */

public class FamilyMember {

    private int primaryKey;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String prefix;
    private Bitmap familyMemberImage;
    private String birthdate;
    private String gender;
    private String height;
    private String weight;
    private String bloodType;

    public FamilyMember() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Bitmap getFamilyMemberImage() {
        return familyMemberImage;
    }

    public void setFamilyMemberImage(Bitmap familyMemberImage) {
        this.familyMemberImage = familyMemberImage;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
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

        fullName = fullName.trim();

        return fullName.trim();
    }
}
