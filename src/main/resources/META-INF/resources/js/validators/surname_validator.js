var locale = PrimeFaces.settings.locale;

PrimeFaces.validator['custom.surnameValidator'] = {
	 
    pattern: /^([^0-9]*)$/,
 
    validate: function(element, value) {
        if(!this.pattern.test(value)) {
            throw {
                summary: 'Validation Error',
                detail: CustomValidation.locales[locale].messages['surname.not.valid']
            }
        }
    }
};