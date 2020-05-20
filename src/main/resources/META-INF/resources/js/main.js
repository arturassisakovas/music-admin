var locale = PrimeFaces.settings.locale;

var CustomValidation = {
		locales: ['lt_LT']
};

$( document ).ready(function() {
	var text = $("#caps-lock-on");
	$("#password, #username").on("keyup", function(event)  {
	    var keyUpEvent = event.originalEvent;
		if (typeof keyUpEvent.getModifierState != 'undefined' &&
            keyUpEvent.getModifierState("CapsLock")) {
			text.show();
		} else {
			text.hide();
		}
	});
	changeSaveCancelIcons();
});

function changeSaveCancelIcons() {
	var inplaceSaveBtn = $(".ui-button-icon-left.ui-icon.ui-c.ui-icon-check");
	inplaceSaveBtn.removeClass("ui-icon-check");
	inplaceSaveBtn.addClass("fa fa-check");
	inplaceSaveBtn.before().css("color", "white");

	var inplaceCancelBtn = $(".ui-button-icon-left.ui-icon.ui-c.ui-icon-close");
	inplaceCancelBtn.removeClass("ui-icon-close");
	inplaceCancelBtn.addClass("fa fa-times");
	inplaceCancelBtn.before().css("color", "white");
}

function scrollToTop() {
	$('html, body').animate({
        scrollTop: $('body').offset().top
    }, 500);
}

function showWorkingHours(animation) {
	if ($('.workingHours').length) { 
		$('.trackPeriodDialog').css('width', '800px');
	} else {
		$('.trackPeriodDialog').css('width', '500px');
	}
	
	if (animation) {
		setTimeout(
		  function() 
		  {
			  $('.workingHours').fadeIn().addClass('inline-block');
		  }, 1000);
	} else {
		$('.workingHours').addClass('inline-block');
	}
}

function prepareSessionExpiredModal() {
		PF('sessionExpiredModal').show();
		setTimeout(function () {
			location.replace('/');
		}, 10000);
}

var countdownId;

function prepareSessionCountdown(startDate, endDate) {
	var days, hours, minutes, seconds;
  
	startDate = new Date(startDate).getTime();
	endDate = new Date(endDate).getTime();
  
	if (isNaN(endDate) || isNaN(startDate)) {
		return;
	}
	
	clearInterval(countdownId);
	
	countdownId = setInterval(calculate, 1000);
  
	function calculate() {
		
		startDate += 1000;
	
		var timeRemaining = parseInt((endDate - startDate) / 1000);
	
		if (timeRemaining >= 0) {
			days = parseInt(timeRemaining / 86400);
			timeRemaining = (timeRemaining % 86400);
  
			hours = parseInt(timeRemaining / 3600);
			timeRemaining = (timeRemaining % 3600);
  
			minutes = parseInt(timeRemaining / 60);
			timeRemaining = (timeRemaining % 60);
  
			seconds = parseInt(timeRemaining);
	  
			$('.timer #minutes').html(("0" + minutes).slice(-2));
			$('.timer #seconds').html(("0" + seconds).slice(-2));
		} else {
			prepareSessionExpiredModal();
			clearInterval(countdownId);
		}
  }
}

function disableSubmitButton() {
	PF('submit').disable();
}

$(document).on("click", ".health-problems-block .ui-inplace-cancel ", function() {
	$('.edit-health-problems-btn').removeClass('hidden');
});

function base64ToArrayBuffer(data) {
	var binaryString = window.atob(data);
	var binaryLen = binaryString.length;
	var bytes = new Uint8Array(binaryLen);
	for (var i = 0; i < binaryLen; i++) {
		var ascii = binaryString.charCodeAt(i);
		bytes[i] = ascii;
	}
	return bytes;
};

var KEY_ENTER = 13;
var KEY_ESCAPE = 27;

function onInplaceEditorKeyDown(event, widgetVar) {
	switch (event.keyCode) {
		case KEY_ESCAPE:
			event.preventDefault();
			PF(widgetVar).cancel();
			break;
		case KEY_ENTER:
			event.preventDefault();
			PF(widgetVar).save();
			break;
	}
}

function hideInplaceEditorTrigger() {
	changeSaveCancelIcons();
	$('.inplace-editor-trigger').hide();
}

function showInplaceEditorTrigger() {
	changeSaveCancelIcons();
	$('.inplace-editor-trigger').show();
}
