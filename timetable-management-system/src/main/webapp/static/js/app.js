$(function() {
	
	var init = function() {
		$('#post-button').on('click', submitForm);
		$('#updGroupBtn').on('click', updateStudentGroup);
		$('#rmvGroupBtn').on('click', removeStudentGroup);
		$('#updCourseBtn').on('click', updateStudentCourses);
		$('#rmvStudentBtn').on('click', removeStudent);
	};
	
	var removeStudent = function(event) {
		event.preventDefault();
		
		var studentId = $('#studentForm').attr('data-student-id');
		
        var answer = confirm('Are you sure ?');
        if (answer) {
        	executeStudentRemoving(studentId);
        }
	}
	
	var executeStudentRemoving = function(studentId) {
		$.ajax({
			url: ctx + '/students',
			method: 'POST',
			data: {
				action: 'removeStudent',
				studentId : studentId
			},
			success: function(data) {
				console.log(data);
				window.location.href = ctx + '/students';
			},
			error: function(data) {
				alert(data);
			}
		});
	}
	
	var updateStudentGroup = function(event) {
		event.preventDefault();
		
		var studentId = $('#studentForm').attr('data-student-id');
		var selectedGroup = parseInt($('#groupSelect').find(':selected').val());
		var dest = ctx + '/students';
		
		// Perform update if specified group id is valid
		if (selectedGroup >= 1) {
			var data = {};
			data.studentId = studentId;
			data.action = 'updateGroup';
			data.groupId = selectedGroup;
			
			$.ajax({
				url : dest,
				method: 'POST',
				data: data,
				success : function(data) {
					console.log(data);
				},
				error : function(data) {
					console.log('Student group update failed', data);
					alert(data.message);
				}
			});
		}
	};
	
	var removeStudentGroup = function(event) {
		event.preventDefault();
		
		var currentGroup = parseInt($('#groupSelect').attr('data-current-group'));
		
		// Perform delete if user belongs to some group
		if (!isNaN(currentGroup)) {
			var studentId = $('#studentForm').attr('data-student-id');
			var dest = ctx + '/students';
			
			var data = {};
			data.studentId = studentId;
			data.action = 'removeGroup';
			
			$.ajax({
				url : dest,
				method: 'POST',
				data: data,
				success : function(data) {
					$('#groupSelect').val('-1').change();
					$('#groupSelect').attr('data-current-group', '');
					console.log(data);
				},
				error : function(data) {
					console.log('Student group remove failed', data);
					alert(data.message);
				}
			});
		}
	};
	
	var updateStudentCourses = function(event) {
		event.preventDefault();
		
		var studentId = $('#studentForm').attr('data-student-id');
		var courses = $.map($('#allCourses').find('input:checked'), c => c.value);

		var dest = ctx + '/students';
		var data = {
			action: 'updateCourses',
			studentId: studentId,
			courses: courses
		};
		
		$.ajax({
			url: dest,
			method: 'POST',
			data: data,
			success: function(data) {
				console.log(data);
			},
			error: function(data) {
				alert(data);
			}
		});
	}	
	
	var sendPostRequest = function(event) {
		event.preventDefault();
		var btn = $(this);
		var destination = ctx + btn.attr('data-url');
		var id = btn.attr('data-id');
		
		$.post(destination, {id});
	}
	
	var submitForm = function(event) {
		event.preventDefault();
		var btn = $(this);
		var destination = ctx + btn.attr('data-url');
		var id = btn.attr('data-id');
		
		var form = $('<form>', {
			id: 'postForm',	// arbitrary here
			action: destination,
			method: 'POST'
		});
		var input = $('<input>', {
			name: 'id',
			value: id,
			type: 'hidden'
		});
		form.append(input);
		$('body').append(form);
		form.submit();
	}
	
	init();
});
