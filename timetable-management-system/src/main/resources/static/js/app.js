$(function() {
	
	var init = function() {
		$('#sidebarCollapse').on('click', toggleSidebar);
	};
	
	var toggleSidebar = function() {
		$('#sidebar').toggleClass('active');
	};
	
	init();
});
