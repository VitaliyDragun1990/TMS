$(function() {

	var init = function() {
		$('#sidebarCollapse').on('click', toggleSidebar);
		$('#dateTimePicker').datetimepicker(getInitPickerState());
		
		$('#enrollmentDate').datetimepicker({
			viewMode : 'days',
			format : datePattern
		});
		$('#dateHired').datetimepicker({
			viewMode : 'days',
			format : datePattern
		});
		$('#startTime').datetimepicker({
			viewMode : 'days',
			format : dateTimePattern,
            icons: {
                time: "far fa-clock",
                date: "far fa-calendar-alt",
                up: "fas fa-arrow-up",
                down: "fas fa-arrow-down"
            }
		});
		$('#dailySearch').on('click', dailySearchPicker);
		$('#monthlySearch').on('click', monthlySearchPicker);
		$('#searchTmBtn').on('click', searchTimetables);
		
		$('#deleteDialogBtn').on('click', showDeleteDialog);
		$('#confirmDeleteBtn').on('click', hideDeleteDialog);
		
		$('#updateDialogBtn').on('click', showUpdateDialog);
		$('#confirmUpdateBtn').on('click', hideUpdateDialog);
	};
	
	var getInitPickerState = function() {
		var initState =  {
				viewMode : 'days',
				format : datePattern
		};
		if($('#monthlySearch').is(':checked')) {
			initState.viewMode = 'months';
			initState.format = 'MMMM';
		}
		return initState;
	}
	
	var hideDeleteDialog = function() {
		var targetUrl = $('#deleteDialog').attr('data-target-url');
		var targetId = $('#deleteDialog').attr('data-target-id');
		$('#deleteDialog').modal('hide');
		postForm(targetUrl, 'id', targetId);
	}
	
	var hideUpdateDialog = function() {
		var targetFormId = $('#updateDialog').attr('data-form-id');
		$('#updateDialog').modal('hide');
		$('#' + targetFormId).submit();
	}
	
	var showDeleteDialog = function() {
		$('#deleteDialog').modal();
	}
	
	var showUpdateDialog = function() {
		$('#updateDialog').modal();
	}
	
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
		var csrfTokenValue = $('#deleteDialog').attr('data-csrf-token');
		
		var form = $('<form>', {
			id: 'postForm',	// arbitrary here
			action: url,
			method: 'POST'
		});
		
		var tokenInput = $('<input>', {
			name: '_csrf',
			value: csrfTokenValue,
			type: 'hidden'
		});
		var input = $('<input>', {
			name: fieldName,
			value: fieldValue,
			type: 'hidden'
		});
		
		form.append(input);
		form.append(tokenInput);
		
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
			$('#dateTimePicker').data("DateTimePicker").viewMode('days');
		}
	}
	
	var monthlySearchPicker = function() {
		if ($(this).is(':checked')) {
			// clear date picker field
			$('#dateTimePicker').data("DateTimePicker").clear();
			// set month format
			$('#dateTimePicker').data("DateTimePicker").format('MMMM');
			$('#dateTimePicker').data("DateTimePicker").viewMode('months');
		}
	}


	var toggleSidebar = function() {
		$('#sidebar').toggleClass('active');
	};

	init();
});
