package com.lrc.missionO2.validators;


import com.lrc.missionO2.entity.Address;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddressValidator implements ConstraintValidator<ValidAddress, Address> {

    @Override
    public void initialize(ValidAddress constraintAnnotation) {
        // This method is called during initialization, you can perform setup here if needed.
    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext context) {
        if (address == null) {
            return false;
        }
        return isEmpty(address.getAddressLine1()) && isEmpty(address.getAddressLine2()) && isEmpty(address.getPinCode()) && isEmpty(address.getState()) && isEmpty(address.getTaluk()) && isEmpty(address.getDistrict()) && isEmpty(address.getCountry());
    }

    private boolean isEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
