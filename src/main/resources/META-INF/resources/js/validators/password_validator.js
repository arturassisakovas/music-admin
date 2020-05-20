var locale = PrimeFaces.settings.locale;
PrimeFaces.validator['custom.passwordValidator'] = {
	 
    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?!.*[\'\"\<\>\|\\\/\{\}\[\]\:\;\,\.\?\s]).*$/,
 
    validate: function(element, value) {
        if(!this.pattern.test(value)) {
            throw {
                summary: 'Validation Error',
                detail: CustomValidation.locales[locale].messages['password.not.valid']
            }
        }
    }
};