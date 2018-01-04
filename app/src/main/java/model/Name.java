package model;

import utility.StringUtil;

/**
 * Created by Ironman on 4/17/2017.
 * This class is used to hold name information
 */

public class Name {

    private String namePrefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nameSuffix;

    public Name() {
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
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

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    @Override
    public String toString() {
        String fullName = "";

        if (StringUtil.isNotNullEmptyBlank(namePrefix)){
            fullName = namePrefix.trim();
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

        if (StringUtil.isNotNullEmptyBlank(nameSuffix)) {
            fullName = fullName + " " + nameSuffix.trim();
        }

        return fullName;
    }
}
