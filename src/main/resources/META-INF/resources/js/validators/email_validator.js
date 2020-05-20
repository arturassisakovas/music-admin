var locale = PrimeFaces.settings.locale;

PrimeFaces.validator['custom.emailValidator'] = {
	 
    pattern: /\S+@\S+/,
 
    validate: function(element, value) {
    	console.log(this);
        if(!this.pattern.test(value)) {
            throw {
                summary: 'Validation Error',
                detail: CustomValidation.locales[locale].messages['email.not.valid']
            }
        }
    }
};