$(function() {

	var init = function() {
		$('#sidebarCollapse').on('click', toggleSidebar);
		$('#dateTimePicker').datetimepicker({
			viewMode : 'days',
			format : datePattern
		});
		$('#enrollmentDate').datetimepicker({
			viewMode : 'days',
			format : datePattern
		});
		$('#dateHired').datetimepicker({
			viewMode : 'days',
			format : datePattern
		});
		$('#dailySearch').on('click', dailySearchPicker);
		$('#monthlySearch').on('click', monthlySearchPicker);
		$('#searchTmBtn').on('click', searchTimetables);
	};
	
	var searchTimetables = function() {
		var date = $('#dateTimePicker').val();
		var url = $(this).attr('data-base-url') + getDateType();
		
		if (date.trim().length === 0 || !url) {
			$('#invalidDateInput').removeClass('d-none');
		} else {
			$('#invalidDateInput').addClass('d-none');
			getForm(url, 'targetDate', date);
		}
	}
	
	var getDateType = function() {
		if ($('#dailySearch').is(':checked')) {
			return 'day';
		} else {
			return 'month';
		}
	}
	
	var postForm  = function(url, fieldName, fieldValue) {
		var form = $('<form>', {
			id: 'postForm',	// arbitrary here
			action: url,
			method: 'POST'
		});
		var input = $('<input>', {
			name: fieldName,
			value: fieldValue,
			type: 'hidden'
		});
		form.append(input);
		$('body').append(form);
		form.submit();
	}
	
	var getForm  = function(url, fieldName, fieldValue) {
		var form = $('<form>', {
			id: 'getForm',	// arbitrary here
			action: url,
			method: 'GET'
		});
		var input = $('<input>', {
			name: fieldName,
			value: fieldValue,
			type: 'hidden'
		});
		form.append(input);
		$('body').append(form);
		form.submit();
	}
	
	var dailySearchPicker = function() {
		if ($(this).is(':checked')) {
			// clear date picker
			$('#dateTimePicker').data("DateTimePicker").clear();
			// set day format
			$('#dateTimePicker').data("DateTimePicker").format(datePattern);
		}
	}
	
	var monthlySearchPicker = function() {
		if ($(this).is(':checked')) {
			// clear date picker field
			$('#dateTimePicker').data("DateTimePicker").clear();
			// set month format
			$('#dateTimePicker').data("DateTimePicker").format('MMMM');
		}
	}


	var toggleSidebar = function() {
		$('#sidebar').toggleClass('active');
	};

	init();
});
