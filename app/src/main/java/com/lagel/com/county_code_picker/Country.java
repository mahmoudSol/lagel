package com.lagel.com.county_code_picker;

/**
 * <h>Country</h>
 * <p>
 *     The pojo class for metioning country iso code and number.
 * </p>
 * @since 04/12/2017
 */
public class Country
{
    private String code="",name="",fullname="";

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
