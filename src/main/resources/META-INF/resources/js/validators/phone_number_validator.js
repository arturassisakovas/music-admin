var locale = PrimeFaces.settings.locale;
PrimeFaces.validator['custom.phoneNumberValidator'] = {
	    		 
    pattern: /^[+]?[\d]+$/,
 
    validate: function(element, value) {
	    if(!this.pattern.test(value)) {
	        throw {
	            summary: 'Validation Error',
	            detail: CustomValidation.locales[locale].messages['phoneNumber.not.valid']
	        }
	    }
    }
};